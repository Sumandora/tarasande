package su.mandora.tarasande.mixin.mixins;

import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.TarasandeMain;
import su.mandora.tarasande.event.EventHasForwardMovement;
import su.mandora.tarasande.event.EventInput;

@Mixin(KeyboardInput.class)
public class MixinKeyboardInput extends Input {

    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/input/KeyboardInput;jumping:Z"))
    public void injectTick(boolean slowDown, float f, CallbackInfo ci) {
        EventInput eventInput = new EventInput(this.movementForward, this.movementSideways);
        TarasandeMain.Companion.get().getManagerEvent().call(eventInput);

        this.pressingForward = this.pressingBack = this.pressingLeft = this.pressingRight = false;
        this.movementForward = this.movementSideways = 0.0f;

        if (!eventInput.getCancelled()) {
            if (movementForward > 0)
                this.pressingForward = true;
            else if (movementForward < 0)
                this.pressingBack = true;

            if (movementSideways > 0)
                this.pressingLeft = true;
            else if (movementSideways < 0)
                this.pressingRight = true;

            this.movementForward = eventInput.getMovementForward();
            this.movementSideways = eventInput.getMovementSideways();
        }
    }

    @Override
    public boolean hasForwardMovement() {
        EventHasForwardMovement eventHasForwardMovement = new EventHasForwardMovement(super.hasForwardMovement());
        TarasandeMain.Companion.get().getManagerEvent().call(eventHasForwardMovement);
        return eventHasForwardMovement.getHasForwardMovement();
    }
}
