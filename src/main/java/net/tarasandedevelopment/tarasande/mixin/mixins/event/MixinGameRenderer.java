package net.tarasandedevelopment.tarasande.mixin.mixins.event;

import net.minecraft.client.render.GameRenderer;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.event.EventUpdateTargetedEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {

    @Inject(method = "updateTargetedEntity", at = @At("HEAD"))
    public void hookEventUpdateTargetedEntityPre(float tickDelta, CallbackInfo ci) {
        TarasandeMain.Companion.get().getEventDispatcher().call(new EventUpdateTargetedEntity(EventUpdateTargetedEntity.State.PRE));
    }

    @Inject(method = "updateTargetedEntity", at = @At("RETURN"))
    public void hookEventUpdateTargetedEntityPost(float tickDelta, CallbackInfo ci) {
        TarasandeMain.Companion.get().getEventDispatcher().call(new EventUpdateTargetedEntity(EventUpdateTargetedEntity.State.POST));
    }
}
