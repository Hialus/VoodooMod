package de.morrien.voodoo;

import de.morrien.voodoo.VoodooConfig.Common.PoppetBase;
import de.morrien.voodoo.blockentity.PoppetShelfBlockEntity;
import de.morrien.voodoo.item.PoppetItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import org.apache.commons.lang3.text.WordUtils;

import java.util.Optional;

/**
 * Created by Timor Morrien
 */
public class Poppet {
    private final Player player;
    private final Optional<PoppetShelfBlockEntity> poppetShelf;
    private final PoppetItem item;
    private final ItemStack stack;

    public Poppet(PoppetShelfBlockEntity poppetShelf, Player player, PoppetItem item, ItemStack stack) {
        this.poppetShelf = Optional.of(poppetShelf);
        this.player = player;
        this.item = item;
        this.stack = stack;
    }

    public Poppet(Player player, PoppetItem item, ItemStack stack) {
        this.poppetShelf = Optional.empty();
        this.player = player;
        this.item = item;
        this.stack = stack;
    }

    public PoppetItem getItem() {
        return item;
    }

    public ItemStack getStack() {
        poppetShelf.ifPresent(PoppetShelfBlockEntity::inventoryTouched);
        return stack;
    }

    public Optional<PoppetShelfBlockEntity> getPoppetShelf() {
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
        poppetShelf.ifPresent(PoppetShelfBlockEntity::inventoryTouched);
    }

    private void shrink() {
        stack.shrink(1);
        var text = Component.translatable("text.voodoo.poppet.used_up", Component.translatable(item.getDescriptionId()));
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
