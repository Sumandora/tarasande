package su.mandora.tarasande.injection.mixin.core.prediction;

import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.client.sound.TickableSoundInstance;
import su.mandora.tarasande.injection.accessor.prediction.ISoundSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundSystem.class)
public class MixinSoundSystem implements ISoundSystem {

    @Unique
    private boolean tarasande_disabled;

    @Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", at = @At("HEAD"), cancellable = true)
    public void disableSound(SoundInstance sound, CallbackInfo ci) {
        if (tarasande_disabled)
            ci.cancel();
    }

    @Inject(method = "playNextTick", at = @At("HEAD"), cancellable = true)
    public void disableSound(TickableSoundInstance sound, CallbackInfo ci) {
        if (tarasande_disabled)
            ci.cancel();
    }

    @Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;I)V", at = @At("HEAD"), cancellable = true)
    public void disableSound(SoundInstance sound, int delay, CallbackInfo ci) {
        if (tarasande_disabled)
            ci.cancel();
    }

    @Override
    public void tarasande_setDisabled(boolean b) {
        tarasande_disabled = b;
    }

    @Override
    public boolean tarasande_isDisabled() {
        return tarasande_disabled;
    }
}
