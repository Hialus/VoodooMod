package de.morrien.voodoo.container;

import de.morrien.voodoo.Voodoo;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ContainerRegistry {
    public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, Voodoo.MOD_ID);
    public static final RegistryObject<ContainerType<PoppetShelfContainer>> poppetShelf = CONTAINERS.register("poppet_shelf", () -> IForgeContainerType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        World world = inv.player.getEntityWorld();
        return new PoppetShelfContainer(windowId, world, pos, inv, inv.player);
    }));
}
