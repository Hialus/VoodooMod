package de.morrien.voodoo.mixin;

import de.morrien.voodoo.event.VoodooEvents;
import de.morrien.voodoo.event.wrapper.LivingAttackEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {

    /**
     * Mixin catching when a player was hurt and checking for appropriate poppets
     */
    @Inject(method = "hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", at = @At("HEAD"), cancellable = true)
    public void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        ServerPlayer player = (ServerPlayer) ((Object) this);
        var event = new LivingAttackEvent(player, source, amount);
        VoodooEvents.onLivingAttack(event);
        if (event.isCanceled()) {
            cir.setReturnValue(false);
        }
    }
}