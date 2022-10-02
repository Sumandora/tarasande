package net.tarasandedevelopment.tarasande.mixin.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.screen.list.clientmenu.ScreenBetterClientMenu;

@Mixin(TitleScreen.class)
public class MixinTitleScreen extends Screen {
    protected MixinTitleScreen(Text title) {
        super(title);
    }

    @Redirect(method = "initWidgetsNormal", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/TitleScreen;addDrawableChild(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;"))
    public <T extends Element & Drawable & Selectable> T hookedAddDrawableChild(TitleScreen titleScreen, T drawableElement) {
        if (drawableElement instanceof ButtonWidget buttonWidget) {
            if ((buttonWidget.getMessage().getContent() instanceof TranslatableTextContent && ((TranslatableTextContent) buttonWidget.getMessage().getContent()).getKey().equals("menu.online")) || buttonWidget.getMessage().getString().contains("Realms")) {
                buttonWidget.setWidth(buttonWidget.getWidth() / 2 - 2);
                final String selected = TarasandeMain.Companion.get().getClientValues().getClientMenuFocusedEntry().getSelected().get(0);

                Text buttonText = Text.literal((Character.toUpperCase(TarasandeMain.Companion.get().getName().charAt(0)) + TarasandeMain.Companion.get().getName().substring(1)) + " Menu");
                if (this.anySelected()) {
                    buttonText = Text.literal(selected);
                }

                addDrawableChild(new ButtonWidget(buttonWidget.x + buttonWidget.getWidth() + 4, buttonWidget.y, buttonWidget.getWidth(), buttonWidget.getHeight(), buttonText, button -> {
                    if (this.anySelected() && !Screen.hasShiftDown()) {
                        TarasandeMain.Companion.get().getManagerClientMenu().byName(selected).onClick(GLFW.GLFW_MOUSE_BUTTON_LEFT);
                        return;
                    }

                    MinecraftClient.getInstance().setScreen(new ScreenBetterClientMenu(this));
                }));
            }
        }
        return addDrawableChild(drawableElement);
    }

    @Unique
    private boolean anySelected() {
        return TarasandeMain.Companion.get().getClientValues().getClientMenuFocusedEntry().anySelected() && !TarasandeMain.Companion.get().getClientValues().getClientMenuFocusedEntry().getSelected().get(0).equals("None");
    }
}
