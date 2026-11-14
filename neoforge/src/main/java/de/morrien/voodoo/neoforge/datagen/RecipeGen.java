package de.morrien.voodoo.neoforge.datagen;

import de.morrien.voodoo.item.ItemRegistry;
import de.morrien.voodoo.recipe.BindPoppetRecipe;
import net.minecraft.advancements.criterion.BrewedPotionTrigger;
import net.minecraft.advancements.criterion.ChangeDimensionTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

import static de.morrien.voodoo.Poppet.PoppetType.*;
import static de.morrien.voodoo.Voodoo.MOD_ID;
import static net.minecraft.world.item.Items.*;

public class RecipeGen extends RecipeProvider {
    public RecipeGen(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        shaped(RecipeCategory.MISC, ItemRegistry.needle.get(), 4)
                .pattern(" I ")
                .pattern(" B ")
                .pattern(" B ")
                .define('I', Tags.Items.INGOTS_IRON)
                .define('B', IRON_BARS)
                .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
                .save(output);

        shapeless(RecipeCategory.MISC, ItemRegistry.taglockKit.get())
                .requires(ItemRegistry.needle.get())
                .requires(Tags.Items.STRINGS)
                .requires(GLASS_BOTTLE)
                .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
                .save(output);

        shaped(RecipeCategory.MISC, ItemRegistry.poppetShelf.get())
                .pattern("ACA")
                .pattern("NON")
                .pattern("ACA")
                .define('C', GREEN_CARPET)
                .define('O', Tags.Items.OBSIDIANS)
                .define('A', AMETHYST_SHARD)
                .define('N', RED_NETHER_BRICKS)
                .unlockedBy("has_poppet", has(ItemRegistry.poppetMap.get(BLANK).get()))
                .save(output);

        shaped(RecipeCategory.MISC, ItemRegistry.poppetMap.get(BLANK).get())
                .pattern("EWE")
                .pattern("SWS")
                .pattern("RNR")
                .define('W', BROWN_WOOL)
                .define('E', Ingredient.of(COAL, CHARCOAL))
                .define('S', STRING)
                .define('R', RABBIT_HIDE)
                .define('N', ItemRegistry.needle.get())
                .unlockedBy("has_rabbit_hide", has(RABBIT_HIDE))
                .save(output);

        shaped(RecipeCategory.MISC, ItemRegistry.poppetMap.get(VOODOO).get())
                .pattern("SES")
                .pattern("LPL")
                .pattern("RNR")
                .define('P', ItemRegistry.poppetMap.get(BLANK).get())
                .define('N', ItemRegistry.needle.get())
                .define('R', RABBIT_HIDE)
                .define('E', ENDER_PEARL)
                .define('S', SPIDER_EYE)
                .define('L', LEAD)
                .unlockedBy("has_blank_poppet", has(ItemRegistry.poppetMap.get(BLANK).get()))
                .save(output);

        shaped(RecipeCategory.MISC, ItemRegistry.poppetMap.get(VAMPIRIC).get())
                .pattern("GSG")
                .pattern("CPW")
                .pattern("RNR")
                .define('P', ItemRegistry.poppetMap.get(VOODOO).get())
                .define('N', ItemRegistry.needle.get())
                .define('R', RABBIT_HIDE)
                .define('G', GHAST_TEAR)
                .define('C', CRIMSON_FUNGUS)
                .define('W', WARPED_FUNGUS)
                .define('S', SOUL_LANTERN)
                .unlockedBy("has_voodoo_poppet", has(ItemRegistry.poppetMap.get(VOODOO).get()))
                .save(output);

        shaped(RecipeCategory.MISC, ItemRegistry.poppetMap.get(REFLECTOR).get())
                .pattern("FEF")
                .pattern("CPC")
                .pattern("RNR")
                .define('P', ItemRegistry.poppetMap.get(BLANK).get())
                .define('N', ItemRegistry.needle.get())
                .define('R', RABBIT_HIDE)
                .define('F', Ingredient.of(PUFFERFISH, PUFFERFISH_BUCKET))
                .define('E', END_CRYSTAL)
                .define('C', CACTUS)
                .unlockedBy("has_blank_poppet", has(ItemRegistry.poppetMap.get(BLANK).get()))
                .save(output);

        shaped(RecipeCategory.MISC, ItemRegistry.poppetMap.get(DEATH_PROTECTION).get())
                .pattern("FTF")
                .pattern("GPG")
                .pattern("RNR")
                .define('P', ItemRegistry.poppetMap.get(BLANK).get())
                .define('N', ItemRegistry.needle.get())
                .define('R', RABBIT_HIDE)
                .define('T', TOTEM_OF_UNDYING)
                .define('G', GOLDEN_APPLE)
                .define('F', RABBIT_FOOT)
                .unlockedBy("has_blank_poppet", has(ItemRegistry.poppetMap.get(BLANK).get()))
                .unlockedBy("has_totem_of_undying", has(TOTEM_OF_UNDYING))
                .save(output);

        shaped(RecipeCategory.MISC, ItemRegistry.poppetMap.get(FIRE_PROTECTION).get())
                .pattern("GFG")
                .pattern("BPB")
                .pattern("RNR")
                .define('P', ItemRegistry.poppetMap.get(BLANK).get())
                .define('N', ItemRegistry.needle.get())
                .define('R', RABBIT_HIDE)
                .define('G', Tags.Items.INGOTS_GOLD)
                .define('F', FIRE_CHARGE)
                .define('B', BLAZE_ROD)
                .unlockedBy("has_blank_poppet", has(ItemRegistry.poppetMap.get(BLANK).get()))
                .unlockedBy("entered_nether", ChangeDimensionTrigger.TriggerInstance.changedDimensionTo(Level.NETHER))
                .save(output);

        shaped(RecipeCategory.MISC, ItemRegistry.poppetMap.get(FALL_PROTECTION).get())
                .pattern("SWS")
                .pattern("FPF")
                .pattern("RNR")
                .define('P', ItemRegistry.poppetMap.get(BLANK).get())
                .define('N', ItemRegistry.needle.get())
                .define('R', RABBIT_HIDE)
                .define('W', WHITE_WOOL)
                .define('F', FEATHER)
                .define('S', STRING)
                .unlockedBy("has_blank_poppet", has(ItemRegistry.poppetMap.get(BLANK).get()))
                .save(output);

        shaped(RecipeCategory.MISC, ItemRegistry.poppetMap.get(WATER_PROTECTION).get())
                .pattern("SFS")
                .pattern("KPK")
                .pattern("RNR")
                .define('P', ItemRegistry.poppetMap.get(BLANK).get())
                .define('N', ItemRegistry.needle.get())
                .define('R', RABBIT_HIDE)
                .define('F', Ingredient.of(SALMON_BUCKET, COD_BUCKET, PUFFERFISH_BUCKET, TROPICAL_FISH_BUCKET))
                .define('S', SEA_PICKLE)
                .define('K', KELP)
                .unlockedBy("has_blank_poppet", has(ItemRegistry.poppetMap.get(BLANK).get()))
                .unlockedBy("entered_water", insideOf(Blocks.WATER))
                .save(output);

        shaped(RecipeCategory.MISC, ItemRegistry.poppetMap.get(WITHER_PROTECTION).get())
                .pattern("CBC")
                .pattern("MPH")
                .pattern("RNR")
                .define('P', ItemRegistry.poppetMap.get(BLANK).get())
                .define('N', ItemRegistry.needle.get())
                .define('R', RABBIT_HIDE)
                .define('M', MILK_BUCKET)
                .define('C', Ingredient.of(COAL, CHARCOAL))
                .define('B', BONE_BLOCK)
                .define('H', GOLDEN_APPLE)
                .unlockedBy("has_blank_poppet", has(ItemRegistry.poppetMap.get(BLANK).get()))
                .unlockedBy("entered_nether", ChangeDimensionTrigger.TriggerInstance.changedDimensionTo(Level.NETHER))
                .save(output);

        shaped(RecipeCategory.MISC, ItemRegistry.poppetMap.get(POTION_PROTECTION).get())
                .pattern("GCG")
                .pattern("MPH")
                .pattern("RNR")
                .define('P', ItemRegistry.poppetMap.get(BLANK).get())
                .define('N', ItemRegistry.needle.get())
                .define('R', RABBIT_HIDE)
                .define('M', MILK_BUCKET)
                .define('G', GLISTERING_MELON_SLICE)
                .define('C', GOLDEN_CARROT)
                .define('H', GOLDEN_APPLE)
                .unlockedBy("has_blank_poppet", has(ItemRegistry.poppetMap.get(BLANK).get()))
                .unlockedBy("brewed_potion", BrewedPotionTrigger.TriggerInstance.brewedPotion())
                .save(output);

        shaped(RecipeCategory.MISC, ItemRegistry.poppetMap.get(VOID_PROTECTION).get())
                .pattern("EOE")
                .pattern("MPM")
                .pattern("RNR")
                .define('P', ItemRegistry.poppetMap.get(BLANK).get())
                .define('N', ItemRegistry.needle.get())
                .define('R', RABBIT_HIDE)
                .define('E', ENDER_PEARL)
                .define('O', CRYING_OBSIDIAN)
                .define('M', PHANTOM_MEMBRANE)
                .unlockedBy("has_blank_poppet", has(ItemRegistry.poppetMap.get(BLANK).get()))
                .unlockedBy("has_phantom_membrane", has(PHANTOM_MEMBRANE))
                .save(output);

        shaped(RecipeCategory.MISC, ItemRegistry.poppetMap.get(HUNGER_PROTECTION).get())
                .pattern("CKC")
                .pattern("CPC")
                .pattern("RNR")
                .define('P', ItemRegistry.poppetMap.get(BLANK).get())
                .define('N', ItemRegistry.needle.get())
                .define('R', RABBIT_HIDE)
                .define('C', COOKIE)
                .define('K', CAKE)
                .unlockedBy("has_blank_poppet", has(ItemRegistry.poppetMap.get(BLANK).get()))
                .save(output);

        shaped(RecipeCategory.MISC, ItemRegistry.poppetMap.get(EXPLOSION_PROTECTION).get())
                .pattern("OIO")
                .pattern("WPW")
                .pattern("RNR")
                .define('P', ItemRegistry.poppetMap.get(BLANK).get())
                .define('N', ItemRegistry.needle.get())
                .define('R', RABBIT_HIDE)
                .define('W', WATER_BUCKET)
                .define('I', IRON_BLOCK)
                .define('O', OBSIDIAN)
                .unlockedBy("has_blank_poppet", has(ItemRegistry.poppetMap.get(BLANK).get()))
                .unlockedBy("has_obsidian", has(OBSIDIAN))
                .save(output);

        shaped(RecipeCategory.MISC, ItemRegistry.poppetMap.get(PROJECTILE_PROTECTION).get())
                .pattern("THT")
                .pattern("SPS")
                .pattern("RNR")
                .define('P', ItemRegistry.poppetMap.get(BLANK).get())
                .define('N', ItemRegistry.needle.get())
                .define('R', RABBIT_HIDE)
                .define('H', Ingredient.of(HONEY_BLOCK, SLIME_BLOCK))
                .define('S', SHIELD)
                .define('T', TARGET)
                .unlockedBy("has_blank_poppet", has(ItemRegistry.poppetMap.get(BLANK).get()))
                .save(output);

        shaped(RecipeCategory.MISC, ItemRegistry.poppetMap.get(VOODOO_PROTECTION).get())
                .pattern("MEM")
                .pattern("TPT")
                .pattern("RNR")
                .define('P', ItemRegistry.poppetMap.get(BLANK).get())
                .define('N', ItemRegistry.needle.get())
                .define('R', RABBIT_HIDE)
                .define('M', PHANTOM_MEMBRANE)
                .define('E', ENDER_PEARL)
                .define('T', TNT)
                .unlockedBy("has_blank_poppet", has(ItemRegistry.poppetMap.get(BLANK).get()))
                .save(output);

        SpecialRecipeBuilder.special(BindPoppetRecipe::new)
                .save(output, MOD_ID + ":bind_poppet");
    }

    public static class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
            super(packOutput, registries);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new RecipeGen(registries, output);
        }

        @Override
        public String getName() {
            return "Voodoo Recipes";
        }
    }
}