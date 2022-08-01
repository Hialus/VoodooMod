package de.morrien.voodoo.item;

import de.morrien.voodoo.Poppet;
import de.morrien.voodoo.VoodooDamageSource;
import de.morrien.voodoo.util.PoppetUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

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
    protected void appendDisabledHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        if (!COMMON.voodoo.enableNeedle.get()) {
            final var text = Component.translatable("text.voodoo.poppet.needle.disabled");
            text.setStyle(Style.EMPTY.withColor(ChatFormatting.RED));
            tooltip.add(text);
        }
        if (!COMMON.voodoo.enablePush.get()) {
            final var text = Component.translatable("text.voodoo.poppet.push.disabled");
            text.setStyle(Style.EMPTY.withColor(ChatFormatting.RED));
            tooltip.add(text);
        }
        if (!COMMON.voodoo.enableFire.get()) {
            final var text = Component.translatable("text.voodoo.poppet.fire.disabled");
            text.setStyle(Style.EMPTY.withColor(ChatFormatting.RED));
            tooltip.add(text);
        }
        if (!COMMON.voodoo.enableDrowning.get()) {
            final var text = Component.translatable("text.voodoo.poppet.drowning.disabled");
            text.setStyle(Style.EMPTY.withColor(ChatFormatting.RED));
            tooltip.add(text);
        }
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        if (!COMMON.voodoo.enableNeedle.get() && !COMMON.voodoo.enablePush.get())
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        player.startUsingItem(hand);
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    @Override
    public void releaseUsing(ItemStack stack, Level world, LivingEntity livingEntity, int timeLeft) {
        if (!world.isClientSide &&
                timeLeft <= 72000 - COMMON.voodoo.pullDuration.get() &&
                livingEntity instanceof Player) {
            Player player = (Player) livingEntity;
            Player boundPlayer = getBoundPlayer(stack, world);
            if (boundPlayer != null) {
                ItemStack offhand = livingEntity.getOffhandItem();
                if (COMMON.voodoo.enableNeedle.get() && !offhand.isEmpty() && offhand.getItem() == ItemRegistry.needle) {
                    offhand.shrink(1);
                    if (boundPlayer.hurt(new VoodooDamageSource(VoodooDamageSource.VoodooDamageType.NEEDLE, stack, player), COMMON.voodoo.needleDamage.get())) {
                        stack.hurtAndBreak(COMMON.voodoo.needleDurabilityCost.get(), livingEntity, (e) -> {
                            player.broadcastBreakEvent(player.getUsedItemHand());
                        });
                    }
                } else if (COMMON.voodoo.enablePush.get()) {
                    Poppet voodooProtectionPoppet = PoppetUtil.getPlayerPoppet((ServerPlayer) boundPlayer, Poppet.PoppetType.VOODOO_PROTECTION);

                    if (voodooProtectionPoppet != null) {
                        PoppetUtil.useVoodooProtectionPuppet(stack, livingEntity);
                        voodooProtectionPoppet.use();
                    } else {
                        stack.hurtAndBreak(COMMON.voodoo.pushDurabilityCost.get(), livingEntity, (e) -> {
                            player.broadcastBreakEvent(player.getUsedItemHand());
                        });
                        Vec3 vec = player.getLookAngle();
                        boundPlayer.push(vec.x * 1.5, vec.y * 1.2, vec.z * 1.5);
                        boundPlayer.hurtMarked = true;
                    }
                }
            }
        }
    }
}
