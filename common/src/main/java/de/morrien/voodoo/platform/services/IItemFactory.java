package de.morrien.voodoo.platform.services;

import de.morrien.voodoo.Poppet;
import de.morrien.voodoo.item.PoppetItem;
import de.morrien.voodoo.item.VampiricPoppetItem;
import de.morrien.voodoo.item.VoodooPoppetItem;
import net.minecraft.world.item.Item;

public interface IItemFactory {
    PoppetItem poppet(Poppet.PoppetType poppetType, Item.Properties properties);

    VoodooPoppetItem voodooPoppet(Item.Properties properties);

    VampiricPoppetItem vampiricPoppet(Item.Properties properties);
}
