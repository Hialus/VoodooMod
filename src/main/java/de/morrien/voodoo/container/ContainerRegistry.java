package de.morrien.voodoo.container;

import de.morrien.voodoo.Voodoo;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ContainerRegistry {
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, Voodoo.MOD_ID);
    public static final RegistryObject<MenuType<PoppetShelfContainer>> poppetShelf = CONTAINERS.register("poppet_shelf", () -> IForgeContainerType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        Level world = inv.player.getCommandSenderWorld();
        return new PoppetShelfContainer(windowId, world, pos, inv, inv.player);
    }));
}
