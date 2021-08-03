package de.morrien.voodoo.item;

import de.morrien.voodoo.Poppet;
import de.morrien.voodoo.VoodooDamageSource;
import de.morrien.voodoo.entity.PoppetItemEntity;
import de.morrien.voodoo.util.PoppetUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;

import static de.morrien.voodoo.Poppet.PoppetType.VAMPIRIC;
import static de.morrien.voodoo.Poppet.PoppetType.VOODOO;
import static de.morrien.voodoo.VoodooConfig.COMMON;
import static de.morrien.voodoo.util.BindingUtil.getBoundPlayer;

/**
 * Created by Timor Morrien
 */
public class VoodooPoppetItem extends PoppetItem {
    public VoodooPoppetItem() {
        super(VOODOO);
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Nullable
    @Override
    public Entity createEntity(World world, Entity itemEntity, ItemStack itemstack) {
        return new PoppetItemEntity(world, (ItemEntity) itemEntity, itemstack);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public UseAction getUseAnimation(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (!COMMON.voodoo.enableNeedle.get() && !COMMON.voodoo.enablePush.get())
            return ActionResult.pass(player.getItemInHand(hand));
        player.startUsingItem(hand);
        return ActionResult.success(player.getItemInHand(hand));
    }

    @Override
    public void releaseUsing(ItemStack stack, World world, LivingEntity livingEntity, int timeLeft) {
        if (!world.isClientSide &&
                timeLeft <= 72000 - COMMON.voodoo.pullDuration.get() &&
                livingEntity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) livingEntity;
            PlayerEntity boundPlayer = getBoundPlayer(stack, world);
            if (boundPlayer != null) {
                ItemStack offhand = livingEntity.getOffhandItem();
                if (COMMON.voodoo.enableNeedle.get() && !offhand.isEmpty() && offhand.getItem() == ItemRegistry.needle.get()) {
                    offhand.shrink(1);
                    if (boundPlayer.hurt(new VoodooDamageSource(VoodooDamageSource.VoodooDamageType.NEEDLE, stack, player), COMMON.voodoo.needleDamage.get())) {
                        stack.hurtAndBreak(COMMON.voodoo.needleDurabilityCost.get(), livingEntity, (e) -> {
                            player.broadcastBreakEvent(player.getUsedItemHand());
                        });
                    }
                } else if (COMMON.voodoo.enablePush.get()) {
                    Poppet voodooProtectionPoppet = PoppetUtil.getPlayerPoppet((ServerPlayerEntity) boundPlayer, Poppet.PoppetType.VOODOO_PROTECTION);

                    if (voodooProtectionPoppet != null) {
                        PoppetUtil.useVoodooProtectionPuppet(stack, livingEntity);
                        voodooProtectionPoppet.use();
                    } else {
                        stack.hurtAndBreak(COMMON.voodoo.pushDurabilityCost.get(), livingEntity, (e) -> {
                            player.broadcastBreakEvent(player.getUsedItemHand());
                        });
                        Vector3d vec = player.getLookAngle();
                        boundPlayer.push(vec.x * 1.5, vec.y * 1.2, vec.z * 1.5);
                        boundPlayer.hurtMarked = true;
                    }
                }
            }
        }
    }
}
