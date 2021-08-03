package de.morrien.voodoo.item;

import de.morrien.voodoo.VoodooGroup;
import de.morrien.voodoo.util.BindingUtil;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.state.properties.BedPart;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

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
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        if (isBound(stack)) {
            checkForNameUpdate(stack, world);
            final TranslationTextComponent text = new TranslationTextComponent(
                    "text.voodoo.taglock_kit.bound",
                    getBoundName(stack)
            );
            text.setStyle(Style.EMPTY.withColor(TextFormatting.GRAY));
            tooltip.add(text);
        } else {
            final TranslationTextComponent text = new TranslationTextComponent("text.voodoo.taglock_kit.not_bound");
            text.setStyle(Style.EMPTY.withColor(TextFormatting.GRAY));
            tooltip.add(text);
        }
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        World world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = world.getBlockState(pos);
        PlayerEntity player = context.getPlayer();
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
                    .sorted(Comparator.comparing(ServerPlayerEntity::getSleepTimer))
                    .filter(p -> finalPos.equals(p.getRespawnPosition()))
                    .findFirst()
                    .ifPresent(serverPlayerEntity -> bind(context.getItemInHand(), serverPlayerEntity));
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (!world.isClientSide) {
            ItemStack stack = player.getItemInHand(hand);
            if (isBound(stack)) return new ActionResult<>(ActionResultType.PASS, stack);
            if (player.isShiftKeyDown()) {
                if (!stack.hasTag()) {
                    stack.setTag(new CompoundNBT());
                }
                if (!BindingUtil.isBound(stack)) {
                    BindingUtil.bind(stack, player);
                    return new ActionResult<>(ActionResultType.SUCCESS, stack);
                }
            }
        }
        return super.use(world, player, hand);
    }
}
