package de.morrien.voodoo.event;

import de.morrien.voodoo.Poppet;
import de.morrien.voodoo.Voodoo;
import de.morrien.voodoo.VoodooDamageSource;
import de.morrien.voodoo.command.VoodooCommand;
import de.morrien.voodoo.item.ItemRegistry;
import de.morrien.voodoo.util.BindingUtil;
import de.morrien.voodoo.util.PoppetUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
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
import java.util.Set;

import static de.morrien.voodoo.Poppet.PoppetType.*;
import static de.morrien.voodoo.Voodoo.logger;
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
        for (EffectInstance potionEffect : player.getActiveEffects()) {
            if (potionEffect.getEffect().getCategory() == EffectType.HARMFUL) {
                if (potionEffect.getEffect() == Effects.WITHER) {
                    if (!COMMON.witherProtection.enabled.get()) continue;
                    Poppet witherPoppet = Poppet.getPlayerPoppet(player, WITHER_PROTECTION);
                    if (witherPoppet != null) {
                        player.removeEffectNoUpdate(potionEffect.getEffect());
                        witherPoppet.use();
                    }
                } else {
                    if (!COMMON.potionProtection.enabled.get()) continue;
                    if (poppet == null)
                        poppet = Poppet.getPlayerPoppet(player, POTION_PROTECTION);
                    if (poppet != null) {
                        player.removeEffectNoUpdate(potionEffect.getEffect());
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
        if (event.isCanceled() || event.getEntity().level.isClientSide) return;
        final DamageSource damageSource = event.getSource();
        if (event.getEntity() instanceof PlayerEntity && !((PlayerEntity) event.getEntity()).isCreative()) {
            final PlayerEntity player = (PlayerEntity) event.getEntity();
            final List<Poppet.PoppetType> validPoppets = getProtectionPoppets(damageSource);

            final Set<Poppet> poppetsInInventory = PoppetUtil.getPoppetsInInventory(player);
            poppetsInInventory.removeIf(poppet -> !validPoppets.contains(poppet.getItem().getPoppetType()));

            int originalDurabilityCost = getDurabilityCost(event);
            int durabilityCost = originalDurabilityCost;
            logger.debug("Damage amount: " + event.getAmount());
            logger.debug("Durability cost: " + durabilityCost);
            for (Poppet poppet : poppetsInInventory) {
                durabilityCost = usePoppet(poppet, durabilityCost);
            }
            if (durabilityCost > 0) {
                final Set<Poppet> poppetsInShelves = PoppetUtil.getPoppetsInShelves(player);
                poppetsInShelves.removeIf(poppet -> !validPoppets.contains(poppet.getItem().getPoppetType()));

                for (Poppet poppet : poppetsInShelves) {
                    durabilityCost = usePoppet(poppet, durabilityCost);
                }
            }
            float percentage = ((float) durabilityCost) / ((float) originalDurabilityCost);
            if (player.getHealth() - event.getAmount() * percentage <= 0 && COMMON.deathProtection.enabled.get()) {
                Poppet poppet = Poppet.getPlayerPoppet(player, DEATH_PROTECTION);
                if (poppet != null) {
                    poppet.use();
                    durabilityCost = 0;
                    player.setHealth(player.getMaxHealth() / 2);
                    player.removeAllEffects();
                    player.addEffect(new EffectInstance(Effects.REGENERATION, 900, 1));
                    player.addEffect(new EffectInstance(Effects.ABSORPTION, 100, 1));
                    player.addEffect(new EffectInstance(Effects.FIRE_RESISTANCE, 800, 0));
                    player.level.broadcastEntityEvent(player, (byte) 35);
                }
            }
            if (durabilityCost != originalDurabilityCost) {
                doSpecialActions(event);
                event.setCanceled(true);
                if (durabilityCost > 0) {
                    player.hurt(damageSource, event.getAmount() * percentage);
                }
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
        if (damageSource.getDirectEntity() instanceof PotionEntity && COMMON.potionProtection.enabled.get())
            return (int) Math.ceil(event.getAmount() / 3);
        if (damageSource == FALL && COMMON.fallProtection.enabled.get())
            return (int) Math.min(event.getAmount(), Math.ceil(Math.log(event.getAmount())*3));
        if (damageSource.isProjectile() && COMMON.projectileProtection.enabled.get())
            return 1;
        if (damageSource.isFire() && COMMON.fireProtection.enabled.get())
            return 1;
        if (damageSource.isExplosion() && COMMON.explosionProtection.enabled.get())
            return 1;
        if (damageSource == DROWN && COMMON.waterProtection.enabled.get())
            return 1;
        if (damageSource == STARVE && COMMON.hungerProtection.enabled.get())
            return 1;
        if (damageSource == OUT_OF_WORLD && COMMON.voidProtection.enabled.get())
            return 1;
        return 0;
    }

    private static void doSpecialActions(LivingAttackEvent event) {
        final DamageSource damageSource = event.getSource();
        final PlayerEntity player = (PlayerEntity) event.getEntity();

        if (damageSource.isFire())
            event.getEntity().clearFire();
        if (damageSource == DROWN && COMMON.waterProtection.enabled.get())
            player.setAirSupply(150);
        if (damageSource == STARVE && COMMON.hungerProtection.enabled.get())
            player.getFoodData().setFoodLevel(20);

        if (damageSource instanceof VoodooDamageSource && COMMON.voodooProtection.enabled.get()) {
            final VoodooDamageSource voodooDamageSource = (VoodooDamageSource) damageSource;
            PoppetUtil.useVoodooProtectionPuppet(voodooDamageSource.getVoodooPoppet(), voodooDamageSource.getFromEntity());
        }

        if (damageSource == OUT_OF_WORLD && COMMON.voidProtection.enabled.get()) {
            if (player.getY() < 0) {
                player.fallDistance = 0;
                BlockPos spawnPos = ((ServerPlayerEntity) player).getRespawnPosition();
                if (spawnPos == null) {
                    spawnPos = new BlockPos(
                            player.level.getLevelData().getXSpawn(),
                            player.level.getLevelData().getYSpawn(),
                            player.level.getLevelData().getZSpawn()
                    );
                }
                player.teleportTo(
                        spawnPos.getX(),
                        spawnPos.getY() + 1,
                        spawnPos.getZ());
            }
        }
    }

    //@SubscribeEvent(priority = EventPriority.HIGH)
    //public static void onLivingAttack(LivingAttackEvent event) {
    //    if (event.isCanceled() || event.getEntity().level.isClientSide) return;
    //    DamageSource damageSource = event.getSource();
    //    if (event.getEntity() instanceof PlayerEntity && !((PlayerEntity) event.getEntity()).isCreative()) {
    //        PlayerEntity player = (PlayerEntity) event.getEntity();
//
    //        List<Poppet.PoppetType> protectionPoppetTypes = getProtectionPoppets(damageSource);
//
    //        for (Poppet.PoppetType poppetType : protectionPoppetTypes) {
    //            Poppet poppet = Poppet.getPlayerPoppet(player, poppetType);
    //            if (poppet != null && event.getAmount() > 0) {
    //                if (damageSource.isFire()) {
    //                    if (player.tickCount % 20 == 0)
    //                        poppet.use();
    //                    if (damageSource instanceof VoodooDamageSource)
    //                        player.clearFire();
    //                } else if (damageSource == FALL) {
    //                    float amount = event.getAmount();
    //                    poppet.use((int) amount / 2);
    //                } else if (damageSource.getDirectEntity() instanceof PotionEntity) {
    //                    poppet.use((int) Math.ceil(event.getAmount() / 3));
    //                } else if (damageSource == DROWN) {
    //                    player.setAirSupply(150);
    //                    poppet.use();
    //                } else if (damageSource == STARVE) {
    //                    player.getFoodData().setFoodLevel(20);
    //                    poppet.use();
    //                } else if (poppetType == VOID_PROTECTION) {
    //                    if (player.getY() < 0) {
    //                        player.fallDistance = 0;
    //                        BlockPos spawnPos = ((ServerPlayerEntity) player).getRespawnPosition();
    //                        if (spawnPos == null) {
    //                            spawnPos = new BlockPos(
    //                                    player.level.getLevelData().getXSpawn(),
    //                                    player.level.getLevelData().getYSpawn(),
    //                                    player.level.getLevelData().getZSpawn()
    //                            );
    //                        }
    //                        player.teleportTo(
    //                                spawnPos.getX(),
    //                                spawnPos.getY() + 1,
    //                                spawnPos.getZ());
    //                    }
    //                    poppet.use();
    //                } else {
    //                    poppet.use();
    //                }
    //                event.setCanceled(true);
    //                return;
    //            }
    //        }
    //        if (player.getHealth() - event.getAmount() <= 0 && COMMON.deathProtection.enabled.get()) {
    //            Poppet poppet = Poppet.getPlayerPoppet(player, DEATH_PROTECTION);
    //            if (poppet != null) {
    //                poppet.use();
    //                event.setCanceled(true);
    //                event.getEntity().hurt(new DamageSource("death_protection_info"), 0.0001f);
//
    //                player.setHealth(player.getMaxHealth() / 2);
    //                player.removeAllEffects();
    //                player.addEffect(new EffectInstance(Effects.REGENERATION, 900, 1));
    //                player.addEffect(new EffectInstance(Effects.ABSORPTION, 100, 1));
    //                player.addEffect(new EffectInstance(Effects.FIRE_RESISTANCE, 800, 0));
    //                player.level.broadcastEntityEvent(player, (byte) 35);
    //            }
    //        }
    //    }
    //}

    public static List<Poppet.PoppetType> getProtectionPoppets(DamageSource damageSource) {
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
        if (damageSource == OUT_OF_WORLD && COMMON.voidProtection.enabled.get())
            suitablePoppets.add(VOID_PROTECTION);
        return suitablePoppets;
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent.EntityInteract event) {
        if (!event.getWorld().isClientSide) {
            if (event.getItemStack().getItem() == ItemRegistry.taglockKit.get()) {
                if (event.getTarget() instanceof PlayerEntity) {
                    ItemStack stack = event.getItemStack();
                    if (BindingUtil.isBound(stack)) return;
                    PlayerEntity player = (PlayerEntity) event.getTarget();
                    BindingUtil.bind(stack, player);
                }
            }
        }
    }
}
