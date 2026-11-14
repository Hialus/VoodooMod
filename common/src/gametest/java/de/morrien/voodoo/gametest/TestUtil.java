package de.morrien.voodoo.gametest;

import com.mojang.authlib.GameProfile;
import de.morrien.voodoo.Poppet;
import de.morrien.voodoo.blockentity.PoppetShelfBlockEntity;
import de.morrien.voodoo.item.ItemRegistry;
import de.morrien.voodoo.util.BindingUtil;
import io.netty.channel.embedded.EmbeddedChannel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ServerboundPlayerLoadedPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class TestUtil {
    private static final List<ServerPlayer> players = new ArrayList<>();
    private static final Map<ModConfigSpec.ConfigValue<?>, Object> changedConfigValues = new HashMap<>();

    /**
     * Creates a test function {@code voodoo:<name>}. The test is defined in
     * {@code data/voodoo/test_instance/<name>.json}, which sets its timeout and environment.
     * Tests that change the config have to use the environment {@code voodoo:config}.
     */
    public static GameTestCase test(String name, Consumer<GameTestHelper> function) {
        return new GameTestCase(name, function);
    }

    public record GameTestCase(String name, Consumer<GameTestHelper> function) {
    }

    /**
     * Changes a config value without saving it, until {@link #restoreConfig()} is called after the batch.
     */
    public static <T> void setConfig(ModConfigSpec.ConfigValue<T> configValue, T value) {
        changedConfigValues.putIfAbsent(configValue, configValue.get());
        configValue.set(value);
    }

    @SuppressWarnings("unchecked")
    public static void restoreConfig() {
        changedConfigValues.forEach((configValue, value) -> ((ModConfigSpec.ConfigValue<Object>) configValue).set(value));
        changedConfigValues.clear();
    }

    public static ServerPlayer makePlayer(GameTestHelper helper) {
        return makePlayer(helper, new Vec3(1.5, 1, 1.5));
    }

    /**
     * Creates a survival player that is in the player list, so bound poppets can find it.
     * Unlike {@link GameTestHelper#makeMockServerPlayerInLevel()} it does not always count as creative.
     */
    public static ServerPlayer makePlayer(GameTestHelper helper, Vec3 pos) {
        return makePlayer(helper, pos, UUID.randomUUID());
    }

    public static ServerPlayer makePlayer(GameTestHelper helper, Vec3 pos, UUID uuid) {
        final ServerLevel level = helper.getLevel();
        final CommonListenerCookie cookie = CommonListenerCookie.createInitial(new GameProfile(uuid, "test-player-" + players.size()), false);
        final ServerPlayer player = new ServerPlayer(level.getServer(), level, cookie.gameProfile(), cookie.clientInformation());
        final Connection connection = new Connection(PacketFlow.SERVERBOUND);
        new EmbeddedChannel(connection);
        level.getServer().getPlayerList().placeNewPlayer(connection, player, cookie);
        player.setGameMode(GameType.SURVIVAL);
        player.connection.handleAcceptPlayerLoad(new ServerboundPlayerLoadedPacket());
        player.snapTo(helper.absoluteVec(pos));
        players.add(player);
        return player;
    }

    public static void removePlayers() {
        players.forEach(TestUtil::disconnect);
        players.clear();
    }

    /**
     * Disconnects a player the same way as kicking them.
     */
    public static void disconnect(ServerPlayer player) {
        if (!player.hasDisconnected())
            player.connection.disconnect(Component.literal("Test finished"));
    }

    public static ItemStack poppet(Poppet.PoppetType poppetType) {
        return new ItemStack(ItemRegistry.poppetMap.get(poppetType).get());
    }

    public static ItemStack boundPoppet(Poppet.PoppetType poppetType, Player player) {
        final ItemStack stack = poppet(poppetType);
        BindingUtil.bind(stack, player);
        return stack;
    }

    public static ItemEntity dropItem(GameTestHelper helper, ItemStack stack, Vec3 pos) {
        final Vec3 absolute = helper.absoluteVec(pos);
        final ItemEntity entity = new ItemEntity(helper.getLevel(), absolute.x, absolute.y, absolute.z, stack);
        entity.setDeltaMovement(Vec3.ZERO);
        helper.getLevel().addFreshEntity(entity);
        return entity;
    }

    public static ItemStack craft(ServerLevel level, ItemStack... stacks) {
        final CraftingInput input = CraftingInput.of(stacks.length, 1, List.of(stacks));
        return level.recipeAccess().getRecipeFor(RecipeType.CRAFTING, input, level)
                .map(recipe -> recipe.value().assemble(input))
                .orElse(ItemStack.EMPTY);
    }

    /**
     * Places a poppet shelf as a player, so it is bound to the player and can be used for protection.
     *
     * @return The placed shelf block entity
     */
    public static PoppetShelfBlockEntity placeShelf(GameTestHelper helper, ServerPlayer player, BlockPos pos) {
        helper.setBlock(pos.below(), Blocks.STONE);
        final ItemStack stack = new ItemStack(ItemRegistry.poppetShelf.get());
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        final BlockPos floor = helper.absolutePos(pos.below());
        stack.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, new BlockHitResult(Vec3.atCenterOf(floor), Direction.UP, floor, false)));
        return helper.getBlockEntity(pos, PoppetShelfBlockEntity.class);
    }

    public static void assertBoundTo(GameTestHelper helper, ItemStack stack, Player player) {
        helper.assertValueEqual(BindingUtil.getBoundUUID(stack), player.getUUID(), "bound UUID");
    }

    public static void assertPoppetUsed(GameTestHelper helper, ItemStack stack, Poppet.PoppetType poppetType, int amount) {
        if (amount >= poppetType.getDurability())
            helper.assertTrue(stack.isEmpty(), poppetType + " poppet should be used up");
        else
            helper.assertValueEqual(stack.getDamageValue(), amount, poppetType + " poppet damage");
    }

    public static void assertFullHealth(GameTestHelper helper, Player player) {
        helper.assertValueEqual(player.getHealth(), player.getMaxHealth(), "player health");
    }
}
