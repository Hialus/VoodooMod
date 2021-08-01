package de.morrien.voodoo.item;

import de.morrien.voodoo.VoodooGroup;
import de.morrien.voodoo.VoodooUtil;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.client.resources.I18n;
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
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static de.morrien.voodoo.VoodooUtil.*;

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
            tooltip.add(new TranslationTextComponent("text.voodoo.taglock_kit.bound", getBoundName(stack)));
        } else {
            tooltip.add(new TranslationTextComponent("text.voodoo.taglock_kit.not_bound"));
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
                if (!VoodooUtil.isBound(stack)) {
                    VoodooUtil.bind(stack, player);
                    return new ActionResult<>(ActionResultType.SUCCESS, stack);
                }
            }
        }
        return super.use(world, player, hand);
    }
}
