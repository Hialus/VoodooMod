package de.morrien.voodoo.event.wrapper;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public class LivingAttackEvent {
    private LivingEntity entity;
    private DamageSource source;
    private float amount;
    private boolean canceled;

    public LivingAttackEvent(LivingEntity entity, DamageSource source, float amount) {
        this.entity = entity;
        this.amount = amount;
        this.source = source;
        this.canceled = false;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public void setEntity(LivingEntity entity) {
        this.entity = entity;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public DamageSource getSource() {
        return source;
    }

    public void setSource(DamageSource source) {
        this.source = source;
    }
}
