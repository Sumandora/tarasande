package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.input;

import de.florianmichael.viaprotocolhack.util.VersionList;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.tarasandedevelopment.tarasande.protocol.util.InputTracker1_12_2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Keyboard.class)
public class MixinKeyboard {

    @Redirect(method = "*", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;execute(Ljava/lang/Runnable;)V"))
    public void redirectSync(MinecraftClient instance, Runnable runnable) {
        if (VersionList.isOlderOrEqualTo(VersionList.R1_12_2)) {
            InputTracker1_12_2.INSTANCE.getKeyboard().add(runnable);
            return;
        }

        instance.execute(runnable);
    }
}
