package net.tarasandedevelopment.tarasande.mixin.mixins.event;

import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.event.EventRender2D;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class MixinInGameHud {

    @Inject(method = "render", at = @At("TAIL"))
    public void hookEventRender2D(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        TarasandeMain.Companion.get().getEventDispatcher().call(new EventRender2D(matrices));
    }
}
