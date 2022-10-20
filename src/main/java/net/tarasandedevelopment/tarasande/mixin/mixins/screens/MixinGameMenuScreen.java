package net.tarasandedevelopment.tarasande.mixin.mixins.screens;

import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMenuScreen.class)
public class MixinGameMenuScreen extends Screen {

    protected MixinGameMenuScreen(Text title) {
        super(title);
    }

    @Inject(method = "initWidgets()V", at = @At("RETURN"))
    public void drawMenuButton(CallbackInfo info) {
        if (!TarasandeMain.Companion.get().getDisabled())
            addDrawableChild(TarasandeMain.Companion.get().getManagerClientMenu().createClientMenuButton(3, 3, 100, 25, this));
    }
}
