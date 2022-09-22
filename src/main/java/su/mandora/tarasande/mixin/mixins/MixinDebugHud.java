package su.mandora.tarasande.mixin.mixins;

import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import su.mandora.tarasande.util.math.rotation.RotationUtil;

import java.util.List;

@Mixin(DebugHud.class)
public class MixinDebugHud {

    @Redirect(method = "getLeftText", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;wrapDegrees(F)F"))
    public float hookedWrapDegrees(float degrees) {
        return degrees;
    }

    @Redirect(method = "getLeftText", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
    public boolean hookedAdd(List<Object> instance, Object e) {
        instance.add(e);
        if (e instanceof String) {
            if (((String) e).startsWith("Facing"))
                if (RotationUtil.INSTANCE.getFakeRotation() != null)
                    instance.add("[Tarsande Fake rotation] " + RotationUtil.INSTANCE.getFakeRotation());
        }
        return true;
    }

}
