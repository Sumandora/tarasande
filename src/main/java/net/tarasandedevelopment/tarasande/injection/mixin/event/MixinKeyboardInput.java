package net.tarasandedevelopment.tarasande.injection.mixin.event;

import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.tarasandedevelopment.tarasande.event.EventHasForwardMovement;
import net.tarasandedevelopment.tarasande.event.EventInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.event.EventDispatcher;

@Mixin(KeyboardInput.class)
public class MixinKeyboardInput extends Input {

    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/input/KeyboardInput;sneaking:Z", shift = At.Shift.AFTER), cancellable = true)
    public void hookEventInput(boolean slowDown, float f, CallbackInfo ci) {
        EventInput eventInput = new EventInput(this, this.movementForward, this.movementSideways, slowDown, f);
        EventDispatcher.INSTANCE.call(eventInput);

        this.pressingForward = this.pressingBack = this.pressingLeft = this.pressingRight = false;
        this.movementForward = this.movementSideways = 0.0F;

        if (!eventInput.getCancelled()) {
            this.movementForward = eventInput.getMovementForward();
            this.movementSideways = eventInput.getMovementSideways();

            if (movementForward > 0)
                this.pressingForward = true;
            else if (movementForward < 0)
                this.pressingBack = true;

            if (movementSideways > 0)
                this.pressingLeft = true;
            else if (movementSideways < 0)
                this.pressingRight = true;

            if (slowDown && !eventInput.getSlowDown())
                ci.cancel(); // This is awful, but I can't change the parameter, because Java doesn't support references
        }
    }

    @Override
    public boolean hasForwardMovement() {
        EventHasForwardMovement eventHasForwardMovement = new EventHasForwardMovement(super.hasForwardMovement());
        EventDispatcher.INSTANCE.call(eventHasForwardMovement);
        return eventHasForwardMovement.getHasForwardMovement();
    }
}
