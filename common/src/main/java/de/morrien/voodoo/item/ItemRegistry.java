package de.morrien.voodoo.item;

import de.morrien.voodoo.Poppet;
import de.morrien.voodoo.block.BlockRegistry;
import de.morrien.voodoo.platform.RegistrationProvider;
import de.morrien.voodoo.platform.Services;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static de.morrien.voodoo.Poppet.PoppetType.VAMPIRIC;
import static de.morrien.voodoo.Poppet.PoppetType.VOODOO;

public class ItemRegistry {
    public static final RegistrationProvider<Item> ITEMS = RegistrationProvider.get(Registries.ITEM);
    public static final Supplier<Item> poppetShelf = ITEMS.register("poppet_shelf", key -> new BlockItem(BlockRegistry.poppetShelf.get(), new Item.Properties()));
    public static final Supplier<Item> needle = ITEMS.register("needle", key -> new Item(new Item.Properties()));
    public static final Supplier<Item> taglockKit = ITEMS.register("taglock_kit", key -> new TaglockKitItem(new Item.Properties()));
    public static final Map<Poppet.PoppetType, Supplier<PoppetItem>> poppetMap;

    static {
        poppetMap = new HashMap<>();
        for (Poppet.PoppetType poppetType : Poppet.PoppetType.values()) {
            Function<Item.Properties, PoppetItem> poppetItemFactory;
            if (poppetType == VOODOO)
                poppetItemFactory = Services.ITEMS::voodooPoppet;
            else if (poppetType == VAMPIRIC)
                poppetItemFactory = Services.ITEMS::vampiricPoppet;
            else
                poppetItemFactory = properties -> Services.ITEMS.poppet(poppetType, properties);
            poppetMap.put(poppetType, ITEMS.register(poppetType.name().toLowerCase() + "_poppet", key -> poppetItemFactory.apply(new Item.Properties())));
        }
    }
}
