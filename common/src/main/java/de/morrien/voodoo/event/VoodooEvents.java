package de.morrien.voodoo.event;

import com.mojang.brigadier.CommandDispatcher;
import de.morrien.voodoo.Poppet;
import de.morrien.voodoo.VoodooDamageSource;
import de.morrien.voodoo.command.VoodooCommand;
import de.morrien.voodoo.item.ItemRegistry;
import de.morrien.voodoo.util.BindingUtil;
import de.morrien.voodoo.util.PoppetUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.throwableitemprojectile.AbstractThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.LevelData;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static de.morrien.voodoo.Poppet.PoppetType.*;
import static de.morrien.voodoo.VoodooConfig.COMMON;

public class VoodooEvents {
    public static void onRegisterCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        VoodooCommand.register(dispatcher);
    }

    public static void onTickPlayerTick(ServerPlayer player) {
        checkPotionEffects(player);
        if (player.tickCount % 20 != 0) return;
        checkFoodStatus(player);
    }

   private static void checkPotionEffects(ServerPlayer player) {
        final ArrayList<MobEffectInstance> effects = new ArrayList<>(player.getActiveEffects());
        for (MobEffectInstance potionEffect : effects) {
            if (potionEffect.getEffect().value().getCategory() != MobEffectCategory.HARMFUL) continue;
            if (potionEffect.is(MobEffects.WITHER)) {
                removeWitherEffect(player, potionEffect);
            } else {
                removePotionEffect(player, potionEffect);
            }
        }
    }

    private static void removeWitherEffect(Player player, MobEffectInstance potionEffect) {
        if (!COMMON.witherProtection.enabled.get()) return;
        Poppet witherPoppet = PoppetUtil.getPlayerPoppet((ServerPlayer) player, WITHER_PROTECTION);
        if (witherPoppet == null) return;
        player.removeEffect(potionEffect.getEffect());
        witherPoppet.use();
    }

    private static void removePotionEffect(Player player, MobEffectInstance potionEffect) {
        if (!COMMON.potionProtection.enabled.get()) return;
        int durabilityCost = potionEffect.getAmplifier() + 1;
        while (durabilityCost > 0) {
            Poppet poppet = PoppetUtil.getPlayerPoppet((ServerPlayer) player, POTION_PROTECTION);
            if (poppet == null) break;
            durabilityCost = usePoppet(poppet, durabilityCost);
        }
        if (durabilityCost == potionEffect.getAmplifier() + 1) return;
        player.removeEffect(potionEffect.getEffect());
        if (durabilityCost > 0) {
            final MobEffectInstance effectInstance = new MobEffectInstance(
                    potionEffect.getEffect(),
                    potionEffect.getDuration(),
                    durabilityCost - 1,
                    potionEffect.isAmbient(),
                    potionEffect.isVisible(),
                    potionEffect.showIcon()
            );
            player.addEffect(effectInstance);
        }
    }

    private static void checkFoodStatus(ServerPlayer player) {
        if (player.getFoodData().getFoodLevel() > 10) return;
        final Poppet hungerPoppet = PoppetUtil.getPlayerPoppet(player, HUNGER_PROTECTION);
        if (hungerPoppet == null) return;
        player.addEffect(new MobEffectInstance(MobEffects.SATURATION, 60 * 20, 1));
        usePoppet(hungerPoppet, 1);
    }

    public static void onPlayerInteract(Player player, ItemStack stack, Entity target) {
        if (player.level().isClientSide()) return;
        if (stack.getItem() != ItemRegistry.taglockKit.get()) return;
        if (target instanceof Player targetPlayer) {
            if (BindingUtil.isBound(stack)) return;
            BindingUtil.bind(stack, targetPlayer);
        }
    }

    /**
     * @return true if the death should be canceled
     */
    public static boolean onLivingDeath(LivingEntity entity, DamageSource source) {
        if (entity.level().isClientSide()) return false;
        if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return false;
        if (!COMMON.deathProtection.enabled.get()) return false;
        if (!(entity instanceof final ServerPlayer player)) return false;

        Poppet poppet = PoppetUtil.getPlayerPoppet(player, DEATH_PROTECTION);
        if (poppet != null) {
            poppet.use();
            player.setHealth(player.getMaxHealth() / 2);
            player.removeAllEffects();
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 45 * 20, 1));
            player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 5 * 20, 1));
            player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 40 * 20, 0));
            player.level().broadcastEntityEvent(player, (byte) 35);
            return true;
        }
        return false;
    }

    /**
     * @return The damage that should remain, 0 if the damage is fully blocked
     */
    public static float onLivingAttack(LivingEntity entity, DamageSource damageSource, float amount) {
        if (!(entity instanceof final ServerPlayer player)) return amount;
        if (amount == 0 ||
                player.isInvulnerableTo(player.level(), damageSource) ||
                player.isCreative() ||
                player.isDeadOrDying() ||
                (damageSource.is(DamageTypeTags.IS_FIRE) && player.hasEffect(MobEffects.FIRE_RESISTANCE))
        ) return amount;

        if (tryReflectorPoppet(player, damageSource, amount)) return 0;

        final List<Poppet.PoppetType> validPoppets = getProtectionPoppets(player, damageSource);
        if (validPoppets.isEmpty()) return amount;

        final List<Poppet> poppetsInInventory = PoppetUtil.getPoppetsInInventory(player);
        poppetsInInventory.removeIf(poppet -> !validPoppets.contains(poppet.getItem().getPoppetType()));

        int originalDurabilityCost = getDurabilityCost(damageSource, amount);
        if (originalDurabilityCost == 0) {
            if (poppetsInInventory.isEmpty() && getPoppetsInShelves(player, validPoppets).isEmpty()) return amount;
            doSpecialActions(player, damageSource);
            return 0;
        }
        int durabilityCost = originalDurabilityCost;
        for (int i = 0; i < poppetsInInventory.size() && durabilityCost > 0; i++) {
            Poppet poppet = poppetsInInventory.get(i);
            durabilityCost = usePoppet(poppet, durabilityCost);
        }
        if (durabilityCost > 0) {
            final List<Poppet> poppetsInShelves = getPoppetsInShelves(player, validPoppets);
            for (int i = 0; i < poppetsInShelves.size() && durabilityCost > 0; i++) {
                Poppet poppet = poppetsInShelves.get(i);
                durabilityCost = usePoppet(poppet, durabilityCost);
            }
        }
        if (durabilityCost != originalDurabilityCost) {
            doSpecialActions(player, damageSource);
            float percentage = ((float) durabilityCost) / ((float) originalDurabilityCost);
            return amount * percentage;
        }
        return amount;
    }

    private static List<Poppet> getPoppetsInShelves(ServerPlayer player, List<Poppet.PoppetType> validPoppets) {
        final List<Poppet> poppetsInShelves = PoppetUtil.getPoppetsInShelves(player);
        poppetsInShelves.removeIf(poppet -> !validPoppets.contains(poppet.getItem().getPoppetType()));
        return poppetsInShelves;
    }

    private static boolean tryReflectorPoppet(ServerPlayer player, DamageSource damageSource, float amount) {
        final Entity sourceEntity = damageSource.getEntity();
        if (sourceEntity == null) return false;
        if (player == sourceEntity) return false;
        final Poppet reflectorPoppet = PoppetUtil.getPlayerPoppet(player, REFLECTOR);
        if (reflectorPoppet == null) return false;
        sourceEntity.hurtServer(player.level(), damageSource, amount);
        reflectorPoppet.use();
        return true;
    }

    private static int usePoppet(Poppet poppet, int durabilityCost) {
        final int currentDamage = poppet.getStack().getDamageValue();
        final int maxDamage = Math.max(1, poppet.getStack().getMaxDamage());
        final int remaining = maxDamage - currentDamage;
        if (remaining < durabilityCost) {
            poppet.use(remaining);
            return durabilityCost - remaining;
        } else {
            poppet.use(durabilityCost);
            return 0;
        }
    }

    private static int getDurabilityCost(DamageSource damageSource, float amount) {
        if (damageSource.is(VoodooDamageSource.VOODOO) && COMMON.voodooProtection.enabled.get())
            return 1;
        if ((damageSource.getDirectEntity() instanceof AbstractThrownPotion || damageSource.getDirectEntity() instanceof AreaEffectCloud) && COMMON.potionProtection.enabled.get())
            return Math.max(0, (int) (Math.log(amount / 6) / Math.log(2)) + 1);
        if (damageSource.is(DamageTypes.FALL) && COMMON.fallProtection.enabled.get())
            return (int) Math.max(0, Math.min(amount, Math.ceil(Math.log(amount) * 3)));
        if (damageSource.is(DamageTypeTags.IS_PROJECTILE) && COMMON.projectileProtection.enabled.get())
            return 1;
        if (damageSource.is(DamageTypeTags.IS_FIRE) && COMMON.fireProtection.enabled.get())
            return 1;
        if (damageSource.is(DamageTypeTags.IS_EXPLOSION) && COMMON.explosionProtection.enabled.get())
            return 1;
        if (damageSource.is(DamageTypes.DROWN) && COMMON.waterProtection.enabled.get())
            return 1;
        if (damageSource.is(DamageTypes.STARVE) && COMMON.hungerProtection.enabled.get())
            return 1;
        if (damageSource.is(DamageTypes.FELL_OUT_OF_WORLD) && COMMON.voidProtection.enabled.get())
            return 1;
        return 0;
    }

    private static void doSpecialActions(ServerPlayer player, DamageSource damageSource) {
        if (damageSource.is(DamageTypeTags.IS_FIRE)) {
            player.clearFire();
            player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 10 * 20, 0));
        }

        if (damageSource.is(DamageTypes.DROWN) && COMMON.waterProtection.enabled.get()) {
            player.setAirSupply(300);
            player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 20 * 20, 0));
        }

        if (damageSource.is(DamageTypeTags.IS_PROJECTILE) && damageSource.getDirectEntity() instanceof AbstractArrow && COMMON.projectileProtection.enabled.get()) {
            damageSource.getDirectEntity().remove(Entity.RemovalReason.KILLED);
        }

        if (damageSource instanceof final VoodooDamageSource voodooDamageSource && COMMON.voodooProtection.enabled.get()) {
            PoppetUtil.useVoodooProtectionPuppet(voodooDamageSource.getVoodooPoppet(), voodooDamageSource.getFromEntity());
        }

        if (damageSource.is(DamageTypes.FELL_OUT_OF_WORLD) && player.getY() < 0 && COMMON.voidProtection.enabled.get()) {
            player.fallDistance = 0;
            final ServerPlayer.RespawnConfig respawnConfig = player.getRespawnConfig();
            final LevelData.RespawnData respawnData = respawnConfig != null ? respawnConfig.respawnData() : player.level().getServer().getRespawnData();
            BlockPos spawnPos = respawnData.pos();
            ServerLevel serverWorld = player.level().getServer().getLevel(respawnData.dimension());
            if (serverWorld == null)
                serverWorld = player.level().getServer().overworld();
            player.teleportTo(
                    serverWorld,
                    spawnPos.getX(),
                    spawnPos.getY() + 1,
                    spawnPos.getZ(),
                    Set.of(),
                    player.getYRot(),
                    player.getXRot(),
                    true
            );
        }
    }

    public static List<Poppet.PoppetType> getProtectionPoppets(Player player, DamageSource damageSource) {
        List<Poppet.PoppetType> suitablePoppets = new ArrayList<>();

        if (damageSource.is(VoodooDamageSource.VOODOO) && COMMON.voodooProtection.enabled.get())
            suitablePoppets.add(VOODOO_PROTECTION);
        if ((damageSource.getDirectEntity() instanceof AbstractThrownPotion || damageSource.getDirectEntity() instanceof AreaEffectCloud) && COMMON.potionProtection.enabled.get())
            suitablePoppets.add(POTION_PROTECTION);
        if (damageSource.is(DamageTypes.FALL) && COMMON.fallProtection.enabled.get())
            suitablePoppets.add(FALL_PROTECTION);
        if (damageSource.is(DamageTypeTags.IS_PROJECTILE) && COMMON.projectileProtection.enabled.get())
            suitablePoppets.add(PROJECTILE_PROTECTION);
        if (damageSource.is(DamageTypeTags.IS_FIRE) && COMMON.fireProtection.enabled.get())
            suitablePoppets.add(FIRE_PROTECTION);
        if (damageSource.is(DamageTypeTags.IS_EXPLOSION) && COMMON.explosionProtection.enabled.get())
            suitablePoppets.add(EXPLOSION_PROTECTION);
        if (damageSource.is(DamageTypes.DROWN) && COMMON.waterProtection.enabled.get())
            suitablePoppets.add(WATER_PROTECTION);
        if (damageSource.is(DamageTypes.STARVE) && COMMON.hungerProtection.enabled.get())
            suitablePoppets.add(HUNGER_PROTECTION);
        if (damageSource.is(DamageTypes.FELL_OUT_OF_WORLD) && player.getY() < 0 && COMMON.voidProtection.enabled.get())
            suitablePoppets.add(VOID_PROTECTION);
        return suitablePoppets;
    }
}
