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
    protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(ItemRegistry.needle.get())
                .pattern(" I ")
                .pattern(" B ")
                .pattern(" B ")
                .define('I', Tags.Items.INGOTS_IRON)
                .define('B', Items.IRON_BARS)
                .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(ItemRegistry.taglockKit.get())
                .requires(ItemRegistry.needle.get())
                .requires(Tags.Items.STRING)
                .requires(Items.GLASS_BOTTLE)
                .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ItemRegistry.poppetShelf.get())
                .pattern("CCC")
                .pattern("ODO")
                .pattern("NNN")
                .define('C', Items.GREEN_CARPET)
                .define('O', Tags.Items.OBSIDIAN)
                .define('D', Tags.Items.GEMS_DIAMOND)
                .define('N', Items.RED_NETHER_BRICKS)
                .unlockedBy("has_poppet", has(ItemRegistry.poppetMap.get(BLANK).get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ItemRegistry.poppetMap.get(BLANK).get())
                .pattern("EWE")
                .pattern("SWS")
                .pattern("RNR")
                .define('W', Items.BROWN_WOOL)
                .define('E', Items.COAL)
                .define('S', Items.STRING)
                .define('R', Items.RABBIT_HIDE)
                .define('N', ItemRegistry.needle.get())
                .unlockedBy("has_rabbit_hide", has(Items.RABBIT_HIDE))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ItemRegistry.poppetMap.get(VOODOO).get())
                .pattern("SES")
                .pattern("LPL")
                .pattern("RNR")
                .define('P', ItemRegistry.poppetMap.get(BLANK).get())
                .define('N', ItemRegistry.needle.get())
                .define('R', Items.RABBIT_HIDE)
                .define('E', Items.ENDER_PEARL)
                .define('S', Items.SPIDER_EYE)
                .define('L', Items.LEAD)
                .unlockedBy("has_blank_poppet", has(ItemRegistry.poppetMap.get(BLANK).get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ItemRegistry.poppetMap.get(DEATH_PROTECTION).get())
                .pattern("FTF")
                .pattern("GPG")
                .pattern("RNR")
                .define('P', ItemRegistry.poppetMap.get(BLANK).get())
                .define('N', ItemRegistry.needle.get())
                .define('R', Items.RABBIT_HIDE)
                .define('T', Items.TOTEM_OF_UNDYING)
                .define('G', Items.GOLDEN_APPLE)
                .define('F', Items.RABBIT_FOOT)
                .unlockedBy("has_blank_poppet", has(ItemRegistry.poppetMap.get(BLANK).get()))
                .unlockedBy("has_totem_of_undying", has(Items.TOTEM_OF_UNDYING))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ItemRegistry.poppetMap.get(FIRE_PROTECTION).get())
                .pattern("MFM")
                .pattern("BPB")
                .pattern("RNR")
                .define('P', ItemRegistry.poppetMap.get(BLANK).get())
                .define('N', ItemRegistry.needle.get())
                .define('R', Items.RABBIT_HIDE)
                .define('M', Items.MAGMA_CREAM)
                .define('F', Items.FIRE_CHARGE)
                .define('B', Items.BLAZE_ROD)
                .unlockedBy("has_blank_poppet", has(ItemRegistry.poppetMap.get(BLANK).get()))
                .unlockedBy("entered_nether", ChangeDimensionTrigger.Instance.changedDimensionTo(World.NETHER))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ItemRegistry.poppetMap.get(FALL_PROTECTION).get())
                .pattern("SWS")
                .pattern("FPF")
                .pattern("RNR")
                .define('P', ItemRegistry.poppetMap.get(BLANK).get())
                .define('N', ItemRegistry.needle.get())
                .define('R', Items.RABBIT_HIDE)
                .define('W', Items.WHITE_WOOL)
                .define('F', Items.FEATHER)
                .define('S', Items.STRING)
                .unlockedBy("has_blank_poppet", has(ItemRegistry.poppetMap.get(BLANK).get()))
                .unlockedBy("entered_water", insideOf(Blocks.WATER))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ItemRegistry.poppetMap.get(WATER_PROTECTION).get())
                .pattern("   ")
                .pattern(" P ")
                .pattern("RNR")
                .define('P', ItemRegistry.poppetMap.get(BLANK).get())
                .define('N', ItemRegistry.needle.get())
                .define('R', Items.RABBIT_HIDE)
                .unlockedBy("has_blank_poppet", has(ItemRegistry.poppetMap.get(BLANK).get()))
                .unlockedBy("entered_water", insideOf(Blocks.WATER))
                .save(consumer);

        CustomRecipeBuilder.special(RecipeRegistry.bindPoppetRecipe.get())
                .save(consumer, MOD_ID + ":bind_poppet");
    }
}