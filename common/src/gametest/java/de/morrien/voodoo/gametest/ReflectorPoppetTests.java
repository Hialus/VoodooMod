package de.morrien.voodoo.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;

import java.util.List;

import static de.morrien.voodoo.Poppet.PoppetType.REFLECTOR;
import static de.morrien.voodoo.gametest.TestUtil.*;

public class ReflectorPoppetTests {
    public static List<TestFunction> tests() {
        return List.of(
                test("reflector_poppet.reflects_mob_attack", ReflectorPoppetTests::reflectsMobAttack)
        );
    }

    private static void reflectsMobAttack(GameTestHelper helper) {
        final ServerPlayer player = makePlayer(helper);
        final ItemStack poppet = boundPoppet(REFLECTOR, player);
        player.getInventory().setItem(9, poppet);
        final Zombie zombie = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, new BlockPos(3, 1, 3));
        zombie.doHurtTarget(player);
        assertFullHealth(helper, player);
        helper.assertTrue(zombie.getHealth() < zombie.getMaxHealth(), "Zombie should take the damage");
        assertPoppetUsed(helper, poppet, REFLECTOR, 1);
        helper.succeed();
    }
}
