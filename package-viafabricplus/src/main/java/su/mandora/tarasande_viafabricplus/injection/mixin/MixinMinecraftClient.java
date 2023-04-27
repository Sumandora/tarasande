package su.mandora.tarasande_viafabricplus.injection.mixin;

import de.florianmichael.viafabricplus.settings.groups.DebugSettings;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.event.EventDispatcher;
import su.mandora.tarasande.event.impl.EventScreenInput;
import su.mandora.tarasande_viafabricplus.injection.accessor.IEventScreenInput;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {

    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;",
      ordinal = 4, shift = At.Shift.BEFORE))
    public void injectTick(CallbackInfo ci) {
        if (!DebugSettings.INSTANCE.executeInputsInSync.getValue()) return;

        EventScreenInput eventScreenInput = new EventScreenInput(false);
        ((IEventScreenInput) (Object) eventScreenInput).tarasande_setOriginal(false);
        EventDispatcher.INSTANCE.call(eventScreenInput);
    }

}
