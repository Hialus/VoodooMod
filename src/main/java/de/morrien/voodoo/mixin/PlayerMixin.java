package de.morrien.voodoo.mixin;

import de.morrien.voodoo.entity.PoppetItemEntity;
import de.morrien.voodoo.item.VoodooPoppetItem;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerMixin {

    /**
     * Mixin to drop the custom @{link PoppetItemEntity} when a player drops a @{link VoodooPoppetItem}.
     * Code was adapted from the drop method it is injected into
     */
    @Inject(method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At(value = "TAIL"), cancellable = true)
    public void dropItem(ItemStack stack, boolean throwRandomly, boolean retainOwnership, CallbackInfoReturnable<ItemEntity> cir) {
        if (stack.getItem() instanceof VoodooPoppetItem) {
            if (stack.isEmpty()) {
                cir.setReturnValue(null);
            } else {
                Player player = (Player) (Object) this;
                if (player.level.isClientSide) {
                    player.swing(InteractionHand.MAIN_HAND);
                }
                double d = player.getEyeY() - 0.30000001192092896;

                float f;
                float g;
                Vec3 deltaMovement;
                if (throwRandomly) {
                    f = player.getRandom().nextFloat() * 0.5F;
                    g = player.getRandom().nextFloat() * 6.2831855F;
                    deltaMovement = new Vec3(-Mth.sin(g) * f, 0.20000000298023224, Mth.cos(g) * f);
                } else {
                    g = Mth.sin(player.getXRot() * 0.017453292F);
                    float h = Mth.cos(player.getXRot() * 0.017453292F);
                    float i = Mth.sin(player.getYRot() * 0.017453292F);
                    float j = Mth.cos(player.getYRot() * 0.017453292F);
                    float k = player.getRandom().nextFloat() * 6.2831855F;
                    float l = 0.02F * player.getRandom().nextFloat();
                    deltaMovement = new Vec3((double) (-i * h * 0.3F) + Math.cos(k) * (double) l, -g * 0.3F + 0.1F + (player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.1F, (double) (j * h * 0.3F) + Math.sin(k) * (double) l);
                }
                PoppetItemEntity poppetItemEntity = new PoppetItemEntity(player.level, player.getX(), d, player.getZ(), deltaMovement, player.getYRot(), stack, retainOwnership ? player.getUUID() : null);
                cir.setReturnValue(poppetItemEntity);
            }
        }
    }
}