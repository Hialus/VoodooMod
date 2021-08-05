package de.morrien.voodoo;

import de.morrien.voodoo.blockentity.PoppetShelfBlockEntity;
import de.morrien.voodoo.item.PoppetItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.TranslatableComponent;
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
        final TranslatableComponent text = new TranslatableComponent("text.voodoo.poppet.used_up", new TranslatableComponent(item.getDescriptionId()));
        player.displayClientMessage(text, false);
    }

    public enum PoppetType {
        BLANK(null),
        VOODOO(VoodooConfig.COMMON.voodoo.durability),
        VAMPIRIC(VoodooConfig.COMMON.vampiric.durability),
        REFLECTOR(VoodooConfig.COMMON.reflector.durability),
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
