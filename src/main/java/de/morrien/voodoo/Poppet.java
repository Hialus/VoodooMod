package de.morrien.voodoo;

import de.morrien.voodoo.VoodooConfig.Common.PoppetBase;
import de.morrien.voodoo.item.PoppetItem;
import de.morrien.voodoo.tileentity.PoppetShelfTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import org.apache.commons.lang3.text.WordUtils;

import java.util.Optional;

/**
 * Created by Timor Morrien
 */
public class Poppet {
    private final PlayerEntity player;
    private final Optional<PoppetShelfTileEntity> poppetShelf;
    private final PoppetItem item;
    private final ItemStack stack;

    public Poppet(PoppetShelfTileEntity poppetShelf, PlayerEntity player, PoppetItem item, ItemStack stack) {
        this.poppetShelf = Optional.of(poppetShelf);
        this.player = player;
        this.item = item;
        this.stack = stack;
    }

    public Poppet(PlayerEntity player, PoppetItem item, ItemStack stack) {
        this.poppetShelf = Optional.empty();
        this.player = player;
        this.item = item;
        this.stack = stack;
    }

    public PoppetItem getItem() {
        return item;
    }

    public ItemStack getStack() {
        poppetShelf.ifPresent(PoppetShelfTileEntity::inventoryTouched);
        return stack;
    }

    public Optional<PoppetShelfTileEntity> getPoppetShelf() {
        return poppetShelf;
    }

    public void use() {
        use(1);
    }

    public void use(int amount) {
        int durability = item.getPoppetType().getDurability();
        if (durability > 0) {
            stack.setDamageValue(stack.getDamageValue() + amount);
            if (stack.getMaxDamage() <= stack.getDamageValue()) {
                shrink();
            }
        } else {
            shrink();
        }
        poppetShelf.ifPresent(PoppetShelfTileEntity::inventoryTouched);
    }

    private void shrink() {
        stack.shrink(1);
        final TranslationTextComponent text = new TranslationTextComponent("text.voodoo.poppet.used_up", new TranslationTextComponent(item.getDescriptionId()));
        player.displayClientMessage(text, false);
    }

    public enum PoppetType {
        BLANK(),
        VOODOO(VoodooConfig.COMMON.voodoo.durability),
        VAMPIRIC(VoodooConfig.COMMON.vampiric),
        REFLECTOR(VoodooConfig.COMMON.reflector),
        VOODOO_PROTECTION(VoodooConfig.COMMON.voodooProtection),
        DEATH_PROTECTION(VoodooConfig.COMMON.deathProtection),
        FIRE_PROTECTION(VoodooConfig.COMMON.fireProtection),
        WATER_PROTECTION(VoodooConfig.COMMON.waterProtection),
        FALL_PROTECTION(VoodooConfig.COMMON.fallProtection),
        EXPLOSION_PROTECTION(VoodooConfig.COMMON.explosionProtection),
        PROJECTILE_PROTECTION(VoodooConfig.COMMON.projectileProtection),
        WITHER_PROTECTION(VoodooConfig.COMMON.witherProtection),
        HUNGER_PROTECTION(VoodooConfig.COMMON.hungerProtection),
        POTION_PROTECTION(VoodooConfig.COMMON.potionProtection),
        VOID_PROTECTION(VoodooConfig.COMMON.voidProtection);

        private final PoppetBase config;
        private final IntValue durability;

        PoppetType() {
            this.config = null;
            this.durability = null;
        }

        PoppetType(PoppetBase config) {
            this.config = config;
            this.durability = config.durability;
        }

        PoppetType(IntValue durability) {
            this.config = null;
            this.durability = durability;
        }

        public PoppetBase getConfig() {
            return config;
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
