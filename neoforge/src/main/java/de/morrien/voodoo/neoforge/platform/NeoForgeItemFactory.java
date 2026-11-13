package de.morrien.voodoo.neoforge.platform;

import de.morrien.voodoo.Poppet;
import de.morrien.voodoo.item.PoppetItem;
import de.morrien.voodoo.item.VampiricPoppetItem;
import de.morrien.voodoo.item.VoodooPoppetItem;
import de.morrien.voodoo.neoforge.item.NeoPoppetItem;
import de.morrien.voodoo.neoforge.item.NeoVampiricPoppetItem;
import de.morrien.voodoo.neoforge.item.NeoVoodooPoppetItem;
import de.morrien.voodoo.platform.services.IItemFactory;
import net.minecraft.world.item.Item;

public class NeoForgeItemFactory implements IItemFactory {
    @Override
    public PoppetItem poppet(Poppet.PoppetType poppetType, Item.Properties properties) {
        return new NeoPoppetItem(poppetType, properties);
    }

    @Override
    public VoodooPoppetItem voodooPoppet(Item.Properties properties) {
        return new NeoVoodooPoppetItem(properties);
    }

    @Override
    public VampiricPoppetItem vampiricPoppet(Item.Properties properties) {
        return new NeoVampiricPoppetItem(properties);
    }
}
