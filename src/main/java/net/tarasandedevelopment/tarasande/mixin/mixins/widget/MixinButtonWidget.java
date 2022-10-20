package net.tarasandedevelopment.tarasande.mixin.mixins.widget;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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