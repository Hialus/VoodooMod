package de.morrien.voodoo.item;

import de.morrien.voodoo.VoodooDamageSource;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import static de.morrien.voodoo.Poppet.PoppetType.VAMPIRIC;
import static de.morrien.voodoo.VoodooConfig.COMMON;
import static de.morrien.voodoo.util.BindingUtil.getBoundPlayer;

/**
 * Created by Timor Morrien
 */
public class VampiricPoppetItem extends PoppetItem {
    private final Rarity rarity = Rarity.create("vampiric", TextFormatting.RED);

    public VampiricPoppetItem() {
        super(VAMPIRIC);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public UseAction getUseAnimation(ItemStack stack) {
        return UseAction.DRINK;
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (!COMMON.vampiric.enabled.get())
            return ActionResult.pass(player.getItemInHand(hand));
        player.startUsingItem(hand);
        return ActionResult.success(player.getItemInHand(hand));
    }

    @Override
    public void onUseTick(World world, LivingEntity entity, ItemStack stack, int remainingTicks) {
        if (world.isClientSide) return;
        if ((remainingTicks - 1) % COMMON.vampiric.drainageInterval.get() != 0) return;
        if (!(entity instanceof ServerPlayerEntity)) return;
        final ServerPlayerEntity player = (ServerPlayerEntity) entity;
        final float playerDifference = player.getMaxHealth() - player.getHealth();
        if (playerDifference == 0) return;
        final PlayerEntity boundPlayer = getBoundPlayer(stack, world);
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
