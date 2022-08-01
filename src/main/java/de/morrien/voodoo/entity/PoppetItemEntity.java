package de.morrien.voodoo.entity;

import de.morrien.voodoo.VoodooConfig;
import de.morrien.voodoo.VoodooDamageSource;
import de.morrien.voodoo.util.BindingUtil;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

/**
 * Created by Timor Morrien
 */
public class PoppetItemEntity extends ItemEntity {
    public PoppetItemEntity(Level world, double x, double y, double z, Vec3 deltaMovement, float yRot, ItemStack stack, UUID thrower) {
        super(world, x, y, z, stack);
        this.setPickUpDelay(40);
        this.setThrower(thrower);
        this.setDeltaMovement(deltaMovement);
        this.setYRot(yRot);
    }

    private int lastFireTick = -20;

    /**
     * Detects fire damage to the poppet.
     * If the poppet is bound to a player the bound player will be set on fire.
     *
     * @param source The source of the damage
     * @param amount The amount of damage that should be inflicted
     * @return If the damage to this entity should be canceled
     */
    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.isFire()) {
            if (VoodooConfig.COMMON.voodoo.enableFire.get() && this.tickCount - this.lastFireTick >= 20) {
                this.lastFireTick = this.tickCount;
                Player boundPlayer = BindingUtil.getBoundPlayer(getItem(), level);
                if (boundPlayer != null) {
                    boundPlayer.setSecondsOnFire(2);
                    boundPlayer.hurt(new VoodooDamageSource(VoodooDamageSource.VoodooDamageType.FIRE, getItem(), this), 1);
                    this.getItem().hurtAndBreak(VoodooConfig.COMMON.voodoo.fireDurabilityCost.get(), boundPlayer, (e) -> {
                        this.remove(RemovalReason.KILLED);
                    });
                }
            }
            return false;
        }
        return super.hurt(source, amount);
    }

    /**
     * This method is used to detect if the poppet is in water.
     * If the poppet is in water and is bound the bound player will start to drown, as if they were in water themselves.
     */
    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide) return;
        if (!this.isInWaterOrBubble()) return;
        if (!VoodooConfig.COMMON.voodoo.enableDrowning.get()) return;
        if (!BindingUtil.isBound(this.getItem())) return;
        final Player boundPlayer = BindingUtil.getBoundPlayer(this.getItem(), this.level);
        if (boundPlayer == null) return;
        if (boundPlayer.isInvulnerable()) return;
        if (boundPlayer.canBreatheUnderwater() || MobEffectUtil.hasWaterBreathing(boundPlayer)) return;
        this.decreaseAirSupply(boundPlayer);
        if (boundPlayer.getAirSupply() > -40) return;
        boundPlayer.setAirSupply(0);
        if (boundPlayer.hurt(new VoodooDamageSource(VoodooDamageSource.VoodooDamageType.WATER, getItem(), this), 2.0F))
            this.getItem().hurtAndBreak(VoodooConfig.COMMON.voodoo.drownDurabilityCost.get(), boundPlayer, (e) -> boundPlayer.broadcastBreakEvent(boundPlayer.getUsedItemHand()));
    }

    /**
     * Private helper method to decrease the air supply of a player.
     *
     * @param playerEntity The player
     */
    private void decreaseAirSupply(Player playerEntity) {
        final int airSupply = playerEntity.getAirSupply();
        int respiration = EnchantmentHelper.getRespiration(playerEntity);
        playerEntity.setAirSupply(respiration > 0 && this.random.nextInt(respiration + 1) > 0 ? airSupply : airSupply - 5);
    }
}
