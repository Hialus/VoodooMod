package de.morrien.voodoo.item;

import de.morrien.voodoo.VoodooGroup;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;

import static de.morrien.voodoo.util.BindingUtil.*;

/**
 * Created by Timor Morrien
 */
@SuppressWarnings({"ConstantConditions", "NullableProblems"})
public class TaglockKitItem extends Item {
    public TaglockKitItem() {
        super(new Properties().tab(VoodooGroup.INSTANCE).stacksTo(8));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        if (isBound(stack)) {
            checkForNameUpdate(stack, world);
            final var text = Component.translatable(
                    "text.voodoo.taglock_kit.bound",
                    getBoundName(stack)
            );
            text.setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY));
            tooltip.add(text);
        } else {
            final var text = Component.translatable("text.voodoo.taglock_kit.not_bound");
            text.setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY));
            tooltip.add(text);
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = world.getBlockState(pos);
        Player player = context.getPlayer();
        if (world != null &&
                !world.isClientSide &&
                player != null &&
                player.isShiftKeyDown() &&
                state.getBlock() instanceof BedBlock) {
            MinecraftServer server = world.getServer();
            if (state.getValue(BedBlock.PART) != BedPart.HEAD)
                pos = pos.relative(state.getValue(BedBlock.FACING));

            BlockPos finalPos = pos;
            server.getPlayerList().getPlayers().stream()
                    .sorted(Comparator.comparing(ServerPlayer::getSleepTimer))
                    .filter(p -> finalPos.equals(p.getRespawnPosition()))
                    .findFirst()
                    .ifPresent(serverPlayerEntity -> bind(context.getItemInHand(), serverPlayerEntity));
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        if (!world.isClientSide) {
            ItemStack stack = player.getItemInHand(hand);
            if (isBound(stack)) return new InteractionResultHolder<>(InteractionResult.PASS, stack);
            if (player.isShiftKeyDown()) {
                if (!stack.hasTag()) {
                    stack.setTag(new CompoundTag());
                }
                if (!isBound(stack)) {
                    bind(stack, player);
                    return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
                }
            }
        }
        return super.use(world, player, hand);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player user, LivingEntity entity, InteractionHand hand) {
        if (user.getLevel().isClientSide()) return InteractionResult.PASS;
        if (entity instanceof Player player) {
            if (isBound(stack)) return InteractionResult.PASS;
            bind(stack, player);
            return InteractionResult.SUCCESS;
        }
        return super.interactLivingEntity(stack, user, entity, hand);
    }
}
