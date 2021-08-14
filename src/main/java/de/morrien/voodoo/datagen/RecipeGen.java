package de.morrien.voodoo.datagen;

import de.morrien.voodoo.item.ItemRegistry;
import de.morrien.voodoo.recipe.RecipeRegistry;
import net.minecraft.advancements.criterion.BrewedPotionTrigger;
import net.minecraft.advancements.criterion.ChangeDimensionTrigger;
import net.minecraft.block.Blocks;
import net.minecraft.data.*;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

import static de.morrien.voodoo.Poppet.PoppetType.*;
import static de.morrien.voodoo.Voodoo.MOD_ID;
import static net.minecraft.item.Items.*;

public class RecipeGen extends RecipeProvider {
    public RecipeGen(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(ItemRegistry.needle.get(), 4)
                .pattern(" I ")
                .pattern(" B ")
                .pattern(" B ")
                .define('I', Tags.Items.INGOTS_IRON)
                .define('B', IRON_BARS)
                .unlocks("has_iron", has(Tags.Items.INGOTS_IRON))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(ItemRegistry.taglockKit.get())
                .requires(ItemRegistry.needle.get())
                .requires(Tags.Items.STRING)
                .requires(GLASS_BOTTLE)
                .unlocks("has_iron", has(Tags.Items.INGOTS_IRON))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ItemRegistry.poppetShelf.get())
                .pattern("CCC")
                .pattern("ODO")
                .pattern("NNN")
                .define('C', GREEN_CARPET)
                .define('O', Tags.Items.OBSIDIAN)
                .define('D', Tags.Items.GEMS_DIAMOND)
                .define('N', RED_NETHER_BRICKS)
                .unlocks("has_poppet", has(ItemRegistry.poppetMap.get(BLANK).get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ItemRegistry.poppetMap.get(BLANK).get())
                .pattern("EWE")
                .pattern("SWS")
                .pattern("RNR")
                .define('W', BROWN_WOOL)
                .define('E', Ingredient.of(COAL, CHARCOAL))
                .define('S', STRING)
                .define('R', RABBIT_HIDE)
                .define('N', ItemRegistry.needle.get())
                .unlocks("has_rabbit_hide", has(RABBIT_HIDE))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ItemRegistry.poppetMap.get(VOODOO).get())
                .pattern("SES")
                .pattern("LPL")
                .pattern("RNR")
                .define('P', ItemRegistry.poppetMap.get(BLANK).get())
                .define('N', ItemRegistry.needle.get())
                .define('R', RABBIT_HIDE)
                .define('E', ENDER_PEARL)
                .define('S', SPIDER_EYE)
                .define('L', LEAD)
                .unlocks("has_blank_poppet", has(ItemRegistry.poppetMap.get(BLANK).get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ItemRegistry.poppetMap.get(VAMPIRIC).get())
                .pattern("GSG")
                .pattern("CPW")
                .pattern("RNR")
                .define('P', ItemRegistry.poppetMap.get(VOODOO).get())
                .define('N', ItemRegistry.needle.get())
                .define('R', RABBIT_HIDE)
                .define('G', GHAST_TEAR)
                .define('C', QUARTZ)
                .define('W', NETHER_WART)
                .define('S', SOUL_SAND)
                .unlocks("has_voodoo_poppet", has(ItemRegistry.poppetMap.get(VOODOO).get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ItemRegistry.poppetMap.get(REFLECTOR).get())
                .pattern("FEF")
                .pattern("CPC")
                .pattern("RNR")
                .define('P', ItemRegistry.poppetMap.get(BLANK).get())
                .define('N', ItemRegistry.needle.get())
                .define('R', RABBIT_HIDE)
                .define('F', Ingredient.of(PUFFERFISH, PUFFERFISH_BUCKET))
                .define('E', END_CRYSTAL)
                .define('C', CACTUS)
                .unlocks("has_blank_poppet", has(ItemRegistry.poppetMap.get(BLANK).get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ItemRegistry.poppetMap.get(DEATH_PROTECTION).get())
                .pattern("FTF")
                .pattern("GPG")
                .pattern("RNR")
                .define('P', ItemRegistry.poppetMap.get(BLANK).get())
                .define('N', ItemRegistry.needle.get())
                .define('R', RABBIT_HIDE)
                .define('T', TOTEM_OF_UNDYING)
                .define('G', GOLDEN_APPLE)
                .define('F', RABBIT_FOOT)
                .unlocks("has_blank_poppet", has(ItemRegistry.poppetMap.get(BLANK).get()))
                .unlocks("has_totem_of_undying", has(TOTEM_OF_UNDYING))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ItemRegistry.poppetMap.get(FIRE_PROTECTION).get())
                .pattern("GFG")
                .pattern("BPB")
                .pattern("RNR")
                .define('P', ItemRegistry.poppetMap.get(BLANK).get())
                .define('N', ItemRegistry.needle.get())
                .define('R', RABBIT_HIDE)
                .define('G', Tags.Items.INGOTS_GOLD)
                .define('F', FIRE_CHARGE)
                .define('B', BLAZE_ROD)
                .unlocks("has_blank_poppet", has(ItemRegistry.poppetMap.get(BLANK).get()))
                .unlocks("entered_nether", ChangeDimensionTrigger.Instance.changedDimensionTo(DimensionType.NETHER))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ItemRegistry.poppetMap.get(FALL_PROTECTION).get())
                .pattern("SWS")
                .pattern("FPF")
                .pattern("RNR")
                .define('P', ItemRegistry.poppetMap.get(BLANK).get())
                .define('N', ItemRegistry.needle.get())
                .define('R', RABBIT_HIDE)
                .define('W', WHITE_WOOL)
                .define('F', FEATHER)
                .define('S', STRING)
                .unlocks("has_blank_poppet", has(ItemRegistry.poppetMap.get(BLANK).get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ItemRegistry.poppetMap.get(WATER_PROTECTION).get())
                .pattern("SFS")
                .pattern("KPK")
                .pattern("RNR")
                .define('P', ItemRegistry.poppetMap.get(BLANK).get())
                .define('N', ItemRegistry.needle.get())
                .define('R', RABBIT_HIDE)
                .define('F', Ingredient.of(SALMON_BUCKET, COD_BUCKET, PUFFERFISH_BUCKET, TROPICAL_FISH_BUCKET))
                .define('S', SEA_PICKLE)
                .define('K', KELP)
                .unlocks("has_blank_poppet", has(ItemRegistry.poppetMap.get(BLANK).get()))
                .unlocks("entered_water", insideOf(Blocks.WATER))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ItemRegistry.poppetMap.get(WITHER_PROTECTION).get())
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
                .unlocks("has_blank_poppet", has(ItemRegistry.poppetMap.get(BLANK).get()))
                .unlocks("entered_nether", ChangeDimensionTrigger.Instance.changedDimensionTo(DimensionType.NETHER))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ItemRegistry.poppetMap.get(POTION_PROTECTION).get())
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
                .unlocks("has_blank_poppet", has(ItemRegistry.poppetMap.get(BLANK).get()))
                .unlocks("brewed_potion", BrewedPotionTrigger.Instance.brewedPotion())
                .save(consumer);

