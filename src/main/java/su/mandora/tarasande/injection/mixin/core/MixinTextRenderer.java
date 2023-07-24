package su.mandora.tarasande.injection.mixin.core;

import net.minecraft.client.font.TextRenderer;
import org.joml.Matrix4f;
import org.joml.Vector3fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import su.mandora.tarasande.injection.accessor.ITextRenderer;

@Mixin(TextRenderer.class)
public class MixinTextRenderer implements ITextRenderer {

    @Unique
    private boolean tarasande_disableForwardShift = false;

    @Redirect(method = "drawInternal*", at = @At(value = "INVOKE", target = "Lorg/joml/Matrix4f;translate(Lorg/joml/Vector3fc;)Lorg/joml/Matrix4f;", remap = false))
    public Matrix4f disableForwardShift(Matrix4f instance, Vector3fc offset) {
        if (tarasande_disableForwardShift)
            return instance;
        return instance.translate(offset);
    }

    @Override
    public void tarasande_setDisableForwardShift(boolean enabled) {
        tarasande_disableForwardShift = enabled;
    }

    @Override
    public boolean tarasande_isDisableForwardShift() {
        return tarasande_disableForwardShift;
    }
}
