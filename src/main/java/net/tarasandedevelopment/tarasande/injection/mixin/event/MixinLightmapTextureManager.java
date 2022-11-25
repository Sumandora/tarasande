package net.tarasandedevelopment.tarasande.injection.mixin.event;

import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.texture.NativeImage;
import net.tarasandedevelopment.tarasande.event.EventGamma;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import su.mandora.event.EventDispatcher;

@Mixin(LightmapTextureManager.class)
public class MixinLightmapTextureManager {

    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/NativeImage;setColor(III)V"))
    public void hookEventGamma(NativeImage instance, int x, int y, int color) {
        EventGamma eventGamma = new EventGamma(x, y, color);
        EventDispatcher.INSTANCE.call(eventGamma);
        instance.setColor(x, y, eventGamma.getColor());
    }
}
