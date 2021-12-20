package de.morrien.voodoo.item;

import de.morrien.voodoo.Poppet;
import de.morrien.voodoo.Voodoo;
import de.morrien.voodoo.VoodooGroup;
import de.morrien.voodoo.block.BlockRegistry;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static de.morrien.voodoo.Poppet.PoppetType.VAMPIRIC;
import static de.morrien.voodoo.Poppet.PoppetType.VOODOO;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Voodoo.MOD_ID);
    public static final RegistryObject<Item> poppetShelf = ITEMS.register("poppet_shelf", () -> new BlockItem(BlockRegistry.poppetShelf.get(), new Item.Properties().tab(VoodooGroup.INSTANCE)));
    public static final RegistryObject<Item> needle = ITEMS.register("needle", () -> new Item(new Item.Properties().tab(VoodooGroup.INSTANCE)));
    public static final RegistryObject<Item> taglockKit = ITEMS.register("taglock_kit", TaglockKitItem::new);
    public static final Map<Poppet.PoppetType, RegistryObject<PoppetItem>> poppetMap;

    static {
        poppetMap = new HashMap<>();
        for (Poppet.PoppetType poppetType : Poppet.PoppetType.values()) {
            Supplier<PoppetItem> poppetItemSupplier;
            if (poppetType == VOODOO)
                poppetItemSupplier = VoodooPoppetItem::new;
            else if (poppetType == VAMPIRIC)
                poppetItemSupplier = VampiricPoppetItem::new;
            else
                poppetItemSupplier = () -> new PoppetItem(poppetType);
            poppetMap.put(poppetType, ITEMS.register(poppetType.name().toLowerCase() + "_poppet", poppetItemSupplier));
        }
    }
}
