package net.tarasandedevelopment.tarasande_protocol_hack.injection.mixin;

import net.minecraft.client.main.Main;
import net.tarasandedevelopment.tarasande_protocol_hack.TarasandeProtocolHack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Main.class)
public class MixinMain {

    @Inject(method = "main([Ljava/lang/String;Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/crash/CrashReport;initCrashReport()V"))
    private static void loadViaLoadingBase(CallbackInfo ci) {
        new TarasandeProtocolHack().initialize();
    }
}
