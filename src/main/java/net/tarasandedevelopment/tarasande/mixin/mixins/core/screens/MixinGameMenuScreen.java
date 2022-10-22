package net.tarasandedevelopment.tarasande.mixin.mixins.core.screens;

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

    @Inject(method = "initWidgets()V", at = @At("RETURN"))
    public void addClientMenuButton(CallbackInfo info) {
        for (Element child : this.children()) {
            if (child instanceof ButtonWidget button) {
                if (button.getMessage().contains(Text.translatable("menu.options"))) {
                    addDrawableChild(TarasandeMain.Companion.get().getManagerClientMenu().createClientMenuButton(this.width / 2 - 102, button.y - 24, 204, 20, this));
                    break;
                }
            }
        }
    }

    @Override
    protected <T extends Element & Drawable & Selectable> T addDrawableChild(T drawableElement) {
        if (!TarasandeMain.Companion.get().getDisabled() && drawableElement instanceof ButtonWidget button) {
            if (button.y >= this.height / 4 - 16 + 24 * 4 - 1) {
                button.y += 24;
            }
            button.y -= 12;
        }
        return super.addDrawableChild(drawableElement);
    }
}
