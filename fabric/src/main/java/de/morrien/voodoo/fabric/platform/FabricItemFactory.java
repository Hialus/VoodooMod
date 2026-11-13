package de.morrien.voodoo.fabric.platform;

import de.morrien.voodoo.Poppet;
import de.morrien.voodoo.item.PoppetItem;
import de.morrien.voodoo.item.VampiricPoppetItem;
import de.morrien.voodoo.item.VoodooPoppetItem;
import de.morrien.voodoo.platform.services.IItemFactory;
import net.minecraft.world.item.Item;

public class FabricItemFactory implements IItemFactory {
    @Override
    public PoppetItem poppet(Poppet.PoppetType poppetType, Item.Properties properties) {
        return new PoppetItem(poppetType, properties);
    }

    @Override
    public VoodooPoppetItem voodooPoppet(Item.Properties properties) {
        return new VoodooPoppetItem(properties);
    }

    @Override
    public VampiricPoppetItem vampiricPoppet(Item.Properties properties) {
        return new VampiricPoppetItem(properties);
    }
}
