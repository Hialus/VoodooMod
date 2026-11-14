package de.morrien.voodoo.fabric.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import de.morrien.voodoo.event.VoodooEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isSleeping()Z"), cancellable = true)
    private void voodoo$hurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir, @Local(argsOnly = true) LocalFloatRef amountRef) {
        final float remaining = VoodooEvents.onLivingAttack((LivingEntity) (Object) this, source, amount);
        if (remaining == amount) return;
        if (remaining <= 0)
            cir.setReturnValue(false);
        else
            amountRef.set(remaining);
    }
}
