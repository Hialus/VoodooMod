package de.morrien.voodoo.item;

import de.morrien.voodoo.Poppet;
import de.morrien.voodoo.Voodoo;
import de.morrien.voodoo.VoodooGroup;
import de.morrien.voodoo.block.BlockRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.Map;

import static de.morrien.voodoo.Poppet.PoppetType.VAMPIRIC;
import static de.morrien.voodoo.Poppet.PoppetType.VOODOO;

public class ItemRegistry {
    public static final Item needle = new Item(new FabricItemSettings().group(VoodooGroup.INSTANCE));
    public static final TaglockKitItem taglockKit = new TaglockKitItem();
    public static final BlockItem poppetShelf = new BlockItem(BlockRegistry.poppetShelf, new FabricItemSettings().group(VoodooGroup.INSTANCE));
    public static final Map<Poppet.PoppetType, PoppetItem> poppetMap = new HashMap<>();

    public static void register() {
        Registry.register(Registry.ITEM, new ResourceLocation(Voodoo.MOD_ID, "needle"), needle);
        Registry.register(Registry.ITEM, new ResourceLocation(Voodoo.MOD_ID, "taglock_kit"), taglockKit);
        Registry.register(Registry.ITEM, new ResourceLocation(Voodoo.MOD_ID, "poppet_shelf"), poppetShelf);

        for (Poppet.PoppetType poppetType : Poppet.PoppetType.values()) {
            PoppetItem poppetItem;
            if (poppetType == VOODOO)
                poppetItem = new VoodooPoppetItem();
            else if (poppetType == VAMPIRIC)
                poppetItem = new VampiricPoppetItem();
            else
                poppetItem = new PoppetItem(poppetType);
            Registry.register(Registry.ITEM, new ResourceLocation(Voodoo.MOD_ID, poppetType.name().toLowerCase() + "_poppet"), poppetItem);
            poppetMap.put(poppetType, poppetItem);
        }
    }
}
