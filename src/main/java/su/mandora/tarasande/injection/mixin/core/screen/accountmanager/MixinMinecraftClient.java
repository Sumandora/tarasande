package su.mandora.tarasande.injection.mixin.core.screen.accountmanager;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.injection.accessor.IMinecraftClient;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient implements IMinecraftClient {

    @Unique
    private RunArgs tarasande_runArgs;

    @Inject(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;navigationType:Lnet/minecraft/client/gui/navigation/GuiNavigationType;", shift = At.Shift.BEFORE))
    public void captureRunArgs(RunArgs args, CallbackInfo ci) {
        tarasande_runArgs = args;
    }

    @Override
    public RunArgs tarasande_getRunArgs() {
        return tarasande_runArgs;
    }
}
