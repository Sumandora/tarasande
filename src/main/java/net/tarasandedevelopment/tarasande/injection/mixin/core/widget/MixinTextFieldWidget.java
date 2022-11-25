package net.tarasandedevelopment.tarasande.injection.mixin.core.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.tarasandedevelopment.tarasande.injection.accessor.ITextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;

@Mixin(TextFieldWidget.class)
public class MixinTextFieldWidget implements ITextFieldWidget {

    @Unique
    private Color tarasande_color = null;

    @Inject(method = "mouseClicked", at = @At("HEAD"))
    public void fixSelectionBug(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (MinecraftClient.getInstance().currentScreen != null)
            for (Element element : MinecraftClient.getInstance().currentScreen.children())
                if (element != this)
                    if (element instanceof TextFieldWidget)
                        ((TextFieldWidget) element).setTextFieldFocused(false);
    }

    @Redirect(method = "renderButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;drawWithShadow(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/OrderedText;FFI)I"))
    public int injectColor(TextRenderer textRenderer, MatrixStack matrices, OrderedText text, float x, float y, int color) {
        return textRenderer.drawWithShadow(matrices, text, x, y, this.tarasande_color != null ? this.tarasande_color.getRGB() : color);
    }

    @Redirect(method = "renderButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;drawWithShadow(Lnet/minecraft/client/util/math/MatrixStack;Ljava/lang/String;FFI)I"))
    public int injectColor(TextRenderer textRenderer, MatrixStack matrices, String text, float x, float y, int color) {
        return textRenderer.drawWithShadow(matrices, text, x, y, this.tarasande_color != null ? this.tarasande_color.getRGB() : color);
    }

    @ModifyConstant(method = "renderButton", constant = @Constant(intValue = -3092272))
    public int injectColor(int original) {
        return this.tarasande_color != null ? this.tarasande_color.getRGB() : original;
    }

    @Override
    public void tarasande_setColor(Color color) {
        this.tarasande_color = color;
    }

    @Override
    public Color tarasande_getColor() {
        return tarasande_color;
    }
}
