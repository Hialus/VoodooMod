package de.morrien.voodoo.item;

import de.morrien.voodoo.VoodooDamageSource;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import static de.morrien.voodoo.Poppet.PoppetType.VAMPIRIC;
import static de.morrien.voodoo.VoodooConfig.COMMON;
import static de.morrien.voodoo.util.BindingUtil.getBoundPlayer;

/**
 * Created by Timor Morrien
 */
public class VampiricPoppetItem extends PoppetItem {
    private final Rarity rarity = Rarity.create("vampiric", ChatFormatting.RED);

    public VampiricPoppetItem() {
        super(VAMPIRIC);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        if (!COMMON.vampiric.enabled.get())
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        player.startUsingItem(hand);
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    @Override
    public void onUseTick(Level world, LivingEntity entity, ItemStack stack, int remainingTicks) {
        if (world.isClientSide) return;
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
        if (boundPlayer.hurt(new VoodooDamageSource(VoodooDamageSource.VoodooDamageType.VAMPIRIC, stack, player), healthToTake)) {
            player.heal(healthToTake);
            stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(player.getUsedItemHand()));
        }
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return rarity;
    }
}
