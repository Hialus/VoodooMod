package de.morrien.voodoo.datagen;

import de.morrien.voodoo.item.ItemRegistry;
import de.morrien.voodoo.recipe.RecipeRegistry;
import net.minecraft.advancements.criterion.ChangeDimensionTrigger;
import net.minecraft.block.Blocks;
import net.minecraft.data.*;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

import static de.morrien.voodoo.Poppet.PoppetType.*;
import static de.morrien.voodoo.Voodoo.MOD_ID;

public class RecipeGen extends RecipeProvider {
    public RecipeGen(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
        ShapedRecipeBuilder.shapedRecipe(ItemRegistry.needle.get())
                .patternLine(" I ")
                .patternLine(" B ")
                .patternLine(" B ")
                .key('I', Tags.Items.INGOTS_IRON)
                .key('B', Items.IRON_BARS)
                .addCriterion("has_iron", hasItem(Tags.Items.INGOTS_IRON))
                .build(consumer);

        ShapelessRecipeBuilder.shapelessRecipe(ItemRegistry.taglockKit.get())
                .addIngredient(ItemRegistry.needle.get())
                .addIngredient(Tags.Items.STRING)
                .addIngredient(Items.GLASS_BOTTLE)
                .addCriterion("has_iron", hasItem(Tags.Items.INGOTS_IRON))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(ItemRegistry.poppetShelf.get())
                .patternLine("CCC")
                .patternLine("ODO")
                .patternLine("NNN")
                .key('C', Items.GREEN_CARPET)
                .key('O', Tags.Items.OBSIDIAN)
                .key('D', Tags.Items.GEMS_DIAMOND)
                .key('N', Items.RED_NETHER_BRICKS)
                .addCriterion("has_poppet", hasItem(ItemRegistry.poppetMap.get(BLANK).get()))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(ItemRegistry.poppetMap.get(BLANK).get())
                .patternLine("EWE")
                .patternLine("SWS")
                .patternLine("RNR")
                .key('W', Items.BROWN_WOOL)
                .key('E', Items.COAL)
                .key('S', Items.STRING)
                .key('R', Items.RABBIT_HIDE)
                .key('N', ItemRegistry.needle.get())
                .addCriterion("has_rabbit_hide", hasItem(Items.RABBIT_HIDE))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(ItemRegistry.poppetMap.get(VOODOO).get())
                .patternLine("SES")
                .patternLine("LPL")
                .patternLine("RNR")
                .key('P', ItemRegistry.poppetMap.get(BLANK).get())
                .key('N', ItemRegistry.needle.get())
                .key('R', Items.RABBIT_HIDE)
                .key('E', Items.ENDER_PEARL)
                .key('S', Items.SPIDER_EYE)
                .key('L', Items.LEAD)
                .addCriterion("has_blank_poppet", hasItem(ItemRegistry.poppetMap.get(BLANK).get()))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(ItemRegistry.poppetMap.get(DEATH_PROTECTION).get())
                .patternLine("FTF")
                .patternLine("GPG")
                .patternLine("RNR")
                .key('P', ItemRegistry.poppetMap.get(BLANK).get())
                .key('N', ItemRegistry.needle.get())
                .key('R', Items.RABBIT_HIDE)
                .key('T', Items.TOTEM_OF_UNDYING)
                .key('G', Items.GOLDEN_APPLE)
                .key('F', Items.RABBIT_FOOT)
                .addCriterion("has_blank_poppet", hasItem(ItemRegistry.poppetMap.get(BLANK).get()))
                .addCriterion("has_totem_of_undying", hasItem(Items.TOTEM_OF_UNDYING))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(ItemRegistry.poppetMap.get(FIRE_PROTECTION).get())
                .patternLine("MFM")
                .patternLine("BPB")
                .patternLine("RNR")
                .key('P', ItemRegistry.poppetMap.get(BLANK).get())
                .key('N', ItemRegistry.needle.get())
                .key('R', Items.RABBIT_HIDE)
                .key('M', Items.MAGMA_CREAM)
                .key('F', Items.FIRE_CHARGE)
                .key('B', Items.BLAZE_ROD)
                .addCriterion("has_blank_poppet", hasItem(ItemRegistry.poppetMap.get(BLANK).get()))
                .addCriterion("entered_nether", ChangeDimensionTrigger.Instance.toWorld(World.THE_NETHER))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(ItemRegistry.poppetMap.get(FALL_PROTECTION).get())
                .patternLine("SWS")
                .patternLine("FPF")
                .patternLine("RNR")
                .key('P', ItemRegistry.poppetMap.get(BLANK).get())
                .key('N', ItemRegistry.needle.get())
                .key('R', Items.RABBIT_HIDE)
                .key('W', Items.WHITE_WOOL)
                .key('F', Items.FEATHER)
                .key('S', Items.STRING)
                .addCriterion("has_blank_poppet", hasItem(ItemRegistry.poppetMap.get(BLANK).get()))
                .addCriterion("entered_water", enteredBlock(Blocks.WATER))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(ItemRegistry.poppetMap.get(WATER_PROTECTION).get())
                .patternLine("   ")
                .patternLine(" P ")
                .patternLine("RNR")
                .key('P', ItemRegistry.poppetMap.get(BLANK).get())
                .key('N', ItemRegistry.needle.get())
                .key('R', Items.RABBIT_HIDE)
                .addCriterion("has_blank_poppet", hasItem(ItemRegistry.poppetMap.get(BLANK).get()))
                .addCriterion("entered_water", enteredBlock(Blocks.WATER))
                .build(consumer);

        CustomRecipeBuilder.customRecipe(RecipeRegistry.bindPoppetRecipe.get())
                .build(consumer, MOD_ID + ":bind_poppet");
    }
}