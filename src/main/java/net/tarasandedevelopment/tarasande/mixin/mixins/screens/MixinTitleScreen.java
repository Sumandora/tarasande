package net.tarasandedevelopment.tarasande.mixin.mixins.screens;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class MixinTitleScreen extends Screen {
    protected MixinTitleScreen(Text title) {
        super(title);
    }

    @Inject(method = "initWidgetsNormal", at = @At("TAIL"))
    public void injectInitWidgetsNormal(int y, int spacingY, CallbackInfo ci) {
        if (!TarasandeMain.Companion.get().getDisabled())
            addDrawableChild(TarasandeMain.Companion.get().getManagerClientMenu().createClientMenuButton(3, 3, 100, 25, this));
    }
}
