package de.morrien.voodoo.fabric.mixin;

import de.morrien.voodoo.entity.PoppetEntityHandler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {
    @Inject(method = "tick", at = @At("TAIL"))
    private void voodoo$tick(CallbackInfo ci) {
        PoppetEntityHandler.onTick((ItemEntity) (Object) this);
    }

    @Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
    private void voodoo$hurtServer(ServerLevel level, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (PoppetEntityHandler.onFire((ItemEntity) (Object) this, source))
            cir.setReturnValue(false);
    }
}
