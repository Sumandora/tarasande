package net.tarasandedevelopment.tarasande.mixin.mixins.screens;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
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

    @Inject(at = @At("RETURN"), method = "initWidgets()V")
    public void drawMenuButton(CallbackInfo info) {
        if (!TarasandeMain.Companion.get().getDisabled())
            addDrawableChild(TarasandeMain.Companion.get().getManagerClientMenu().createClientMenuButton(this.width / 2 - 102, this.height / 4 + 8 + 24 * 3, 204, 20, this, false));
    }

    @Override
    protected <T extends Element & Drawable & Selectable> T addDrawableChild(T drawableElement) {
        if (!TarasandeMain.Companion.get().getDisabled() && drawableElement instanceof ButtonWidget button) {
            if (button.y >= this.height / 4 - 16 + 24 * 4 - 1 && !(button.getMessage().equals(TarasandeMain.Companion.get().getManagerClientMenu().createButtonText()))) {
                button.y += 24;
            }
            button.y -= 12;
        }
        return super.addDrawableChild(drawableElement);
    }
}
