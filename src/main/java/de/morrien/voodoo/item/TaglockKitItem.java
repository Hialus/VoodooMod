package de.morrien.voodoo.item;

import de.morrien.voodoo.VoodooGroup;
import de.morrien.voodoo.VoodooUtil;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.tileentity.BedTileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

import net.minecraft.item.Item.Properties;

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
        UUID uuid = getBoundUUID(stack);
        if (uuid == null) return;
        if (world != null) {
            PlayerEntity player = world.getPlayerByUUID(uuid);
            if (player != null) {
                final String playerName = player.getName().getString();
                if (!playerName.equals(getBoundName(stack))) {
                    stack.getTag().putString(BOUND_NAME, playerName);
                }
                tooltip.add(new StringTextComponent(I18n.get("text.voodoo.taglock_kit.bound").replace("&p", playerName)));
                return;
            }
        }
        if (isBound(stack)) {
            tooltip.add(new StringTextComponent(I18n.get("text.voodoo.taglock_kit.bound").replace("&p", getBoundName(stack))));
        }
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        final BlockPos pos = context.getClickedPos();
        //for (PlayerProfileCache.ProfileEntry profileEntry : context.getWorld().getServer().getPlayerProfileCache().load()) {
//
        //}
        return super.useOn(context);
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
