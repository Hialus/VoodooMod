package de.morrien.voodoo;

import de.morrien.voodoo.blockentity.PoppetShelfBlockEntity;
import de.morrien.voodoo.VoodooConfig.Common.PoppetBase;
import de.morrien.voodoo.item.PoppetItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;
import org.apache.commons.lang3.text.WordUtils;

import java.util.Optional;

/**
 * Created by Phoebe Morrien
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
        poppetShelf.ifPresent(PoppetShelfBlockEntity::setChanged);
    }

    private void shrink() {
        stack.shrink(1);
        var text = Component.translatable("text.voodoo.poppet.used_up", Component.translatable(item.getDescriptionId()));
        player.sendSystemMessage(text);
    }

    public enum PoppetType {
        BLANK(),
        VOODOO(VoodooConfig.DURABILITY.voodoo),
        VAMPIRIC(VoodooConfig.COMMON.vampiric, VoodooConfig.DURABILITY.vampiric),
        REFLECTOR(VoodooConfig.COMMON.reflector, VoodooConfig.DURABILITY.reflector),
        VOODOO_PROTECTION(VoodooConfig.COMMON.voodooProtection, VoodooConfig.DURABILITY.voodooProtection),
        DEATH_PROTECTION(VoodooConfig.COMMON.deathProtection, VoodooConfig.DURABILITY.deathProtection),
        FIRE_PROTECTION(VoodooConfig.COMMON.fireProtection, VoodooConfig.DURABILITY.fireProtection),
        WATER_PROTECTION(VoodooConfig.COMMON.waterProtection, VoodooConfig.DURABILITY.waterProtection),
        FALL_PROTECTION(VoodooConfig.COMMON.fallProtection, VoodooConfig.DURABILITY.fallProtection),
        EXPLOSION_PROTECTION(VoodooConfig.COMMON.explosionProtection, VoodooConfig.DURABILITY.explosionProtection),
        PROJECTILE_PROTECTION(VoodooConfig.COMMON.projectileProtection, VoodooConfig.DURABILITY.projectileProtection),
        WITHER_PROTECTION(VoodooConfig.COMMON.witherProtection, VoodooConfig.DURABILITY.witherProtection),
        HUNGER_PROTECTION(VoodooConfig.COMMON.hungerProtection, VoodooConfig.DURABILITY.hungerProtection),
        POTION_PROTECTION(VoodooConfig.COMMON.potionProtection, VoodooConfig.DURABILITY.potionProtection),
        VOID_PROTECTION(VoodooConfig.COMMON.voidProtection, VoodooConfig.DURABILITY.voidProtection);

        private final PoppetBase config;
        private final IntValue durability;

        PoppetType() {
            this.config = null;
            this.durability = null;
        }

        PoppetType(PoppetBase config, IntValue durability) {
            this.config = config;
            this.durability = durability;
        }

        PoppetType(IntValue durability) {
            this.config = null;
            this.durability = durability;
        }

        public PoppetBase getConfig() {
            return config;
        }

        public boolean hasDurability() {
            return getDurability() > 0;
        }

        public int getDurability() {
            if (durability == null) return 0;
            return VoodooConfig.durabilitySpec.isLoaded() ? durability.get() : durability.getDefault();
        }

        @Override
        public String toString() {
            return WordUtils.capitalize(super.toString().replaceAll("_", " ").toLowerCase());
        }
    }
}
