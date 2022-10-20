package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.input;

import de.florianmichael.viaprotocolhack.util.VersionList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.tarasandedevelopment.tarasande.protocol.util.InputTracker1_12_2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Mouse.class)
public class MixinMouse {

    @Redirect(method = { "method_29615", "method_22685", "method_22684" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;execute(Ljava/lang/Runnable;)V"))
    public void redirectSync(MinecraftClient instance, Runnable runnable) {
        if (VersionList.isOlderOrEqualTo(VersionList.R1_12_2)) {
            InputTracker1_12_2.INSTANCE.getMouse().add(runnable);
            return;
        }

        instance.execute(runnable);
    }
}
