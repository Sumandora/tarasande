package net.tarasandedevelopment.tarasande.mixin.mixins;

import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.SoundSystem;
import net.tarasandedevelopment.tarasande.mixin.accessor.ISoundManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SoundManager.class)
public class MixinSoundManager implements ISoundManager {
    @Shadow
    @Final
    private SoundSystem soundSystem;

    @Override
    public SoundSystem tarasande_getSoundSystem() {
        return soundSystem;
    }
}
