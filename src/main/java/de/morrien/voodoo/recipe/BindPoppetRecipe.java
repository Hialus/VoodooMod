package de.morrien.voodoo.recipe;

import de.morrien.voodoo.item.ItemRegistry;
import de.morrien.voodoo.item.PoppetItem;
import de.morrien.voodoo.item.TaglockKitItem;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class BindPoppetRecipe extends SpecialRecipe {
    public BindPoppetRecipe(ResourceLocation idIn) {
        super(idIn);
    }

    private List<ItemStack> getItems(CraftingInventory inv) {
        List<ItemStack> itemStacks = new ArrayList<>();
        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack itemStack = inv.getStackInSlot(i);
            if (!itemStack.isEmpty()) {
                itemStacks.add(itemStack);
            }
        }
        return itemStacks;
    }

    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        List<ItemStack> itemStacks = getItems(inv);
        if (itemStacks.size() != 2) return false;
        ItemStack itemStack1 = itemStacks.get(0);
        ItemStack itemStack2 = itemStacks.get(1);
        if (itemStack2.getItem() == ItemRegistry.taglockKit.get()) {
            final ItemStack tmp = itemStack1;
            itemStack1 = itemStack2;
            itemStack2 = tmp;
        }
        return itemStack1.getItem() == ItemRegistry.taglockKit.get() &&
                TaglockKitItem.isBound(itemStack1) &&
                itemStack2.getItem() instanceof PoppetItem &&
                !PoppetItem.isBound(itemStack2);
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        List<ItemStack> itemStacks = getItems(inv);
        ItemStack itemStack1 = itemStacks.get(0);
        ItemStack itemStack2 = itemStacks.get(1);
        if (itemStack2.getItem() == ItemRegistry.taglockKit.get()) {
            final ItemStack tmp = itemStack1;
            itemStack1 = itemStack2;
            itemStack2 = tmp;
        }
        CompoundNBT compound = new CompoundNBT();
        compound.putUniqueId(PoppetItem.BOUND_UUID, TaglockKitItem.getBoundUUID(itemStack1));
        compound.putString(PoppetItem.BOUND_NAME, TaglockKitItem.getBoundName(itemStack1));
        ItemStack boundPoppet = new ItemStack(itemStack2.getItem());
        boundPoppet.setTag(compound);
        return boundPoppet;
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height > 2;
    }

    @Override
    public IRecipeSerializer<BindPoppetRecipe> getSerializer() {
        return RecipeRegistry.bindPoppetRecipe.get();
    }

}
