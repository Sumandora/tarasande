package su.mandora.tarasande.injection.mixin.event;

import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.VertexConsumer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import su.mandora.tarasande.event.EventDispatcher;
import su.mandora.tarasande.event.impl.EventColorCorrection;

@Mixin(Framebuffer.class)
public class MixinFramebuffer {

    @Redirect(method = "drawInternal", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumer;color(IIII)Lnet/minecraft/client/render/VertexConsumer;"))
    public VertexConsumer hookEventColorCorrection(VertexConsumer instance, int r, int g, int b, int a) {
        EventColorCorrection eventColorCorrection = new EventColorCorrection(r, g, b);
        EventDispatcher.INSTANCE.call(eventColorCorrection);
        return instance.color(eventColorCorrection.getRed(), eventColorCorrection.getGreen(), eventColorCorrection.getBlue(), a);
    }

}
