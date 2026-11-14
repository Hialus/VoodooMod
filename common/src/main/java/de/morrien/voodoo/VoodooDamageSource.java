package de.morrien.voodoo;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Created by Phoebe Morrien
 */
public class VoodooDamageSource extends DamageSource {
    public static final TagKey<DamageType> VOODOO = TagKey.create(Registries.DAMAGE_TYPE, Voodoo.id("voodoo"));
    private ItemStack voodooPoppet;
    private Entity fromEntity;

    public VoodooDamageSource(Level level, VoodooDamageType damageType, ItemStack voodooPoppet, Entity fromEntity) {
        super(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(damageType.key));
        this.voodooPoppet = voodooPoppet;
        this.fromEntity = fromEntity;
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
        VAMPIRIC;

        private final ResourceKey<DamageType> key = ResourceKey.create(Registries.DAMAGE_TYPE, Voodoo.id(name().toLowerCase()));
    }
}
