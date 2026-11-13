package de.morrien.voodoo;

import de.morrien.voodoo.item.ItemRegistry;
import de.morrien.voodoo.platform.RegistrationProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class VoodooGroup {
    public static final RegistrationProvider<CreativeModeTab> CREATIVE_MODE_TABS = RegistrationProvider.get(Registries.CREATIVE_MODE_TAB);
    public static final Supplier<CreativeModeTab> INSTANCE = CREATIVE_MODE_TABS.register("voodoo_group", key -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
            .title(Component.translatable("itemGroup.voodoo_group"))
            .icon(() -> new ItemStack(ItemRegistry.poppetMap.get(Poppet.PoppetType.BLANK).get()))
            .displayItems((parameters, output) -> ItemRegistry.ITEMS.getEntries().forEach(item -> output.accept(item.get())))
            .build()
    );
}
