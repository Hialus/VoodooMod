package de.morrien.voodoo.container;

import de.morrien.voodoo.item.PoppetItem;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class PoppetShelfContainer extends AbstractContainerMenu {
    private final Container inventory;

    public PoppetShelfContainer(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, new SimpleContainer(9));
    }

    public PoppetShelfContainer(int syncId, Inventory playerInventory, Container inventory) {
        super(ContainerRegistry.poppetShelf, syncId);
        checkContainerSize(inventory, 9);
        this.inventory = inventory;
        inventory.startOpen(playerInventory.player);

        for (int i = 0; i < 9; i++) {
            this.addSlot(new PoppetSlot(inventory, i, 62 + (i % 3) * 18, 17 + (i / 3) * 18));
        }

        layoutPlayerInventorySlots(playerInventory, 8, 84);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot.hasItem()) {
            ItemStack originalStack = slot.getItem();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.getContainerSize()) {
                if (!this.moveItemStackTo(originalStack, this.inventory.getContainerSize(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(originalStack, 0, this.inventory.getContainerSize(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return newStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.inventory.stillValid(player);
    }

    private int addSlotRange(Inventory playerInventory, int index, int x, int y, int amount, int dx) {
        for (int i = 0; i < amount; i++) {
            this.addSlot(new Slot(playerInventory, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    private int addSlotBox(Inventory playerInventory, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0; j < verAmount; j++) {
            index = addSlotRange(playerInventory, index, x, y, horAmount, dx);
            y += dy;
        }
        return index;
    }

    private void layoutPlayerInventorySlots(Inventory playerInventory, int leftCol, int topRow) {
        // Player inventory
        addSlotBox(playerInventory, 9, leftCol, topRow, 9, 18, 3, 18);

        // Hotbar
        topRow += 58;
        addSlotRange(playerInventory, 0, leftCol, topRow, 9, 18);
    }

    public static class PoppetSlot extends Slot {
        public PoppetSlot(Container inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack itemStack) {
            return itemStack.getItem() instanceof PoppetItem;
        }
    }
}