        ShapedRecipeBuilder.shaped(ItemRegistry.poppetMap.get(VOID_PROTECTION).get())
                .pattern("EOE")
                .pattern("MPM")
                .pattern("RNR")
                .define('P', ItemRegistry.poppetMap.get(BLANK).get())
                .define('N', ItemRegistry.needle.get())
                .define('R', RABBIT_HIDE)
                .define('E', ENDER_PEARL)
                .define('O', OBSIDIAN)
                .define('M', PHANTOM_MEMBRANE)
                .unlocks("has_blank_poppet", has(ItemRegistry.poppetMap.get(BLANK).get()))
                .unlocks("has_phantom_membrane", has(PHANTOM_MEMBRANE))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ItemRegistry.poppetMap.get(HUNGER_PROTECTION).get())
                .pattern("CKC")
                .pattern("CPC")
                .pattern("RNR")
                .define('P', ItemRegistry.poppetMap.get(BLANK).get())
                .define('N', ItemRegistry.needle.get())
                .define('R', RABBIT_HIDE)
                .define('C', COOKIE)
                .define('K', CAKE)
                .unlocks("has_blank_poppet", has(ItemRegistry.poppetMap.get(BLANK).get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ItemRegistry.poppetMap.get(EXPLOSION_PROTECTION).get())
                .pattern("OIO")
                .pattern("WPW")
                .pattern("RNR")
                .define('P', ItemRegistry.poppetMap.get(BLANK).get())
                .define('N', ItemRegistry.needle.get())
                .define('R', RABBIT_HIDE)
                .define('W', WATER_BUCKET)
                .define('I', IRON_BLOCK)
                .define('O', OBSIDIAN)
                .unlocks("has_blank_poppet", has(ItemRegistry.poppetMap.get(BLANK).get()))
                .unlocks("has_obsidian", has(OBSIDIAN))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ItemRegistry.poppetMap.get(PROJECTILE_PROTECTION).get())
                .pattern("THT")
                .pattern("SPS")
                .pattern("RNR")
                .define('P', ItemRegistry.poppetMap.get(BLANK).get())
                .define('N', ItemRegistry.needle.get())
                .define('R', RABBIT_HIDE)
                .define('H', Ingredient.of(HONEY_BLOCK, SLIME_BLOCK))
                .define('S', SHIELD)
                .define('T', IRON_INGOT)
                .unlocks("has_blank_poppet", has(ItemRegistry.poppetMap.get(BLANK).get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ItemRegistry.poppetMap.get(VOODOO_PROTECTION).get())
                .pattern("MEM")
                .pattern("TPT")
                .pattern("RNR")
                .define('P', ItemRegistry.poppetMap.get(BLANK).get())
                .define('N', ItemRegistry.needle.get())
                .define('R', RABBIT_HIDE)
                .define('M', PHANTOM_MEMBRANE)
                .define('E', ENDER_PEARL)
                .define('T', TNT)
                .unlocks("has_blank_poppet", has(ItemRegistry.poppetMap.get(BLANK).get()))
                .save(consumer);

        CustomRecipeBuilder.special(RecipeRegistry.bindPoppetRecipe.get())
                .save(consumer, MOD_ID + ":bind_poppet");
    }
}