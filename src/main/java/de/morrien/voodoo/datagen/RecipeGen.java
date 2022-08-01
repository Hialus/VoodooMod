package de.morrien.voodoo.datagen;

import de.morrien.voodoo.item.ItemRegistry;
import de.morrien.voodoo.recipe.RecipeRegistry;
import net.minecraft.advancements.critereon.BrewedPotionTrigger;
import net.minecraft.advancements.critereon.ChangeDimensionTrigger;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import java.util.function.Consumer;

import static de.morrien.voodoo.Poppet.PoppetType.*;
import static de.morrien.voodoo.Voodoo.MOD_ID;
import static net.minecraft.world.item.Items.*;

public class RecipeGen extends RecipeProvider {
    public RecipeGen(DataGenerator generator) {
        super(generator);
    }

    public void buildCraftingRecipes2(Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(ItemRegistry.needle, 4)
                .pattern(" I ")
                .pattern(" B ")
                .pattern(" B ")
                .define('I', IRON_INGOT)
                .define('B', IRON_BARS)
                .unlockedBy("has_iron", has(IRON_INGOT))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(ItemRegistry.taglockKit)
                .requires(ItemRegistry.needle)
                .requires(STRING)
                .requires(GLASS_BOTTLE)
                .unlockedBy("has_iron", has(IRON_INGOT))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ItemRegistry.poppetShelf)
                .pattern("ACA")
                .pattern("NON")
                .pattern("ACA")
                .define('C', GREEN_CARPET)
                .define('O', OBSIDIAN)
                .define('A', AMETHYST_SHARD)
                .define('N', RED_NETHER_BRICKS)
                .unlockedBy("has_poppet", has(ItemRegistry.poppetMap.get(BLANK)))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ItemRegistry.poppetMap.get(BLANK))
                .pattern("EWE")
                .pattern("SWS")
                .pattern("RNR")
                .define('W', BROWN_WOOL)
                .define('E', Ingredient.of(COAL, CHARCOAL))
                .define('S', STRING)
                .define('R', RABBIT_HIDE)
                .define('N', ItemRegistry.needle)
                .unlockedBy("has_rabbit_hide", has(RABBIT_HIDE))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ItemRegistry.poppetMap.get(VOODOO))
                .pattern("SES")
                .pattern("LPL")
                .pattern("RNR")
                .define('P', ItemRegistry.poppetMap.get(BLANK))
                .define('N', ItemRegistry.needle)
                .define('R', RABBIT_HIDE)
                .define('E', ENDER_PEARL)
                .define('S', SPIDER_EYE)
                .define('L', LEAD)
                .unlockedBy("has_blank_poppet", has(ItemRegistry.poppetMap.get(BLANK)))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ItemRegistry.poppetMap.get(VAMPIRIC))
                .pattern("GSG")
                .pattern("CPW")
                .pattern("RNR")
                .define('P', ItemRegistry.poppetMap.get(VOODOO))
                .define('N', ItemRegistry.needle)
                .define('R', RABBIT_HIDE)
                .define('G', GHAST_TEAR)
                .define('C', CRIMSON_FUNGUS)
                .define('W', WARPED_FUNGUS)
                .define('S', SOUL_LANTERN)
                .unlockedBy("has_voodoo_poppet", has(ItemRegistry.poppetMap.get(VOODOO)))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ItemRegistry.poppetMap.get(REFLECTOR))
                .pattern("FEF")
                .pattern("CPC")
                .pattern("RNR")
                .define('P', ItemRegistry.poppetMap.get(BLANK))
                .define('N', ItemRegistry.needle)
                .define('R', RABBIT_HIDE)
                .define('F', Ingredient.of(PUFFERFISH, PUFFERFISH_BUCKET))
                .define('E', END_CRYSTAL)
                .define('C', CACTUS)
                .unlockedBy("has_blank_poppet", has(ItemRegistry.poppetMap.get(BLANK)))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ItemRegistry.poppetMap.get(DEATH_PROTECTION))
                .pattern("FTF")
                .pattern("GPG")
                .pattern("RNR")
                .define('P', ItemRegistry.poppetMap.get(BLANK))
                .define('N', ItemRegistry.needle)
                .define('R', RABBIT_HIDE)
                .define('T', TOTEM_OF_UNDYING)
                .define('G', GOLDEN_APPLE)
                .define('F', RABBIT_FOOT)
                .unlockedBy("has_blank_poppet", has(ItemRegistry.poppetMap.get(BLANK)))
                .unlockedBy("has_totem_of_undying", has(TOTEM_OF_UNDYING))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ItemRegistry.poppetMap.get(FIRE_PROTECTION))
                .pattern("GFG")
                .pattern("BPB")
                .pattern("RNR")
                .define('P', ItemRegistry.poppetMap.get(BLANK))
                .define('N', ItemRegistry.needle)
                .define('R', RABBIT_HIDE)
                .define('G', GOLD_INGOT)
                .define('F', FIRE_CHARGE)
                .define('B', BLAZE_ROD)
                .unlockedBy("has_blank_poppet", has(ItemRegistry.poppetMap.get(BLANK)))
                .unlockedBy("entered_nether", ChangeDimensionTrigger.TriggerInstance.changedDimensionTo(Level.NETHER))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ItemRegistry.poppetMap.get(FALL_PROTECTION))
                .pattern("SWS")
                .pattern("FPF")
                .pattern("RNR")
                .define('P', ItemRegistry.poppetMap.get(BLANK))
                .define('N', ItemRegistry.needle)
                .define('R', RABBIT_HIDE)
                .define('W', WHITE_WOOL)
                .define('F', FEATHER)
                .define('S', STRING)
                .unlockedBy("has_blank_poppet", has(ItemRegistry.poppetMap.get(BLANK)))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ItemRegistry.poppetMap.get(WATER_PROTECTION))
                .pattern("SFS")
                .pattern("KPK")
                .pattern("RNR")
                .define('P', ItemRegistry.poppetMap.get(BLANK))
                .define('N', ItemRegistry.needle)
                .define('R', RABBIT_HIDE)
                .define('F', Ingredient.of(SALMON_BUCKET, COD_BUCKET, PUFFERFISH_BUCKET, TROPICAL_FISH_BUCKET))
                .define('S', SEA_PICKLE)
                .define('K', KELP)
                .unlockedBy("has_blank_poppet", has(ItemRegistry.poppetMap.get(BLANK)))
                .unlockedBy("entered_water", insideOf(Blocks.WATER))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ItemRegistry.poppetMap.get(WITHER_PROTECTION))
                .pattern("CBC")
                .pattern("MPH")
                .pattern("RNR")
                .define('P', ItemRegistry.poppetMap.get(BLANK))
                .define('N', ItemRegistry.needle)
                .define('R', RABBIT_HIDE)
                .define('M', MILK_BUCKET)
                .define('C', Ingredient.of(COAL, CHARCOAL))
                .define('B', BONE_BLOCK)
                .define('H', GOLDEN_APPLE)
                .unlockedBy("has_blank_poppet", has(ItemRegistry.poppetMap.get(BLANK)))
                .unlockedBy("entered_nether", ChangeDimensionTrigger.TriggerInstance.changedDimensionTo(Level.NETHER))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ItemRegistry.poppetMap.get(POTION_PROTECTION))
                .pattern("GCG")
                .pattern("MPH")
                .pattern("RNR")
                .define('P', ItemRegistry.poppetMap.get(BLANK))
                .define('N', ItemRegistry.needle)
                .define('R', RABBIT_HIDE)
                .define('M', MILK_BUCKET)
                .define('G', GLISTERING_MELON_SLICE)
                .define('C', GOLDEN_CARROT)
                .define('H', GOLDEN_APPLE)
                .unlockedBy("has_blank_poppet", has(ItemRegistry.poppetMap.get(BLANK)))
                .unlockedBy("brewed_potion", BrewedPotionTrigger.TriggerInstance.brewedPotion())
                .save(consumer);

        ShapedRecipeBuilder.shaped(ItemRegistry.poppetMap.get(VOID_PROTECTION))
                .pattern("EOE")
                .pattern("MPM")
                .pattern("RNR")
                .define('P', ItemRegistry.poppetMap.get(BLANK))
                .define('N', ItemRegistry.needle)
                .define('R', RABBIT_HIDE)
                .define('E', ENDER_PEARL)
                .define('O', CRYING_OBSIDIAN)
                .define('M', PHANTOM_MEMBRANE)
                .unlockedBy("has_blank_poppet", has(ItemRegistry.poppetMap.get(BLANK)))
                .unlockedBy("has_phantom_membrane", has(PHANTOM_MEMBRANE))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ItemRegistry.poppetMap.get(HUNGER_PROTECTION))
                .pattern("CKC")
                .pattern("CPC")
                .pattern("RNR")
                .define('P', ItemRegistry.poppetMap.get(BLANK))
                .define('N', ItemRegistry.needle)
                .define('R', RABBIT_HIDE)
                .define('C', COOKIE)
                .define('K', CAKE)
                .unlockedBy("has_blank_poppet", has(ItemRegistry.poppetMap.get(BLANK)))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ItemRegistry.poppetMap.get(EXPLOSION_PROTECTION))
                .pattern("OIO")
                .pattern("WPW")
                .pattern("RNR")
                .define('P', ItemRegistry.poppetMap.get(BLANK))
                .define('N', ItemRegistry.needle)
                .define('R', RABBIT_HIDE)
                .define('W', WATER_BUCKET)
                .define('I', IRON_BLOCK)
                .define('O', OBSIDIAN)
                .unlockedBy("has_blank_poppet", has(ItemRegistry.poppetMap.get(BLANK)))
                .unlockedBy("has_obsidian", has(OBSIDIAN))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ItemRegistry.poppetMap.get(PROJECTILE_PROTECTION))
                .pattern("THT")
                .pattern("SPS")
                .pattern("RNR")
                .define('P', ItemRegistry.poppetMap.get(BLANK))
                .define('N', ItemRegistry.needle)
                .define('R', RABBIT_HIDE)
                .define('H', Ingredient.of(HONEY_BLOCK, SLIME_BLOCK))
                .define('S', SHIELD)
                .define('T', TARGET)
                .unlockedBy("has_blank_poppet", has(ItemRegistry.poppetMap.get(BLANK)))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ItemRegistry.poppetMap.get(VOODOO_PROTECTION))
                .pattern("MEM")
                .pattern("TPT")
                .pattern("RNR")
                .define('P', ItemRegistry.poppetMap.get(BLANK))
                .define('N', ItemRegistry.needle)
                .define('R', RABBIT_HIDE)
                .define('M', PHANTOM_MEMBRANE)
                .define('E', ENDER_PEARL)
                .define('T', TNT)
                .unlockedBy("has_blank_poppet", has(ItemRegistry.poppetMap.get(BLANK)))
                .save(consumer);

        SpecialRecipeBuilder.special(RecipeRegistry.bindPoppetRecipe)
                .save(consumer, MOD_ID + ":bind_poppet");
    }
}