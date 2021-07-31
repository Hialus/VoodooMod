package de.morrien.voodoo;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import static de.morrien.voodoo.VoodooDamageSource.VoodooDamageType.FIRE;

/**
 * Created by Timor Morrien
 */
public class VoodooDamageSource extends DamageSource {
    private VoodooDamageType damageType;

    public VoodooDamageSource(VoodooDamageType damageType) {
        super("voodoo_" + damageType.toString());
        this.damageType = damageType;
    }

    @Override
    public ITextComponent getDeathMessage(LivingEntity livingEntity) {
        return new StringTextComponent(livingEntity.getName().getUnformattedComponentText() + " was killed by voodoo-magic."); // TODO: Translation
    }

    @Override
    public boolean isFireDamage() {
        return damageType == FIRE;
    }

    @Override
    public boolean isUnblockable() {
        return true;
    }

    @Override
    public boolean isDamageAbsolute() {
        return true;
    }

    @Override
    public boolean isMagicDamage() {
        return true;
    }

    @Override
    public boolean isDifficultyScaled() {
        return false;
    }

    @Override
    public boolean canHarmInCreative() {
        return false;
    }

    public enum VoodooDamageType {
        NEEDLE,
        FIRE,
        WATER
    }
}
