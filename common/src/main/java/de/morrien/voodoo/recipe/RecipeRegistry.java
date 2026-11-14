package de.morrien.voodoo.recipe;

import de.morrien.voodoo.platform.RegistrationProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.function.Supplier;

public class RecipeRegistry {
    public static final RegistrationProvider<RecipeSerializer<?>> RECIPES = RegistrationProvider.get(Registries.RECIPE_SERIALIZER);
    public static final Supplier<SimpleCraftingRecipeSerializer<BindPoppetRecipe>> bindPoppetRecipe = RECIPES.register("bind_poppet", key -> new SimpleCraftingRecipeSerializer<>(BindPoppetRecipe::new));
}
