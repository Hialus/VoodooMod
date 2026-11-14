package de.morrien.voodoo.recipe;

import de.morrien.voodoo.item.ItemRegistry;
import de.morrien.voodoo.item.PoppetItem;
import de.morrien.voodoo.util.BindingUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

import static de.morrien.voodoo.Poppet.PoppetType.BLANK;

public class BindPoppetRecipe extends CustomRecipe {
    public BindPoppetRecipe(CraftingBookCategory category) {
        super(category);
    }

    private List<ItemStack> getItems(CraftingInput inv) {
        List<ItemStack> itemStacks = new ArrayList<>();
        for (int i = 0; i < inv.size(); ++i) {
            ItemStack itemStack = inv.getItem(i);
            if (!itemStack.isEmpty()) {
                itemStacks.add(itemStack);
            }
        }
        return itemStacks;
    }

    @Override
    public boolean matches(CraftingInput inv, Level world) {
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
                BindingUtil.isBound(itemStack1) &&
                itemStack2.getItem() instanceof PoppetItem &&
                !itemStack2.getItem().equals(ItemRegistry.poppetMap.get(BLANK).get()) &&
                !BindingUtil.isBound(itemStack2);
    }

    @Override
    public ItemStack assemble(CraftingInput inv, HolderLookup.Provider registries) {
        List<ItemStack> itemStacks = getItems(inv);
        ItemStack itemStack1 = itemStacks.get(0);
        ItemStack itemStack2 = itemStacks.get(1);
        if (itemStack2.getItem() == ItemRegistry.taglockKit.get()) {
            final ItemStack tmp = itemStack1;
            itemStack1 = itemStack2;
            itemStack2 = tmp;
        }
        ItemStack boundPoppet = itemStack2.copyWithCount(1);
        BindingUtil.transfer(itemStack1, boundPoppet);
        return boundPoppet;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.bindPoppetRecipe.get();
    }

}
