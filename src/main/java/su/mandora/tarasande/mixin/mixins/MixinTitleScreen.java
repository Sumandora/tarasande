package su.mandora.tarasande.mixin.mixins;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.TarasandeMain;

import java.util.concurrent.ThreadLocalRandom;

@Mixin(TitleScreen.class)
public class MixinTitleScreen extends Screen {

    @Shadow
    @Nullable
    private String splashText;

    protected MixinTitleScreen(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("HEAD"))
    public void injectInit(CallbackInfo ci) {
        if (ThreadLocalRandom.current().nextInt(10) == 0)
            splashText = TarasandeMain.Companion.get().getName() + " is powered by Cocklin";
        else
            splashText = null;
    }

    @Redirect(method = "initWidgetsNormal", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/TitleScreen;addDrawableChild(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;"))
    public <T extends Element & Drawable & Selectable> T hookedAddDrawableChild(TitleScreen titleScreen, T drawableElement) {
        if (drawableElement instanceof ButtonWidget buttonWidget) {
            if ((buttonWidget.getMessage().getContent() instanceof TranslatableTextContent && ((TranslatableTextContent) buttonWidget.getMessage().getContent()).getKey().equals("menu.online")) || buttonWidget.getMessage().getString().contains("Realms")) {
                buttonWidget.setWidth(buttonWidget.getWidth() / 2 - 2);
                addDrawableChild(new ButtonWidget(buttonWidget.x + buttonWidget.getWidth() + 4, buttonWidget.y, buttonWidget.getWidth(), buttonWidget.getHeight(), Text.of("Account Manager"), button -> client.setScreen(TarasandeMain.Companion.get().getScreens().getBetterScreenAccountManager())));

            }
        }
        return addDrawableChild(drawableElement);
    }
}
