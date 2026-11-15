package de.morrien.voodoo.item;

import de.morrien.voodoo.VoodooDamageSource;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;

import static de.morrien.voodoo.Poppet.PoppetType.VAMPIRIC;
import static de.morrien.voodoo.VoodooConfig.COMMON;
import static de.morrien.voodoo.util.BindingUtil.getBoundPlayer;

/**
 * Created by Phoebe Morrien
 */
public class VampiricPoppetItem extends PoppetItem {
    public VampiricPoppetItem(Properties properties) {
        super(VAMPIRIC, properties);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return COMMON.vampiric.drainageInterval.get();
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.DRINK;
    }

    @Override
    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        if (!COMMON.vampiric.enabled.get())
            return InteractionResult.PASS;
        player.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }

    @Override
    public void onUseTick(Level world, LivingEntity entity, ItemStack stack, int remainingTicks) {
        if (world.isClientSide()) return;
        if ((remainingTicks - 1) % COMMON.vampiric.drainageInterval.get() != 0) return;
        if (!(entity instanceof ServerPlayer)) return;
        final ServerPlayer player = (ServerPlayer) entity;
        final float playerDifference = player.getMaxHealth() - player.getHealth();
        if (playerDifference == 0) return;
        final Player boundPlayer = getBoundPlayer(stack, world);
        if (boundPlayer == null) return;
        final float boundDifference = boundPlayer.getHealth() - COMMON.vampiric.healthLimit.get();
        if (boundDifference <= 0) return;
        final float healthToTake = Math.min(Math.min(playerDifference, boundDifference), COMMON.vampiric.healthPerDrain.get());
        if (boundPlayer.hurtServer((ServerLevel) world, new VoodooDamageSource(world, VoodooDamageSource.VoodooDamageType.VAMPIRIC, stack, player), healthToTake)) {
            player.heal(healthToTake);
            if (poppetType.hasDurability())
                stack.hurtAndBreak(1, player, player.getUsedItemHand().asEquipmentSlot());
        }
    }

    @Override
    public Component getName(ItemStack stack) {
        return super.getName(stack).copy().withStyle(ChatFormatting.RED);
    }
}
