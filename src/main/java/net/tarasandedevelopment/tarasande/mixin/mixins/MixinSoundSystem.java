package net.tarasandedevelopment.tarasande.mixin.mixins;

import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.client.sound.TickableSoundInstance;
import net.tarasandedevelopment.tarasande.mixin.accessor.ISoundSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundSystem.class)
public class MixinSoundSystem implements ISoundSystem {

    @Unique
    public boolean disabled;

    @Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", at = @At("HEAD"), cancellable = true)
    public void injectPlay(SoundInstance sound, CallbackInfo ci) {
        if (disabled)
            ci.cancel();
    }

    @Inject(method = "playNextTick", at = @At("HEAD"), cancellable = true)
    public void injectPlayNextTick(TickableSoundInstance sound, CallbackInfo ci) {
        if (disabled)
            ci.cancel();
    }

    @Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;I)V", at = @At("HEAD"), cancellable = true)
    public void injectPlayNextTick(SoundInstance sound, int delay, CallbackInfo ci) {
        if (disabled)
            ci.cancel();
    }

    @Override
    public void tarasande_setDisabled(boolean b) {
        disabled = b;
    }

    @Override
    public boolean tarasande_isDisabled() {
        return disabled;
    }
}
