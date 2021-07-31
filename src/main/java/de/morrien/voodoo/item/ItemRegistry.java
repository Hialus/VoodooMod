package de.morrien.voodoo.item;

import de.morrien.voodoo.Poppet;
import de.morrien.voodoo.Voodoo;
import de.morrien.voodoo.VoodooGroup;
import de.morrien.voodoo.block.BlockRegistry;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Voodoo.MOD_ID);
    public static final RegistryObject<Item> poppetShelf = ITEMS.register("poppet_shelf", () -> new BlockItem(BlockRegistry.poppetShelf.get(), new Item.Properties().tab(VoodooGroup.INSTANCE)));
    public static final RegistryObject<Item> needle = ITEMS.register("needle", () -> new Item(new Item.Properties().tab(VoodooGroup.INSTANCE)));
    public static final RegistryObject<Item> taglockKit = ITEMS.register("taglock_kit", TaglockKitItem::new);
    public static final Map<Poppet.PoppetType, RegistryObject<Item>> poppetMap;

    static {
        poppetMap = new HashMap<>();
        for (Poppet.PoppetType poppetType : Poppet.PoppetType.values()) {
            poppetMap.put(poppetType, ITEMS.register(poppetType.name().toLowerCase() + "_poppet", () -> new PoppetItem(poppetType)));
        }
    }
}
