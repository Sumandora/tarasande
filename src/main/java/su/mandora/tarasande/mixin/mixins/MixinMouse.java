package su.mandora.tarasande.mixin.mixins;

import net.minecraft.client.Mouse;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.TarasandeMain;
import su.mandora.tarasande.event.EventMouse;
import su.mandora.tarasande.event.EventMouseDelta;

@Mixin(Mouse.class)
public class MixinMouse {

    @Shadow private double cursorDeltaX;

    @Shadow private double cursorDeltaY;

    @Inject(method = "onMouseButton", at = @At("HEAD"), cancellable = true)
    public void injectOnMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        if(action == GLFW.GLFW_PRESS) {
            EventMouse eventMouse = new EventMouse(button);
            TarasandeMain.Companion.get().getManagerEvent().call(eventMouse);
            if(eventMouse.getCancelled())
                ci.cancel();
        }
    }

    @Inject(method = "updateMouse", at = @At("HEAD"))
    public void injectUpdateMouse(CallbackInfo ci) {
        EventMouseDelta eventMouseDelta = new EventMouseDelta(cursorDeltaX, cursorDeltaY);
        TarasandeMain.Companion.get().getManagerEvent().call(eventMouseDelta);
        cursorDeltaX = eventMouseDelta.getDeltaX();
        cursorDeltaY = eventMouseDelta.getDeltaY();
    }

}
