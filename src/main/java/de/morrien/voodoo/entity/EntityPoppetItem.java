package de.morrien.voodoo.entity;

import de.morrien.voodoo.VoodooConfig;
import de.morrien.voodoo.VoodooDamageSource;
import de.morrien.voodoo.VoodooUtil;
import de.morrien.voodoo.item.PoppetItem;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
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
                        this.getItem().hurtAndBreak(2, boundPlayer, (e) -> {
                            boundPlayer.broadcastBreakEvent(boundPlayer.getUsedItemHand());
                        });
                    }
                }
            }
            return false;
        }
        return super.hurt(source, amount);
    }
}
