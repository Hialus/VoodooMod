package de.morrien.voodoo.recipe;

import de.morrien.voodoo.Voodoo;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class RecipeRegistry {
    public static final DeferredRegister<IRecipeSerializer<?>> RECIPES = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Voodoo.MOD_ID);
    public static final RegistryObject<SpecialRecipeSerializer<BindPoppetRecipe>> bindPoppetRecipe = RECIPES.register("bind_poppet", () -> new SpecialRecipeSerializer<>(BindPoppetRecipe::new));
}