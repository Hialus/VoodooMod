package de.morrien.voodoo.fabric.mixin;

import de.morrien.voodoo.item.PoppetItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RepairItemRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RepairItemRecipe.class)
public class RepairItemRecipeMixin {
    @Inject(method = "canCombine", at = @At("HEAD"), cancellable = true)
    private static void voodoo$canCombine(ItemStack first, ItemStack second, CallbackInfoReturnable<Boolean> cir) {
        if (first.getItem() instanceof PoppetItem)
            cir.setReturnValue(false);
    }
}
