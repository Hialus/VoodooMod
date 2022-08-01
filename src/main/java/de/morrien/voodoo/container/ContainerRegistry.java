package de.morrien.voodoo.container;

import de.morrien.voodoo.Voodoo;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ContainerRegistry {
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, Voodoo.MOD_ID);
    public static final RegistryObject<MenuType<PoppetShelfContainer>> poppetShelf = CONTAINERS.register("poppet_shelf", () -> IForgeMenuType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        Level world = inv.player.getCommandSenderWorld();
        return new PoppetShelfContainer(windowId, world, pos, inv, inv.player);
    }));
}
