package de.morrien.voodoo.event;

import de.morrien.voodoo.Poppet;
import de.morrien.voodoo.Voodoo;
import de.morrien.voodoo.VoodooDamageSource;
import de.morrien.voodoo.command.VoodooCommand;
import de.morrien.voodoo.item.ItemRegistry;
import de.morrien.voodoo.util.BindingUtil;
import de.morrien.voodoo.util.PoppetUtil;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static de.morrien.voodoo.Poppet.PoppetType.*;
import static de.morrien.voodoo.VoodooConfig.COMMON;
import static net.minecraft.util.DamageSource.*;

@Mod.EventBusSubscriber(modid = Voodoo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class VoodooEvents {
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        VoodooCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onTickPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.side == LogicalSide.CLIENT) return;
        if (event.phase == TickEvent.Phase.START) return;
        checkPotionEffects(event.player);
        if (event.player.tickCount % 20 != 0) return;
        checkFoodStatus(event.player);
    }

    private static void checkPotionEffects(PlayerEntity player) {
        for (Iterator<EffectInstance> iterator = player.getActiveEffects().iterator(); iterator.hasNext(); ) {
            EffectInstance potionEffect = iterator.next();
            if (potionEffect.getEffect().getCategory() != EffectType.HARMFUL) continue;
            if (potionEffect.getEffect() == Effects.WITHER) {
                removeWitherEffect(player, potionEffect);
            } else {
                removePotionEffect(player, potionEffect);
            }
        }
    }

    private static void removeWitherEffect(PlayerEntity player, EffectInstance potionEffect) {
        if (!COMMON.witherProtection.enabled.get()) return;
        Poppet witherPoppet = PoppetUtil.getPlayerPoppet(player, WITHER_PROTECTION);
        if (witherPoppet == null) return;
        player.removeEffect(potionEffect.getEffect());
        witherPoppet.use();
    }

    private static void removePotionEffect(PlayerEntity player, EffectInstance potionEffect) {
        if (!COMMON.potionProtection.enabled.get()) return;
        int durabilityCost = potionEffect.getAmplifier() + 1;
        while (durabilityCost > 0) {
            Poppet poppet = PoppetUtil.getPlayerPoppet(player, POTION_PROTECTION);
            if (poppet == null) break;
            durabilityCost = usePoppet(poppet, durabilityCost);
        }
        player.removeEffect(potionEffect.getEffect());
        if (durabilityCost > 0) {
            final EffectInstance effectInstance = new EffectInstance(
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

    private static void checkFoodStatus(PlayerEntity player) {
        if (player.getFoodData().getFoodLevel() > 10) return;
        final Poppet hungerPoppet = PoppetUtil.getPlayerPoppet(player, HUNGER_PROTECTION);
        if (hungerPoppet == null) return;
        player.addEffect(new EffectInstance(Effects.SATURATION, 60 * 20, 1));
        usePoppet(hungerPoppet, 1);
    }

    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getWorld().isClientSide) return;
        if (event.getItemStack().getItem() != ItemRegistry.taglockKit.get()) return;
        if (event.getTarget() instanceof PlayerEntity) {
            ItemStack stack = event.getItemStack();
            if (BindingUtil.isBound(stack)) return;
            PlayerEntity player = (PlayerEntity) event.getTarget();
            BindingUtil.bind(stack, player);
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity().level.isClientSide) return;
        if (event.getSource() == OUT_OF_WORLD) return;
        if (!COMMON.deathProtection.enabled.get()) return;
        if (!(event.getEntityLiving() instanceof PlayerEntity)) return;

        final PlayerEntity player = (PlayerEntity) event.getEntity();
        Poppet poppet = PoppetUtil.getPlayerPoppet(player, DEATH_PROTECTION);
        if (poppet != null) {
            poppet.use();
            event.setCanceled(true);
            player.setHealth(player.getMaxHealth() / 2);
            player.removeAllEffects();
            player.addEffect(new EffectInstance(Effects.REGENERATION, 45 * 20, 1));
            player.addEffect(new EffectInstance(Effects.ABSORPTION, 5 * 20, 1));
            player.addEffect(new EffectInstance(Effects.FIRE_RESISTANCE, 40 * 20, 0));
            player.level.broadcastEntityEvent(player, (byte) 35);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onLivingAttack(LivingAttackEvent event) {
        if (!(event.getEntity() instanceof PlayerEntity)) return;
        final PlayerEntity player = (PlayerEntity) event.getEntity();
        if (event.isCanceled() ||
                event.getAmount() == 0 ||
                player.level.isClientSide ||
                player.isInvulnerableTo(event.getSource()) ||
                player.isCreative() ||
                player.isDeadOrDying() ||
                (event.getSource().isFire() && player.hasEffect(Effects.FIRE_RESISTANCE))
        ) return;
        final DamageSource damageSource = event.getSource();
        final List<Poppet.PoppetType> validPoppets = getProtectionPoppets(event);

        final List<Poppet> poppetsInInventory = PoppetUtil.getPoppetsInInventory(player);
        poppetsInInventory.removeIf(poppet -> !validPoppets.contains(poppet.getItem().getPoppetType()));

        int originalDurabilityCost = getDurabilityCost(event);
        int durabilityCost = originalDurabilityCost;
        for (int i = 0; i < poppetsInInventory.size() && durabilityCost > 0; i++) {
            Poppet poppet = poppetsInInventory.get(i);
            durabilityCost = usePoppet(poppet, durabilityCost);
        }
        if (durabilityCost > 0) {
            final List<Poppet> poppetsInShelves = PoppetUtil.getPoppetsInShelves(player);
            poppetsInShelves.removeIf(poppet -> !validPoppets.contains(poppet.getItem().getPoppetType()));

            for (int i = 0; i < poppetsInShelves.size() && durabilityCost > 0; i++) {
                Poppet poppet = poppetsInShelves.get(i);
                durabilityCost = usePoppet(poppet, durabilityCost);
            }
        }
        if (durabilityCost != originalDurabilityCost) {
            doSpecialActions(event);
            event.setCanceled(true);
            if (durabilityCost > 0) {
                float percentage = ((float) durabilityCost) / ((float) originalDurabilityCost);
                player.hurt(damageSource, event.getAmount() * percentage);
            }
        }
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

    private static int getDurabilityCost(LivingAttackEvent event) {
        final DamageSource damageSource = event.getSource();
        if (damageSource instanceof VoodooDamageSource && COMMON.voodooProtection.enabled.get())
            return 1;
        if ((damageSource.getDirectEntity() instanceof PotionEntity || damageSource.getDirectEntity() instanceof AreaEffectCloudEntity) && COMMON.potionProtection.enabled.get())
            return (int) (Math.log(event.getAmount() / 6) / Math.log(2)) + 1;
        if (damageSource == FALL && COMMON.fallProtection.enabled.get())
            return (int) Math.min(event.getAmount(), Math.ceil(Math.log(event.getAmount()) * 3));
        if (damageSource.isProjectile() && COMMON.projectileProtection.enabled.get())
            return 1;
        if (damageSource.isFire() && COMMON.fireProtection.enabled.get())
            return 1;
        if (damageSource.isExplosion() && COMMON.explosionProtection.enabled.get())
            return 1;
        if (damageSource == DROWN && COMMON.waterProtection.enabled.get())
            return 1;
        if (damageSource == OUT_OF_WORLD && COMMON.voidProtection.enabled.get())
            return 1;
        return 0;
    }

    private static void doSpecialActions(LivingAttackEvent event) {
        final DamageSource damageSource = event.getSource();
        final PlayerEntity player = (PlayerEntity) event.getEntity();

        if (damageSource.isFire()) {
            event.getEntity().clearFire();
            ((PlayerEntity) event.getEntity()).addEffect(new EffectInstance(Effects.FIRE_RESISTANCE, 10 * 20, 0));
        }

        if (damageSource == DROWN && COMMON.waterProtection.enabled.get()) {
            player.setAirSupply(300);
            ((PlayerEntity) event.getEntity()).addEffect(new EffectInstance(Effects.WATER_BREATHING, 20 * 20, 0));
        }

        if (damageSource.isProjectile() && damageSource.getDirectEntity() instanceof AbstractArrowEntity && COMMON.projectileProtection.enabled.get()) {
            damageSource.getDirectEntity().remove();
        }

        if (damageSource instanceof VoodooDamageSource && COMMON.voodooProtection.enabled.get()) {
            final VoodooDamageSource voodooDamageSource = (VoodooDamageSource) damageSource;
            PoppetUtil.useVoodooProtectionPuppet(voodooDamageSource.getVoodooPoppet(), voodooDamageSource.getFromEntity());
        }

        if (damageSource == OUT_OF_WORLD && player.getY() < 0 && COMMON.voidProtection.enabled.get()) {
            player.fallDistance = 0;
            final ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
            BlockPos spawnPos = serverPlayer.getRespawnPosition();
            ServerWorld serverWorld = serverPlayer.server.getLevel(serverPlayer.getRespawnDimension());
            if (serverWorld == null)
                serverWorld = serverPlayer.server.overworld();
            if (spawnPos == null) {
                spawnPos = new BlockPos(
                        serverWorld.getLevelData().getXSpawn(),
                        serverWorld.getLevelData().getYSpawn(),
                        serverWorld.getLevelData().getZSpawn()
                );
            }
            serverPlayer.teleportTo(
                    serverWorld,
                    spawnPos.getX(),
                    spawnPos.getY() + 1,
                    spawnPos.getZ(),
                    serverPlayer.xRot,
                    serverPlayer.yRot
            );
        }
    }

    public static List<Poppet.PoppetType> getProtectionPoppets(LivingAttackEvent event) {
        final DamageSource damageSource = event.getSource();
        List<Poppet.PoppetType> suitablePoppets = new ArrayList<>();

        if (damageSource instanceof VoodooDamageSource && COMMON.voodooProtection.enabled.get())
            suitablePoppets.add(VOODOO_PROTECTION);
        if (damageSource.getDirectEntity() instanceof PotionEntity && COMMON.potionProtection.enabled.get())
            suitablePoppets.add(POTION_PROTECTION);
        if (damageSource == FALL && COMMON.fallProtection.enabled.get())
            suitablePoppets.add(FALL_PROTECTION);
        if (damageSource.isProjectile() && COMMON.projectileProtection.enabled.get())
            suitablePoppets.add(PROJECTILE_PROTECTION);
        if (damageSource.isFire() && COMMON.fireProtection.enabled.get())
            suitablePoppets.add(FIRE_PROTECTION);
        if (damageSource.isExplosion() && COMMON.explosionProtection.enabled.get())
            suitablePoppets.add(EXPLOSION_PROTECTION);
        if (damageSource == DROWN && COMMON.waterProtection.enabled.get())
            suitablePoppets.add(WATER_PROTECTION);
        if (damageSource == STARVE && COMMON.hungerProtection.enabled.get())
            suitablePoppets.add(HUNGER_PROTECTION);
        if (damageSource == OUT_OF_WORLD && event.getEntity().getY() < 0 && COMMON.voidProtection.enabled.get())
            suitablePoppets.add(VOID_PROTECTION);
        return suitablePoppets;
    }
}
