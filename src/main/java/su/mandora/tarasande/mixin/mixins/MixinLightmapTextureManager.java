package su.mandora.tarasande.mixin.mixins;

import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import su.mandora.tarasande.TarasandeMain;
import su.mandora.tarasande.event.EventGamma;

@Mixin(LightmapTextureManager.class)
public class MixinLightmapTextureManager {

    @Redirect(method = "update", at = @At(value = "FIELD", target = "Lnet/minecraft/client/option/GameOptions;gamma:D"))
    public double hookedGamma(GameOptions gameOptions) {
        EventGamma eventGamma = new EventGamma(gameOptions.gamma);
        TarasandeMain.Companion.get().getManagerEvent().call(eventGamma);
        return eventGamma.getGamma();
    }
}
