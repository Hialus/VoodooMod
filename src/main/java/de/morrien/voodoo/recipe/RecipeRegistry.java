package de.morrien.voodoo.recipe;

import de.morrien.voodoo.Voodoo;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class RecipeRegistry {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPES = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Voodoo.MOD_ID);
    public static final RegistryObject<SimpleRecipeSerializer<BindPoppetRecipe>> bindPoppetRecipe = RECIPES.register("bind_poppet", () -> new SimpleRecipeSerializer<>(BindPoppetRecipe::new));
}