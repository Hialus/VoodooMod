package de.morrien.voodoo.event.wrapper;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public class LivingDeathEvent {
    private LivingEntity entity;
    private DamageSource source;
    private boolean canceled;

    public LivingDeathEvent(LivingEntity entity, DamageSource source) {
        this.entity = entity;
        this.source = source;
        this.canceled = false;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public void setEntity(LivingEntity entity) {
        this.entity = entity;
    }

    public DamageSource getSource() {
        return source;
    }

    public void setSource(DamageSource source) {
        this.source = source;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public boolean isCanceled() {
        return canceled;
    }
}
