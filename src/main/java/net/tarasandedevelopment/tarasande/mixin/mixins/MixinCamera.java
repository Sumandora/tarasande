package net.tarasandedevelopment.tarasande.mixin.mixins;

import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.TarasandeMain;
import su.mandora.tarasande.event.EventCameraOverride;
import net.tarasandedevelopment.tarasande.mixin.accessor.ICamera;

@Mixin(Camera.class)
public abstract class MixinCamera implements ICamera {
    @Shadow
    protected abstract void setPos(Vec3d pos);

    @Shadow
    protected abstract void setRotation(float yaw, float pitch);

    @Inject(method = "update", at = @At("TAIL"))
    public void injectUpdate(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        TarasandeMain.Companion.get().getManagerEvent().call(new EventCameraOverride((Camera) (Object) this));
    }

    @Override
    public void tarasande_invokeSetPos(Vec3d pos) {
        this.setPos(pos);
    }

    @Override
    public void tarasande_invokeSetRotation(float yaw, float pitch) {
        this.setRotation(yaw, pitch);
    }
}
