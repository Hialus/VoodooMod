package de.morrien.voodoo.item;

import de.morrien.voodoo.Poppet;
import de.morrien.voodoo.VoodooConfig;
import de.morrien.voodoo.VoodooDamageSource;
import de.morrien.voodoo.VoodooGroup;
import de.morrien.voodoo.entity.PoppetItemEntity;
import de.morrien.voodoo.util.PoppetUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

import static de.morrien.voodoo.Poppet.PoppetType.BLANK;
import static de.morrien.voodoo.Poppet.PoppetType.VOODOO;
import static de.morrien.voodoo.VoodooConfig.COMMON;
import static de.morrien.voodoo.util.BindingUtil.*;

/**
 * Created by Timor Morrien
 */
public class PoppetItem extends Item {
    private final Poppet.PoppetType poppetType;

    public PoppetItem(Poppet.PoppetType poppetType) {
        super(new Properties()
                .tab(VoodooGroup.INSTANCE)
                .durability(poppetType.getDurability())
                .setNoRepair()
        );
        this.poppetType = poppetType;
    }

    public Poppet.PoppetType getPoppetType() {
        return poppetType;
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return poppetType.getDurability();
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return stack.getItem() == ItemRegistry.poppetMap.get(VOODOO).get();
    }

    @Nullable
    @Override
    public Entity createEntity(World world, Entity itemEntity, ItemStack itemstack) {
        return new PoppetItemEntity(world, (ItemEntity) itemEntity, itemstack);
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (poppetType != VOODOO) return ActionResult.pass(player.getItemInHand(hand));
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

    @Override
    public UseAction getUseAnimation(ItemStack stack) {
        return poppetType == VOODOO ? UseAction.BOW : UseAction.NONE;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        if (isBound(stack)) {
            checkForNameUpdate(stack, world);
            final TranslationTextComponent text = new TranslationTextComponent(
                    "text.voodoo.poppet.bound",
                    getBoundName(stack)
            );
            text.setStyle(Style.EMPTY.withColor(TextFormatting.GRAY));
            tooltip.add(text);
        } else if (stack.getItem() != ItemRegistry.poppetMap.get(BLANK).get()) {
            final TranslationTextComponent text = new TranslationTextComponent("text.voodoo.poppet.not_bound");
            text.setStyle(Style.EMPTY.withColor(TextFormatting.GRAY));
            tooltip.add(text);
        }
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return poppetType == VOODOO ? 72000 : 0;
    }

    @Override
    public boolean canBeDepleted() {
        return poppetType.hasDurability();
    }
}
