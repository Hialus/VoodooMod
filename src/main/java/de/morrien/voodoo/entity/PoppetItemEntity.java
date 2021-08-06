package de.morrien.voodoo.entity;

import de.morrien.voodoo.VoodooConfig;
import de.morrien.voodoo.VoodooDamageSource;
import de.morrien.voodoo.util.BindingUtil;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

/**
 * Created by Timor Morrien
 */
public class PoppetItemEntity extends ItemEntity {
    public PoppetItemEntity(Level world) {
        this(EntityType.ITEM, world);
    }

    public PoppetItemEntity(EntityType<? extends ItemEntity> entityType, Level world) {
        super(entityType, world);
    }

    public PoppetItemEntity(Level world, ItemEntity base, ItemStack stack) {
        super(world, base.getX(), base.getY(), base.getZ(), stack);
        this.setPickUpDelay(40);
        this.setThrower(base.getThrower());
        this.setDeltaMovement(base.getDeltaMovement());
        this.setYRot(base.getYRot());
    }

    /**
     * Detects fire damage to the poppet.
     * If the poppet is bound to a player the bound player will be set on fire.
     *
     * @param source The source of the damage
     * @param amount THe amount of damage that should be inflicted
     * @return If the damage to this entity should be canceled
     */
    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.isFire()) {
            if (VoodooConfig.COMMON.voodoo.enableFire.get()) {
                Player boundPlayer = BindingUtil.getBoundPlayer(getItem(), level);
                if (boundPlayer != null) {
                    boundPlayer.setSecondsOnFire(1);
                    boundPlayer.hurt(new VoodooDamageSource(VoodooDamageSource.VoodooDamageType.FIRE, getItem(), this), amount);
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
