package de.morrien.voodoo.item;

import de.morrien.voodoo.Poppet;
import de.morrien.voodoo.VoodooConfig;
import de.morrien.voodoo.VoodooDamageSource;
import de.morrien.voodoo.VoodooGroup;
import de.morrien.voodoo.entity.EntityPoppetItem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

import static de.morrien.voodoo.Poppet.PoppetType.BLANK;
import static de.morrien.voodoo.VoodooUtil.*;

/**
 * Created by Timor Morrien
 */
public class PoppetItem extends Item {
    private Poppet.PoppetType poppetType;

    public PoppetItem(Poppet.PoppetType poppetType) {
        super(new Properties()
                .tab(VoodooGroup.INSTANCE)
                .durability(poppetType.getDurability())
                .setNoRepair()
        );
        this.poppetType = poppetType;
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return poppetType.getDurability();
    }

    public Poppet.PoppetType getPoppetType() {
        return poppetType;
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return stack.getItem() == ItemRegistry.poppetMap.get(Poppet.PoppetType.VOODOO).get();
    }

    @Nullable
    @Override
    public Entity createEntity(World world, Entity itemEntity, ItemStack itemstack) {
        return new EntityPoppetItem(world, (ItemEntity) itemEntity, itemstack);
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (!VoodooConfig.COMMON.voodoo.enableNeedle.get() && !VoodooConfig.COMMON.voodoo.enablePush.get())
            return ActionResult.pass(player.getItemInHand(hand));
        player.startUsingItem(hand);
        return ActionResult.success(player.getItemInHand(hand));
    }

    @Override
    public void releaseUsing(ItemStack stack, World world, LivingEntity livingEntity, int timeLeft) {
        if (!world.isClientSide &&
                timeLeft <= 72000 - VoodooConfig.COMMON.voodoo.pullDuration.get() &&
                livingEntity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) livingEntity;
            PlayerEntity boundPlayer = getBoundPlayer(stack, world);
            if (boundPlayer != null) {
                ItemStack offhand = livingEntity.getOffhandItem();
                if (VoodooConfig.COMMON.voodoo.enableNeedle.get() && !offhand.isEmpty() && offhand.getItem() == ItemRegistry.needle.get()) {
                    boundPlayer.hurt(new VoodooDamageSource(VoodooDamageSource.VoodooDamageType.NEEDLE), 1f);
                    offhand.shrink(1);
                    stack.hurtAndBreak(VoodooConfig.COMMON.voodoo.needleDurabilityCost.get(), livingEntity, (e) -> {
                        player.broadcastBreakEvent(player.getUsedItemHand());
                    });
                } else if (VoodooConfig.COMMON.voodoo.enablePush.get()) {
                    Poppet voodooProtectionPoppet = Poppet.getPlayerPoppet(boundPlayer, Poppet.PoppetType.VOODOO_PROTECTION);
                    stack.hurtAndBreak(VoodooConfig.COMMON.voodoo.pushDurabilityCost.get(), livingEntity, (e) -> {
                        player.broadcastBreakEvent(player.getUsedItemHand());
                    });
                    if (voodooProtectionPoppet != null) {
                        voodooProtectionPoppet.use();
                        player.displayClientMessage(new TranslationTextComponent("text.voodoo.voodoo_protection.had", boundPlayer.getName()), true);
                    } else {
                        Vector3d vec = player.getLookAngle();
                        boundPlayer.push(vec.x * 1.5, vec.y * 1.2, vec.z * 1.5);
                        boundPlayer.hurtMarked = true;
                    }
                }
            }
        }
    }

    @Override
    public UseAction getUseAnimation(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        if (isBound(stack)) {
            checkForNameUpdate(stack, world);
            tooltip.add(new TranslationTextComponent("text.voodoo.poppet.bound", getBoundName(stack)));
        } else if (stack.getItem() != ItemRegistry.poppetMap.get(BLANK).get()) {
            tooltip.add(new TranslationTextComponent("text.voodoo.poppet.not_bound"));
        }
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return (poppetType == Poppet.PoppetType.VOODOO) ? 72000 : 0;
    }

    @Override
    public boolean canBeDepleted() {
        return poppetType.hasDurability();
    }
}
