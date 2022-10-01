package su.mandora.tarasande.mixin.mixins;

import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.mandora.tarasande.TarasandeMain;
import su.mandora.tarasande.event.EventDebugHud;

import java.util.List;

@Mixin(DebugHud.class)
public class MixinDebugHud {

    @Redirect(method = "getLeftText", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;wrapDegrees(F)F"))
    public float hookedWrapDegrees(float degrees) {
        return degrees;
    }

    @Inject(method = "getLeftText", at = @At(value = "RETURN"))
    public void hookedAdd(CallbackInfoReturnable<List<String>> cir) {
        EventDebugHud eventDebugHud = new EventDebugHud(cir.getReturnValue());
        TarasandeMain.Companion.get().getManagerEvent().call(eventDebugHud);
    }

}
