package de.morrien.voodoo.entity;

import de.morrien.voodoo.Poppet;
import de.morrien.voodoo.VoodooConfig;
import de.morrien.voodoo.VoodooDamageSource;
import de.morrien.voodoo.item.VoodooPoppetItem;
import de.morrien.voodoo.util.BindingUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

import java.util.WeakHashMap;

/**
 * Created by Phoebe Morrien
 */
public class PoppetEntityHandler {
    private static final WeakHashMap<ItemEntity, Integer> lastFireTicks = new WeakHashMap<>();

    /**
     * Detects fire damage to a voodoo poppet item entity.
     * If the poppet is bound to a player the bound player will be set on fire.
     *
     * @param entity The item entity
     * @param source The source of the damage
     * @return If the item entity should be invulnerable to the damage
     */
    public static boolean onFire(ItemEntity entity, DamageSource source) {
        final ItemStack stack = entity.getItem();
        if (!(stack.getItem() instanceof VoodooPoppetItem)) return false;
        if (!source.is(DamageTypeTags.IS_FIRE)) return false;
        if (entity.level() instanceof ServerLevel level &&
                VoodooConfig.COMMON.voodoo.enableFire.get() &&
                entity.tickCount - lastFireTicks.getOrDefault(entity, -40) >= 60) {
            lastFireTicks.put(entity, entity.tickCount);
            Player boundPlayer = BindingUtil.getBoundPlayer(stack, level);
            if (boundPlayer != null) {
                if (boundPlayer.isInvulnerable()) return true;
                if (boundPlayer.hasEffect(MobEffects.FIRE_RESISTANCE)) return true;
                if (boundPlayer.isDeadOrDying()) return true;
                boundPlayer.igniteForSeconds(4);
                if (Poppet.PoppetType.VOODOO.hasDurability())
                    stack.hurtAndBreak(VoodooConfig.COMMON.voodoo.fireDurabilityCost.get(), level, (ServerPlayer) boundPlayer, item -> entity.discard());
            }
        }
        return true;
    }

    /**
     * This method is used to detect if a voodoo poppet item entity is in water.
     * If the poppet is in water and is bound the bound player will start to drown, as if they were in water themselves.
     * Voodoo poppet item entities never despawn, so they can be left in water or fire.
     *
     * @param entity The item entity
     */
    public static void onTick(ItemEntity entity) {
        if (!(entity.level() instanceof ServerLevel level)) return;
        final ItemStack stack = entity.getItem();
        if (!(stack.getItem() instanceof VoodooPoppetItem)) return;
        entity.setUnlimitedLifetime();
        if (!entity.isInWater() && !entity.getInBlockState().is(Blocks.BUBBLE_COLUMN)) return;
        if (!VoodooConfig.COMMON.voodoo.enableDrowning.get()) return;
        if (!BindingUtil.isBound(stack)) return;
        final Player boundPlayer = BindingUtil.getBoundPlayer(stack, level);
        if (boundPlayer == null) return;
        if (boundPlayer.isInvulnerable()) return;
        if (boundPlayer.canBreatheUnderwater() || MobEffectUtil.hasWaterBreathing(boundPlayer)) return;
        decreaseAirSupply(entity, boundPlayer);
        if (boundPlayer.getAirSupply() > -40) return;
        boundPlayer.setAirSupply(0);
        if (boundPlayer.hurtServer(level, new VoodooDamageSource(level, VoodooDamageSource.VoodooDamageType.WATER, stack, entity), 2.0F) && Poppet.PoppetType.VOODOO.hasDurability())
            stack.hurtAndBreak(VoodooConfig.COMMON.voodoo.drownDurabilityCost.get(), level, (ServerPlayer) boundPlayer, item -> {});
    }

    /**
     * Private helper method to decrease the air supply of a player.
     *
     * @param entity       The item entity
     * @param playerEntity The player
     */
    private static void decreaseAirSupply(ItemEntity entity, Player playerEntity) {
        final int airSupply = playerEntity.getAirSupply();
        final double oxygenBonus = playerEntity.getAttributeValue(Attributes.OXYGEN_BONUS);
        playerEntity.setAirSupply(oxygenBonus > 0 && entity.getRandom().nextDouble() >= 1.0 / (oxygenBonus + 1.0) ? airSupply : airSupply - 5);
    }
}
