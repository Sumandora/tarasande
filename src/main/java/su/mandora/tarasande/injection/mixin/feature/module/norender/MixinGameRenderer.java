package su.mandora.tarasande.injection.mixin.feature.module.norender;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule;
import su.mandora.tarasande.system.feature.modulesystem.impl.render.ModuleNoRender;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {

    @Inject(method = "showFloatingItem", at = @At("HEAD"), cancellable = true)
    public void noRender_showFloatingItem(ItemStack floatingItem, CallbackInfo ci) {
        if (ManagerModule.INSTANCE.get(ModuleNoRender.class).getOverlay().getTotemAnimation().should()) {
            ci.cancel();
        }
    }
}
