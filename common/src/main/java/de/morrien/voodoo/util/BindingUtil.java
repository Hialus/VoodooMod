package de.morrien.voodoo.util;

import de.morrien.voodoo.component.Binding;
import de.morrien.voodoo.component.VoodooComponents;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

import java.util.Optional;
import java.util.UUID;

public class BindingUtil {
    // Constants used for NBT tags of items from older versions
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
        itemStack.set(VoodooComponents.binding.get(), new Binding(uuid, name));
    }

    /**
     * Unbinds an ItemStack
     *
     * @param itemStack The ItemStack that should be unbound
     */
    public static void unbind(ItemStack itemStack) {
        itemStack.remove(VoodooComponents.binding.get());
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
        to.set(VoodooComponents.binding.get(), from.get(VoodooComponents.binding.get()));
    }

    /**
     * Converts the binding of an ItemStack from NBT tags of older versions.
     *
     * @param stack The ItemStack to convert
     */
    public static void migrate(ItemStack stack) {
        final CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) return;
        final CompoundTag tag = data.copyTag();
        if (!tag.contains(BOUND_UUID)) return;
        final Optional<UUID> uuid = tag.read(BOUND_UUID, UUIDUtil.CODEC);
        if (!isBound(stack) && uuid.isPresent()) {
            bind(stack, uuid.get(), tag.getStringOr(BOUND_NAME, ""));
        }
        CustomData.update(DataComponents.CUSTOM_DATA, stack, customTag -> {
            customTag.remove(BOUND_UUID);
            customTag.remove(BOUND_NAME);
        });
    }

    /**
     * This method checks if the name of the bound player has changed.
     * If the bound player is online and his name has changed then the binding will be updated.
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
                    bind(stack, player.getUUID(), playerName);
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
        return stack.has(VoodooComponents.binding.get());
    }

    /**
     * Retrieves the UUID of the bound player.
     * If the ItemStack is not bound it will return null.
     *
     * @param stack The bound ItemStack
     * @return The UUID of the bound player or null
     */
    public static UUID getBoundUUID(ItemStack stack) {
        final Binding binding = stack.get(VoodooComponents.binding.get());
        return binding == null ? null : binding.uuid();
    }

    /**
     * Retrieves the name of the bound player.
     * If the ItemStack is not bound it will return null.
     *
     * @param stack The bound ItemStack
     * @return The name of the bound player or null
     */
    public static String getBoundName(ItemStack stack) {
        final Binding binding = stack.get(VoodooComponents.binding.get());
        return binding == null ? null : binding.name();
    }
}
