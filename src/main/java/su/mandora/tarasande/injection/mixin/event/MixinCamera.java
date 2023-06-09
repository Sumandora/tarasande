package su.mandora.tarasande.injection.mixin.event;

import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.event.EventDispatcher;
import su.mandora.tarasande.event.impl.EventCameraOverride;

@Mixin(Camera.class)
public class MixinCamera {

    @Inject(method = "update", at = @At("TAIL"))
    public void hookEventCameraOverride(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        EventDispatcher.INSTANCE.call(new EventCameraOverride((Camera) (Object) this));
    }
}
