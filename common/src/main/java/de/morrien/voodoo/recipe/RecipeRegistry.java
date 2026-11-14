package de.morrien.voodoo.recipe;

import com.mojang.serialization.MapCodec;
import de.morrien.voodoo.platform.RegistrationProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.function.Supplier;

public class RecipeRegistry {
    public static final RegistrationProvider<RecipeSerializer<?>> RECIPES = RegistrationProvider.get(Registries.RECIPE_SERIALIZER);
    public static final Supplier<RecipeSerializer<BindPoppetRecipe>> bindPoppetRecipe = RECIPES.register("bind_poppet", key -> new RecipeSerializer<>(MapCodec.unit(BindPoppetRecipe.INSTANCE), StreamCodec.unit(BindPoppetRecipe.INSTANCE)));
}
