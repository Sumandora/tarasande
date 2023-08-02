package su.mandora.tarasande.injection.mixin.event;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.mandora.tarasande.event.EventDispatcher;
import su.mandora.tarasande.event.impl.EventIsWalkingForward;

@Mixin(Input.class)
public class MixinInput {

    @Inject(method = "hasForwardMovement", at = @At("RETURN"), cancellable = true)
    public void hookSprint(CallbackInfoReturnable<Boolean> cir) {
        if (MinecraftClient.getInstance().player != null && (Object) this != MinecraftClient.getInstance().player.input)
            return;

        EventIsWalkingForward eventIsWalkingForward = new EventIsWalkingForward(cir.getReturnValue());
        EventDispatcher.INSTANCE.call(eventIsWalkingForward);
        cir.setReturnValue(eventIsWalkingForward.getWalksForward());
    }

}
