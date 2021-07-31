package de.morrien.voodoo.item;

import de.morrien.voodoo.VoodooGroup;
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

/**
 * Created by Timor Morrien
 */
@SuppressWarnings({"ConstantConditions", "NullableProblems"})
public class TaglockKitItem extends Item {
    private static final String BOUND_UUID = "BoundUUID";
    private static final String BOUND_NAME = "BoundName";

    public TaglockKitItem() {
        super(new Properties().group(VoodooGroup.INSTANCE).maxStackSize(8));
    }

    public static boolean isBound(ItemStack stack) {
        return stack.hasTag() && stack.getTag().hasUniqueId(BOUND_UUID);
    }

    public static UUID getBoundUUID(ItemStack stack) {
        if (isBound(stack))
            return stack.getTag().getUniqueId(BOUND_UUID);
        else
            return null;
    }

    public static String getBoundName(ItemStack stack) {
        if (isBound(stack))
            return stack.getTag().getString(BOUND_NAME);
        else
            return null;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        UUID uuid = getBoundUUID(stack);
        if (uuid == null) return;
        if (worldIn != null) {
            PlayerEntity player = worldIn.getPlayerByUuid(uuid);
            if (player != null) {
                final String playerName = player.getName().getString();
                if (!playerName.equals(getBoundName(stack))) {
                    stack.getTag().putString(BOUND_NAME, playerName);
                }
                tooltip.add(new StringTextComponent(I18n.format("text.voodoo.taglock_kit.bound").replace("&p", playerName)));
                return;
            }
        }
        if (isBound(stack)) {
            tooltip.add(new StringTextComponent(I18n.format("text.voodoo.taglock_kit.bound").replace("&p", getBoundName(stack))));
        }
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        final BlockPos pos = context.getPos();
        for (PlayerProfileCache.ProfileEntry profileEntry : context.getWorld().getServer().getPlayerProfileCache().func_242116_a()) {

        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if (!worldIn.isRemote) {
            ItemStack stack = playerIn.getHeldItem(handIn);
            if (isBound(stack)) return new ActionResult<>(ActionResultType.PASS, stack);
            if (playerIn.isSneaking()) {
                if (!stack.hasTag()) {
                    stack.setTag(new CompoundNBT());
                }
                if (!stack.getTag().hasUniqueId(BOUND_UUID)) {
                    stack.getTag().putUniqueId(BOUND_UUID, playerIn.getUniqueID());
                    stack.getTag().putString(BOUND_NAME, playerIn.getName().getString());
                    return new ActionResult<>(ActionResultType.SUCCESS, stack);
                }
            }
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }
}
