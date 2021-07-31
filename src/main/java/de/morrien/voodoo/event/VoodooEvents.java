package de.morrien.voodoo.event;

import de.morrien.voodoo.Poppet;
import de.morrien.voodoo.Voodoo;
import de.morrien.voodoo.VoodooDamageSource;
import de.morrien.voodoo.command.VoodooCommand;
import de.morrien.voodoo.item.ItemRegistry;
import de.morrien.voodoo.item.PoppetItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
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
        PlayerEntity player = event.player;
        Poppet poppet = null;
        for (EffectInstance potionEffect : player.getActivePotionEffects()) {
            if (potionEffect.getPotion().getEffectType() == EffectType.HARMFUL) {
                if (potionEffect.getPotion() == Effects.WITHER) {
                    if (!COMMON.witherProtection.enabled.get()) continue;
                    Poppet witherPoppet = Poppet.getPlayerPoppet(player, WITHER_PROTECTION);
                    if (witherPoppet != null) {
                        player.removeActivePotionEffect(potionEffect.getPotion());
                        witherPoppet.use();
                    }
                } else {
                    if (!COMMON.potionProtection.enabled.get()) continue;
                    if (poppet == null)
                        poppet = Poppet.getPlayerPoppet(player, POTION_PROTECTION);
                    if (poppet != null) {
                        player.removeActivePotionEffect(potionEffect.getPotion());
                        poppet.use(potionEffect.getAmplifier() + 1);
                    } else {
                        break;
                    }
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onLivingAttack(LivingAttackEvent event) {
        if (event.isCanceled() || event.getEntity().world.isRemote) return;
        DamageSource damageSource = event.getSource();
        if (event.getEntity() instanceof PlayerEntity && !((PlayerEntity) event.getEntity()).isCreative()) {
            PlayerEntity player = (PlayerEntity) event.getEntity();

            List<Poppet.PoppetType> protectionPoppetTypes = getProtectionPoppets(damageSource);

            for (Poppet.PoppetType poppetType : protectionPoppetTypes) {
                Poppet poppet = Poppet.getPlayerPoppet(player, poppetType);
                if (poppet != null && event.getAmount() > 0) {
                    if (damageSource == IN_FIRE || damageSource == LAVA || (damageSource instanceof VoodooDamageSource && damageSource.isFireDamage())) {
                        if (player.ticksExisted % 20 == 0)
                            poppet.use();
                        if (damageSource instanceof VoodooDamageSource)
                            player.extinguish();
                    } else if (damageSource == FALL) {
                        float amount = event.getAmount();
                        poppet.use((int) amount / 2);
                    } else if (damageSource.getImmediateSource() instanceof PotionEntity) {
                        poppet.use((int) Math.ceil(event.getAmount() / 3));
                    } else if (damageSource == DROWN) {
                        player.setAir(150);
                        poppet.use();
                    } else if (damageSource == STARVE) {
                        player.getFoodStats().setFoodLevel(20);
                        poppet.use();
                    } else if (poppetType == VOID_PROTECTION) {
                        if (player.getPosY() < 0) {
                            player.fallDistance = 0;
                            BlockPos spawnPos = ((ServerPlayerEntity) player).func_241140_K_();
                            if (spawnPos == null) {
                                spawnPos = new BlockPos(
                                        player.world.getWorldInfo().getSpawnX(),
                                        player.world.getWorldInfo().getSpawnY(),
                                        player.world.getWorldInfo().getSpawnZ()
                                );
                            }
                            player.setPositionAndUpdate(
                                    spawnPos.getX(),
                                    spawnPos.getY() + 1,
                                    spawnPos.getZ());
                        }
                        poppet.use();
                    } else {
                        poppet.use();
                    }
                    event.setCanceled(true);
                    return;
                }
            }
            if (player.getHealth() - event.getAmount() <= 0 && COMMON.deathProtection.enabled.get()) {
                Poppet poppet = Poppet.getPlayerPoppet(player, DEATH_PROTECTION);
                if (poppet != null) {
                    poppet.use();
                    event.setCanceled(true);
                    event.getEntity().attackEntityFrom(new DamageSource("death_protection_info"), 0.0001f);

                    player.setHealth(player.getMaxHealth() / 2);
                    player.clearActivePotions();
                    player.addPotionEffect(new EffectInstance(Effects.REGENERATION, 900, 1));
                    player.addPotionEffect(new EffectInstance(Effects.ABSORPTION, 100, 1));
                    player.addPotionEffect(new EffectInstance(Effects.FIRE_RESISTANCE, 800, 0));
                    player.world.setEntityState(player, (byte) 35);
                }
            }
        }
    }

    public static List<Poppet.PoppetType> getProtectionPoppets(DamageSource damageSource) {
        String damageType = damageSource.damageType;
        List<Poppet.PoppetType> suitablePoppets = new ArrayList<>();

        if (damageSource instanceof VoodooDamageSource && COMMON.voodooProtection.enabled.get())
            suitablePoppets.add(VOODOO_PROTECTION);
        if (damageSource.getImmediateSource() instanceof PotionEntity && COMMON.potionProtection.enabled.get())
            suitablePoppets.add(POTION_PROTECTION);
        if (damageSource == FALL && COMMON.fallProtection.enabled.get())
            suitablePoppets.add(FALL_PROTECTION);
        if (damageSource.isProjectile() && COMMON.projectileProtection.enabled.get())
            suitablePoppets.add(PROJECTILE_PROTECTION);
        if (damageSource.isFireDamage() && COMMON.fireProtection.enabled.get())
            suitablePoppets.add(FIRE_PROTECTION);
        if (damageSource.isExplosion() && COMMON.explosionProtection.enabled.get())
            suitablePoppets.add(EXPLOSION_PROTECTION);
        if (damageSource == DROWN && COMMON.waterProtection.enabled.get())
            suitablePoppets.add(WATER_PROTECTION);
        if (damageSource == STARVE && COMMON.hungerProtection.enabled.get())
            suitablePoppets.add(HUNGER_PROTECTION);
        if (damageSource == OUT_OF_WORLD && COMMON.voidProtection.enabled.get())
            suitablePoppets.add(VOID_PROTECTION);
        return suitablePoppets;
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent.EntityInteract event) {
        if (!event.getWorld().isRemote) {
            if (event.getItemStack().getItem() == ItemRegistry.taglockKit.get()) {
                if (event.getTarget() instanceof PlayerEntity) {
                    ItemStack stack = event.getItemStack();
                    if (PoppetItem.isBound(stack)) return;
                    PlayerEntity player = (PlayerEntity) event.getTarget();
                    final CompoundNBT tag = stack.getOrCreateTag();
                    tag.putUniqueId(PoppetItem.BOUND_UUID, player.getUniqueID());
                    tag.putString(PoppetItem.BOUND_NAME, player.getDisplayName().getString());
                }
            }
        }
    }
}
