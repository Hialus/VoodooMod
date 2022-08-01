package de.morrien.voodoo.recipe;

import de.morrien.voodoo.Voodoo;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;

public class RecipeRegistry {
    public static final SimpleRecipeSerializer<BindPoppetRecipe> bindPoppetRecipe = new SimpleRecipeSerializer<>(BindPoppetRecipe::new);

    public static void register() {
        Registry.register(Registry.RECIPE_SERIALIZER, new ResourceLocation(Voodoo.MOD_ID, "bind_poppet"), bindPoppetRecipe);
    }
}