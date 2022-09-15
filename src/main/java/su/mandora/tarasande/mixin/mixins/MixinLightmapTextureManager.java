package su.mandora.tarasande.mixin.mixins;

import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.texture.NativeImage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import su.mandora.tarasande.TarasandeMain;
import su.mandora.tarasande.event.EventGamma;

@Mixin(LightmapTextureManager.class)
public class MixinLightmapTextureManager {

    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/NativeImage;setColor(III)V"))
    public void hookedSetColor(NativeImage instance, int x, int y, int color) {
        EventGamma eventGamma = new EventGamma(x, y, color);
        TarasandeMain.Companion.get().getManagerEvent().call(eventGamma);
        instance.setColor(x, y, eventGamma.getColor());
    }
}
