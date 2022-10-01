package de.florianmichael.tarasande.mixin.mixins;

import de.florianmichael.tarasande.event.EventChangeScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.TarasandeMain;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {

    @Shadow public abstract void setScreen(@Nullable Screen screen);

    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    public void injectSetScreen(Screen screen, CallbackInfo ci) {
        final EventChangeScreen eventChangeScreen = new EventChangeScreen(screen);
        TarasandeMain.Companion.get().getManagerEvent().call(eventChangeScreen);

        if (eventChangeScreen.getDirty()) {
            this.setScreen(eventChangeScreen.getNewScreen());
            ci.cancel();
        }
    }
}
