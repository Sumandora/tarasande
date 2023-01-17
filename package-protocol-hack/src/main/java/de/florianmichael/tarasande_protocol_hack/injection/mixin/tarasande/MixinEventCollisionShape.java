package de.florianmichael.tarasande_protocol_hack.injection.mixin.tarasande;

import de.florianmichael.tarasande_protocol_hack.injection.accessor.IEventCollisionShape;
import de.florianmichael.tarasande_protocol_hack.injection.accessor.IEventCollisionShape;
import net.minecraft.util.shape.VoxelShape;
import net.tarasandedevelopment.tarasande.event.EventCollisionShape;
import de.florianmichael.tarasande_protocol_hack.injection.accessor.IEventCollisionShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EventCollisionShape.class)
public class MixinEventCollisionShape implements IEventCollisionShape {

    boolean dirty = false;

    @Inject(method = "setCollisionShape", at = @At("HEAD"))
    public void trackDirtyState(VoxelShape _set___, CallbackInfo ci) {
        dirty = true;
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }
}
