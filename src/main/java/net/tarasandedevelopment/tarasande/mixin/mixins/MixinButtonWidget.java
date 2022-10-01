package net.tarasandedevelopment.tarasande.mixin.mixins;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.tarasandedevelopment.tarasande.TarasandeMain;

@Mixin(ButtonWidget.class)
public class MixinButtonWidget {

    @Inject(method = "onPress", at = @At("HEAD"), cancellable = true)
    public void injectOnPress(CallbackInfo ci) {
        if (Screen.hasControlDown()) {
            TarasandeMain.Companion.get().setDisabled(!TarasandeMain.Companion.get().getDisabled());
            ci.cancel();
        }
    }

}
