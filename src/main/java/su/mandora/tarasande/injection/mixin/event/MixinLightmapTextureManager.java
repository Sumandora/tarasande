package su.mandora.tarasande.injection.mixin.event;

import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import su.mandora.tarasande.event.EventDispatcher;
import su.mandora.tarasande.event.impl.EventGamma;

@Mixin(LightmapTextureManager.class)
public class MixinLightmapTextureManager {

    @ModifyArgs(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/NativeImage;setColor(III)V"))
    public void hookEventGamma(Args args) {
        EventGamma eventGamma = new EventGamma(args.get(0), args.get(1), args.get(2));
        EventDispatcher.INSTANCE.call(eventGamma);
        args.set(2, eventGamma.getColor());
    }
}
