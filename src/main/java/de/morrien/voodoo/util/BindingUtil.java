package de.morrien.voodoo.util;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class BindingUtil {
    // Constants used for NBT tags
    public static final String BOUND_UUID = "BoundUUID";
    public static final String BOUND_NAME = "BoundName";

    /**
     * Binds an ItemStack to a player.
     *
     * @param itemStack    The ItemStack that should be bound
     * @param playerEntity The player to bind it to
     */
    public static void bind(ItemStack itemStack, Player playerEntity) {
        bind(itemStack, playerEntity.getUUID(), playerEntity.getName().getString());
    }

    /**
     * Binds an ItemStack to a UUID and name.
     *
     * @param itemStack The ItemStack that should be bound
     * @param uuid      The UUID to bind it to
     * @param name      The name to bind it to
     */
    public static void bind(ItemStack itemStack, UUID uuid, String name) {
        final CompoundTag tag = itemStack.getOrCreateTag();
        tag.putUUID(BOUND_UUID, uuid);
        tag.putString(BOUND_NAME, name);
    }

    /**
     * Transfers the binding from one ItemStack to another.
     * This is used for the binding crafting recipe.
     *
     * @param from The source (Taglock Kit)
     * @param to   The target (Poppet)
     */
    public static void transfer(ItemStack from, ItemStack to) {
        if (!isBound(from)) return;
        bind(to, from.getTag().getUUID(BOUND_UUID), from.getTag().getString(BOUND_NAME));
    }

    /**
     * This method checks if the name of the bound player has changed.
     * If the bound player is online and his name has changed then the NBT tag will be updated.
     *
     * @param stack The bound ItemStack
     * @param world The world instance used to check
     */
    public static void checkForNameUpdate(ItemStack stack, Level world) {
        if (world != null && isBound(stack)) {
            Player player = world.getPlayerByUUID(getBoundUUID(stack));
            if (player != null) {
                final String playerName = player.getName().getString();
                if (!playerName.equals(getBoundName(stack))) {
                    stack.getTag().putString(BOUND_NAME, playerName);
                }
            }
        }
    }

    /**
     * Retrieves the player that is bound to the item.
     * If the ItemStack is not bound or the player is not online it will return null.
     *
     * @param stack The bound ItemStack
     * @param world The world to retrieve the player
     * @return The bound player or null
     */
    public static Player getBoundPlayer(ItemStack stack, Level world) {
        if (isBound(stack)) {
            return world.getPlayerByUUID(getBoundUUID(stack));
        }
        return null;
    }

    /**
     * Checks if the given ItemStack has been bound to a player.
     *
     * @param stack The ItemStack to check
     * @return true if the ItemStack is bound, else false
     */
    public static boolean isBound(ItemStack stack) {
        return stack.hasTag() && stack.getTag().hasUUID(BOUND_UUID);
    }

    /**
     * Retrieves the UUID of the bound player.
     * If the ItemStack is not bound it will return null.
     *
     * @param stack The bound ItemStack
     * @return The UUID of the bound player or null
     */
    public static UUID getBoundUUID(ItemStack stack) {
        if (isBound(stack))
            return stack.getTag().getUUID(BOUND_UUID);
        else
            return null;
    }

    /**
     * Retrieves the name of the bound player.
     * If the ItemStack is not bound it will return null.
     *
     * @param stack The bound ItemStack
     * @return The name of the bound player or null
     */
    public static String getBoundName(ItemStack stack) {
        if (isBound(stack))
            return stack.getTag().getString(BOUND_NAME);
        else
            return null;
    }
}
