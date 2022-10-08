package net.tarasandedevelopment.tarasande.mixin.mixins;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMenuScreen.class)
public class MixinGameMenuScreen extends Screen {

    @Unique
    boolean addedClientMenuButton = false;

    protected MixinGameMenuScreen(Text title) {
        super(title);
    }

    @Inject(method = "initWidgets", at = @At("HEAD"))
    public void injectPreInitWidgets(CallbackInfo ci) {
        addedClientMenuButton = false;
    }

    @Redirect(method = "initWidgets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/GameMenuScreen;addDrawableChild(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;"))
    public <T extends Element & Drawable & Selectable> T hookedAddDrawableChild(GameMenuScreen instance, T drawableElement) {
        if (drawableElement instanceof ButtonWidget buttonWidget) {
            for (String key : new String[]{"menu.sendFeedback", "menu.reportBugs"}) {
                if (buttonWidget.getMessage().getContent() instanceof TranslatableTextContent && ((TranslatableTextContent) buttonWidget.getMessage().getContent()).getKey().equals(key)) {
                    if (!addedClientMenuButton) {
                        addedClientMenuButton = true;
                        //noinspection unchecked
                        drawableElement = (T) TarasandeMain.Companion.get().getManagerClientMenu().createButton(buttonWidget.x, buttonWidget.y, buttonWidget.getWidth(), buttonWidget.getHeight(), this);
                    }
                }
            }
        }
        return addDrawableChild(drawableElement);
    }

    @Inject(method = "initWidgets", at = @At("RETURN"))
    public void injectPostInitWidgets(CallbackInfo ci) {
        if (!addedClientMenuButton)
            addDrawableChild(TarasandeMain.Companion.get().getManagerClientMenu().createButton(5, 5, 98 /* nice magic value mojang */, 20, this));
    }

}
