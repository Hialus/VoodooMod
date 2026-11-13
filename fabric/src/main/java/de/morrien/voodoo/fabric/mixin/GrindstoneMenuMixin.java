package de.morrien.voodoo.fabric.mixin;

import de.morrien.voodoo.item.PoppetItem;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GrindstoneMenu.class)
public class GrindstoneMenuMixin {
    @Inject(method = "mergeItems", at = @At("HEAD"), cancellable = true)
    private void voodoo$mergeItems(ItemStack first, ItemStack second, CallbackInfoReturnable<ItemStack> cir) {
        if (first.getItem() instanceof PoppetItem)
            cir.setReturnValue(ItemStack.EMPTY);
    }
}
