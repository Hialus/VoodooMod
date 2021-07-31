package de.morrien.voodoo;

import de.morrien.voodoo.item.PoppetItem;
import de.morrien.voodoo.tileentity.PoppetShelfTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Timor Morrien
 */
public class Poppet {
    private PlayerEntity player;
    private PoppetShelfTileEntity poppetShelf;
    private PoppetItem item;
    private ItemStack stack;

    public Poppet(PoppetShelfTileEntity poppetShelf, PoppetItem item, ItemStack stack) {
        this.poppetShelf = poppetShelf;
        this.item = item;
        this.stack = stack;
    }

    public Poppet(PlayerEntity player, PoppetItem item, ItemStack stack) {
        this.player = player;
        this.item = item;
        this.stack = stack;
    }

    public static Poppet getPlayerPoppet(PlayerEntity player, PoppetType poppetType) {
        List<ItemStack> playerItems = new ArrayList<>();
        playerItems.addAll(player.inventory.offHandInventory);
        playerItems.addAll(player.inventory.mainInventory);
        for (ItemStack itemStack : playerItems) {
            Item item = itemStack.getItem();
            if (item instanceof PoppetItem && player.equals(PoppetItem.getBoundPlayer(itemStack, player.world))) {
                PoppetItem poppetItem = (PoppetItem) item;
                if (poppetItem.getPoppetType() == poppetType) {
                    return new Poppet(player, poppetItem, itemStack);
                }
            }
        }
        World world = player.world;
        List<TileEntity> tileEntities = world.loadedTileEntityList;
        for (TileEntity tileEntity : tileEntities) {
            if (tileEntity instanceof PoppetShelfTileEntity) {
                Poppet poppet = ((PoppetShelfTileEntity) tileEntity).getPoppet(poppetType);
                if (poppet != null && player.equals(PoppetItem.getBoundPlayer(poppet.stack, player.world))) {
                    poppet.player = player;
                    return poppet;
                }
            }
        }
        return null;
    }

    public PlayerEntity getPlayer() {
        return player;
    }

    public PoppetItem getItem() {
        return item;
    }

    public ItemStack getStack() {
        return stack;
    }

    public void use() {
        use(1);
    }

    public void use(int amount) {
        int durability = item.getPoppetType().getDurability();
        if (durability > 0) {
            stack.setDamage(stack.getDamage() + amount);
            if (stack.getMaxDamage() <= stack.getDamage()) {
                shrink();
            }
        } else {
            shrink();
        }
    }

    private void shrink() {
        if (poppetShelf != null) {
            stack.shrink(1);
            //poppetShelf.shrink(stack);
        } else {
            stack.shrink(1);
        }
        final TranslationTextComponent text = new TranslationTextComponent("text.voodoo.poppet.used_up", new TranslationTextComponent(item.getTranslationKey()));
        player.sendStatusMessage(text, false);
    }

    public enum PoppetType {
        BLANK(null),
        VOODOO(VoodooConfig.COMMON.voodoo.durability),
        VOODOO_PROTECTION(VoodooConfig.COMMON.voodooProtection.durability),
        DEATH_PROTECTION(VoodooConfig.COMMON.deathProtection.durability),
        FIRE_PROTECTION(VoodooConfig.COMMON.fireProtection.durability),
        WATER_PROTECTION(VoodooConfig.COMMON.waterProtection.durability),
        FALL_PROTECTION(VoodooConfig.COMMON.fallProtection.durability),
        EXPLOSION_PROTECTION(VoodooConfig.COMMON.explosionProtection.durability),
        PROJECTILE_PROTECTION(VoodooConfig.COMMON.projectileProtection.durability),
        WITHER_PROTECTION(VoodooConfig.COMMON.witherProtection.durability),
        HUNGER_PROTECTION(VoodooConfig.COMMON.hungerProtection.durability),
        POTION_PROTECTION(VoodooConfig.COMMON.potionProtection.durability),
        VOID_PROTECTION(VoodooConfig.COMMON.voidProtection.durability);

        private final IntValue durability;

        PoppetType(IntValue durability) {
            this.durability = durability;
        }

        public boolean hasDurability() {
            return durability != null && durability.get() > 0;
        }

        public int getDurability() {
            return durability == null ? 0 : durability.get();
        }

        @Override
        public String toString() {
            return WordUtils.capitalize(super.toString().replaceAll("_", " ").toLowerCase());
        }
    }
}
