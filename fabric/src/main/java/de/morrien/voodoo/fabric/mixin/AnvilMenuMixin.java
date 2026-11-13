package de.morrien.voodoo.fabric.mixin;

import de.morrien.voodoo.item.PoppetItem;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
public class AnvilMenuMixin {
    @Shadow
    @Final
    private DataSlot cost;

    @Inject(method = "createResult", at = @At("HEAD"), cancellable = true)
    private void voodoo$createResult(CallbackInfo ci) {
        final AnvilMenu menu = (AnvilMenu) (Object) this;
        final ItemStack left = menu.getSlot(0).getItem();
        if (left.getItem() instanceof PoppetItem && menu.getSlot(1).getItem().is(left.getItem())) {
            menu.getSlot(menu.getResultSlot()).set(ItemStack.EMPTY);
            this.cost.set(0);
            ci.cancel();
        }
    }
}
