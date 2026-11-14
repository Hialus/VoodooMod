package de.morrien.voodoo.gametest;

import de.morrien.voodoo.blockentity.PoppetShelfBlockEntity;
import de.morrien.voodoo.item.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.TagValueInput;

import java.util.List;

import static de.morrien.voodoo.Poppet.PoppetType.FALL_PROTECTION;
import static de.morrien.voodoo.gametest.TestUtil.*;

public class PoppetShelfTests {
    private static final BlockPos SHELF_POS = new BlockPos(3, 1, 3);

    public static List<GameTestCase> tests() {
        return List.of(
                test("poppet_shelf.protects_owner", PoppetShelfTests::protectsOwner),
                test("poppet_shelf.drops_contents", PoppetShelfTests::dropsContents),
                test("poppet_shelf.saves_contents", PoppetShelfTests::savesContents)
        );
    }

    private static void protectsOwner(GameTestHelper helper) {
        final ServerPlayer player = makePlayer(helper);
        final PoppetShelfBlockEntity shelf = placeShelf(helper, player, SHELF_POS);
        final ItemStack poppet = boundPoppet(FALL_PROTECTION, player);
        shelf.setItem(0, poppet);
        player.hurtServer(helper.getLevel(), helper.getLevel().damageSources().fall(), 5);
        assertFullHealth(helper, player);
        assertPoppetUsed(helper, poppet, FALL_PROTECTION, 5);
        helper.succeed();
    }

    private static void dropsContents(GameTestHelper helper) {
        final ServerPlayer player = makePlayer(helper);
        final PoppetShelfBlockEntity shelf = placeShelf(helper, player, SHELF_POS);
        shelf.setItem(0, boundPoppet(FALL_PROTECTION, player));
        helper.destroyBlock(SHELF_POS);
        helper.assertItemEntityPresent(ItemRegistry.poppetMap.get(FALL_PROTECTION).get(), SHELF_POS, 2);
        helper.succeed();
    }

    private static void savesContents(GameTestHelper helper) {
        final ServerPlayer player = makePlayer(helper);
        final PoppetShelfBlockEntity shelf = placeShelf(helper, player, SHELF_POS);
        shelf.setItem(4, boundPoppet(FALL_PROTECTION, player));
        final CompoundTag tag = shelf.saveWithFullMetadata(helper.getLevel().registryAccess());
        final PoppetShelfBlockEntity loaded = new PoppetShelfBlockEntity(shelf.getBlockPos(), shelf.getBlockState());
        loaded.setLevel(helper.getLevel());
        loaded.loadWithComponents(TagValueInput.create(ProblemReporter.DISCARDING, helper.getLevel().registryAccess(), tag));
        loaded.setRemoved();
        helper.assertValueEqual(loaded.getOwnerUuid(), player.getUUID(), "owner UUID");
        helper.assertValueEqual(loaded.getOwnerName(), player.getName().getString(), "owner name");
        helper.assertTrue(loaded.getItem(4).is(ItemRegistry.poppetMap.get(FALL_PROTECTION).get()), "Poppet should be loaded, slot has " + loaded.getItem(4));
        assertBoundTo(helper, loaded.getItem(4), player);
        helper.succeed();
    }
}
