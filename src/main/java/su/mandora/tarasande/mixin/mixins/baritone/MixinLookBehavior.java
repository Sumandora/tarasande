package su.mandora.tarasande.mixin.mixins.baritone;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.util.math.rotation.Rotation;

@Pseudo
@Mixin(targets = "baritone.behavior.LookBehavior", remap = false)
public class MixinLookBehavior {

    Rotation prevRotation;

    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(method = "onPlayerUpdate", at = @At("HEAD"), remap = false)
    public void injectPreOnPlayerUpdate(@Coerce Object event, CallbackInfo ci) {
        prevRotation = new Rotation(MinecraftClient.getInstance().player);
    }

    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(method = "onPlayerUpdate", at = @At("TAIL"), remap = false)
    public void injectPostOnPlayerUpdate(@Coerce Object event, CallbackInfo ci) {
        Rotation rotation = new Rotation(MinecraftClient.getInstance().player);
        rotation.correctSensitivity(prevRotation);
        MinecraftClient.getInstance().player.setYaw(rotation.getYaw());
        MinecraftClient.getInstance().player.setPitch(rotation.getPitch());
    }

}
