package net.tarasandedevelopment.tarasande.injection.mixin.event;

import net.minecraft.client.Mouse;
import su.mandora.event.EventDispatcher;
import net.tarasandedevelopment.tarasande.event.EventMouse;
import net.tarasandedevelopment.tarasande.event.EventMouseDelta;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MixinMouse {

    @Shadow
    private double cursorDeltaX;

    @Shadow
    private double cursorDeltaY;

    @Inject(method = "onMouseButton", at = @At("HEAD"), cancellable = true)
    public void hookEventMouse(long window, int button, int action, int mods, CallbackInfo ci) {
        EventMouse eventMouse = new EventMouse(button, action);
        EventDispatcher.INSTANCE.call(eventMouse);
        if (eventMouse.getCancelled())
            ci.cancel();
    }

    @Inject(method = "updateMouse", at = @At("HEAD"))
    public void hookEventMouseDelta(CallbackInfo ci) {
        EventMouseDelta eventMouseDelta = new EventMouseDelta(cursorDeltaX, cursorDeltaY);
        EventDispatcher.INSTANCE.call(eventMouseDelta);
        cursorDeltaX = eventMouseDelta.getDeltaX();
        cursorDeltaY = eventMouseDelta.getDeltaY();
    }

}
