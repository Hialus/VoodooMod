package de.morrien.voodoo.entity;

import de.morrien.voodoo.VoodooConfig;
import de.morrien.voodoo.VoodooDamageSource;
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

    public EntityPoppetItem(World worldIn, ItemEntity base, ItemStack stack) {
        super(worldIn, base.getPosX(), base.getPosY(), base.getPosZ(), stack);
        this.setPickupDelay(40);
        this.setThrowerId(base.getThrowerId());
        this.setMotion(base.getMotion());
        this.rotationYaw = base.rotationYaw;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (source.isFireDamage()) {
            if (VoodooConfig.COMMON.voodoo.enableFire.get()) {
                PlayerEntity boundPlayer = PoppetItem.getBoundPlayer(getItem(), world);
                if (boundPlayer != null) {
                    boundPlayer.setFire(1);
                    if (boundPlayer.attackEntityFrom(new VoodooDamageSource(VoodooDamageSource.VoodooDamageType.FIRE), amount)) {
                        this.getItem().damageItem(2, boundPlayer, (e) -> {
                            boundPlayer.sendBreakAnimation(boundPlayer.getActiveHand());
                        });
                    }
                }
            }
            return false;
        }
        return super.attackEntityFrom(source, amount);
    }
}
