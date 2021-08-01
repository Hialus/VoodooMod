package de.morrien.voodoo;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

import java.util.UUID;

public class VoodooUtil {
    public static final String BOUND_UUID = "BoundUUID";
    public static final String BOUND_NAME = "BoundName";

    public static void bind(ItemStack itemStack, PlayerEntity playerEntity) {
        bind(itemStack, playerEntity.getUUID(), playerEntity.getName().getString());
    }

    public static void bind(ItemStack itemStack, UUID uuid, String name) {
        final CompoundNBT tag = itemStack.getOrCreateTag();
        tag.putUUID(BOUND_UUID, uuid);
        tag.putString(BOUND_NAME, name);
    }

    public static void transfer(ItemStack from, ItemStack to) {
        if (!isBound(from)) return;
        bind(to, from.getTag().getUUID(BOUND_UUID), from.getTag().getString(BOUND_NAME));
    }

    public static void checkForNameUpdate(ItemStack stack, World world) {
        if (world != null && isBound(stack)) {
            PlayerEntity player = world.getPlayerByUUID(getBoundUUID(stack));
            if (player != null) {
                final String playerName = player.getName().getString();
                if (!playerName.equals(getBoundName(stack))) {
                    stack.getTag().putString(BOUND_NAME, playerName);
                }
            }
        }
    }

    public static PlayerEntity getBoundPlayer(ItemStack stack, World world) {
        if (isBound(stack)) {
            return world.getPlayerByUUID(getBoundUUID(stack));
        }
        return null;
    }

    public static boolean isBound(ItemStack stack) {
        return stack.hasTag() && stack.getTag().hasUUID(BOUND_UUID);
    }

    public static UUID getBoundUUID(ItemStack stack) {
        if (isBound(stack))
            return stack.getTag().getUUID(BOUND_UUID);
        else
            return null;
    }

    public static String getBoundName(ItemStack stack) {
        if (isBound(stack))
            return stack.getTag().getString(BOUND_NAME);
        else
            return null;
    }
}
