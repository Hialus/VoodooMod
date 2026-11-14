package de.morrien.voodoo.container;

import de.morrien.voodoo.item.PoppetItem;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * Created by Phoebe Morrien
 */
public class PoppetShelfContainer extends AbstractContainerMenu {
    protected Container poppetShelf;
    protected Player playerEntity;
    protected Inventory playerInventory;

    public PoppetShelfContainer(int windowId, Inventory playerInventory) {
        this(windowId, playerInventory, new SimpleContainer(9));
    }

    public PoppetShelfContainer(int windowId, Inventory playerInventory, Container poppetShelf) {
        super(ContainerRegistry.poppetShelf.get(), windowId);
        checkContainerSize(poppetShelf, 9);
        this.poppetShelf = poppetShelf;
        this.playerEntity = playerInventory.player;
        this.playerInventory = playerInventory;

        for (int i = 0; i < 9; i++) {
            addSlot(new Slot(poppetShelf, i, 62 + (i % 3) * 18, 17 + (i / 3) * 18) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return stack.getItem() instanceof PoppetItem;
                }
            });
        }
        layoutPlayerInventorySlots(8, 84);
    }

    private int addSlotRange(Container handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0; i < amount; i++) {
            addSlot(new Slot(handler, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    private int addSlotBox(Container handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0; j < verAmount; j++) {
            index = addSlotRange(handler, index, x, y, horAmount, dx);
            y += dy;
        }
        return index;
    }

    private void layoutPlayerInventorySlots(int leftCol, int topRow) {
        // Player inventory
        addSlotBox(playerInventory, 9, leftCol, topRow, 9, 18, 3, 18);

        // Hotbar
        topRow += 58;
        addSlotRange(playerInventory, 0, leftCol, topRow, 9, 18);
    }

    @Override
    public boolean stillValid(Player player) {
        return poppetShelf.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            int containerSlots = slots.size() - player.getInventory().getNonEquipmentItems().size();

            if (index < containerSlots) {
                if (!this.moveItemStackTo(itemstack1, containerSlots, slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, containerSlots, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.getCount() == 0) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
        }

        return itemstack;
    }
}
