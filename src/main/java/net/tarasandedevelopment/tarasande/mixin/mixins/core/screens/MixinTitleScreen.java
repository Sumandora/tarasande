package net.tarasandedevelopment.tarasande.mixin.mixins.core.screens;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TitleScreen.class)
public class MixinTitleScreen extends Screen {
    protected MixinTitleScreen(Text title) {
        super(title);
    }

    @Redirect(method = "initWidgetsNormal", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/TitleScreen;addDrawableChild(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;"))
    public <T extends Element & Drawable & Selectable> T addClientMenuButton(TitleScreen titleScreen, T drawableElement) {
        if (drawableElement instanceof ButtonWidget buttonWidget) {
            if (buttonWidget.getMessage().getContent() instanceof TranslatableTextContent && ((TranslatableTextContent) buttonWidget.getMessage().getContent()).getKey().equals("menu.online")) {
                buttonWidget.setWidth(buttonWidget.getWidth() / 2 - 2);
                addDrawableChild(TarasandeMain.Companion.get().getManagerClientMenu().createClientMenuButton(buttonWidget.x + buttonWidget.getWidth() + 4, buttonWidget.y, buttonWidget.getWidth(), buttonWidget.getHeight(), this));
            }
        }
        return addDrawableChild(drawableElement);
    }
}
