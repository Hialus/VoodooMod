package de.morrien.voodoo;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

import static de.morrien.voodoo.VoodooDamageSource.VoodooDamageType.FIRE;

/**
 * Created by Timor Morrien
 */
public class VoodooDamageSource extends DamageSource {
    private VoodooDamageType damageType;
    private ItemStack voodooPoppet;
    private Entity fromEntity;

    public VoodooDamageSource(VoodooDamageType damageType, ItemStack voodooPoppet, Entity fromEntity) {
        super("voodoo_" + damageType.toString());
        this.damageType = damageType;
        this.voodooPoppet = voodooPoppet;
        this.fromEntity = fromEntity;
    }

    @Override
    public Component getLocalizedDeathMessage(LivingEntity livingEntity) {
        return new TextComponent(livingEntity.getName().getContents() + " was killed by voodoo-magic."); // TODO: Translation
    }

    @Override
    public boolean isFire() {
        return damageType == FIRE;
    }

    @Override
    public boolean isBypassArmor() {
        return true;
    }

    @Override
    public boolean isBypassMagic() {
        return true;
    }

    @Override
    public boolean isMagic() {
        return true;
    }

    @Override
    public boolean scalesWithDifficulty() {
        return false;
    }

    @Override
    public boolean isBypassInvul() {
        return false;
    }

    public ItemStack getVoodooPoppet() {
        return voodooPoppet;
    }

    public Entity getFromEntity() {
        return fromEntity;
    }

    public enum VoodooDamageType {
        NEEDLE,
        FIRE,
        WATER,
        VAMPIRIC
    }
}
