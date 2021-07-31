package de.morrien.voodoo.entity;

import de.morrien.voodoo.VoodooConfig;
import de.morrien.voodoo.VoodooDamageSource;
import de.morrien.voodoo.VoodooUtil;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectUtils;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

/**
 * Created by Timor Morrien
 */
public class EntityPoppetItem extends ItemEntity {
    public EntityPoppetItem(World world) {
        this(EntityType.ITEM, world);
    }

    public EntityPoppetItem(EntityType<? extends ItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public EntityPoppetItem(World world, ItemEntity base, ItemStack stack) {
        super(world, base.getX(), base.getY(), base.getZ(), stack);
        this.setPickUpDelay(40);
        this.setThrower(base.getThrower());
        this.setDeltaMovement(base.getDeltaMovement());
        this.yRot = base.yRot;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.isFire()) {
            if (VoodooConfig.COMMON.voodoo.enableFire.get()) {
                PlayerEntity boundPlayer = VoodooUtil.getBoundPlayer(getItem(), level);
                if (boundPlayer != null) {
                    boundPlayer.setSecondsOnFire(1);
                    if (boundPlayer.hurt(new VoodooDamageSource(VoodooDamageSource.VoodooDamageType.FIRE), amount)) {
                        this.getItem().hurtAndBreak(VoodooConfig.COMMON.voodoo.fireDurabilityCost.get(), boundPlayer, (e) -> {
                            boundPlayer.broadcastBreakEvent(boundPlayer.getUsedItemHand());
                        });
                    }
                }
            }
            return false;
        }
        return super.hurt(source, amount);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide) return;
        if (!this.isInWaterOrBubble()) return;
        if (!VoodooConfig.COMMON.voodoo.enableDrowning.get()) return;
        if (!VoodooUtil.isBound(this.getItem())) return;
        final PlayerEntity boundPlayer = VoodooUtil.getBoundPlayer(this.getItem(), this.level);
        if (boundPlayer == null) return;
        if (boundPlayer.isInvulnerable()) return;
        if (boundPlayer.canBreatheUnderwater() || EffectUtils.hasWaterBreathing(boundPlayer)) return;
        this.decreaseAirSupply(boundPlayer);
        if (boundPlayer.getAirSupply() > -40) return;
        boundPlayer.setAirSupply(0);
        boundPlayer.hurt(DamageSource.DROWN, 2.0F);
        this.getItem().hurtAndBreak(VoodooConfig.COMMON.voodoo.drownDurabilityCost.get(), boundPlayer, (e) -> boundPlayer.broadcastBreakEvent(boundPlayer.getUsedItemHand()));
    }

    private void decreaseAirSupply(PlayerEntity playerEntity) {
        final int airSupply = playerEntity.getAirSupply();
        int respiration = EnchantmentHelper.getRespiration(playerEntity);
        playerEntity.setAirSupply(respiration > 0 && this.random.nextInt(respiration + 1) > 0 ? airSupply : airSupply - 5);
    }
}
