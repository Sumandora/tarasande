package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.input;

import de.florianmichael.viaprotocolhack.util.VersionList;
import net.minecraft.client.MinecraftClient;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.event.EventScreenInput;
import net.tarasandedevelopment.tarasande.features.protocol.util.InputTracker1_12_2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;",
            ordinal = 4, shift = At.Shift.BEFORE))
    public void injectTick(CallbackInfo ci) {
        if (VersionList.isNewerTo(VersionList.R1_12_2)) return;

        while (!InputTracker1_12_2.INSTANCE.getMouse().isEmpty()) {
            InputTracker1_12_2.INSTANCE.getMouse().poll().run();
        }

        EventScreenInput eventScreenInput = new EventScreenInput(false);
        TarasandeMain.Companion.get().getManagerEvent().call(eventScreenInput);

        while (!InputTracker1_12_2.INSTANCE.getKeyboard().isEmpty()) {
            InputTracker1_12_2.INSTANCE.getKeyboard().poll().run();
        }
    }
}
