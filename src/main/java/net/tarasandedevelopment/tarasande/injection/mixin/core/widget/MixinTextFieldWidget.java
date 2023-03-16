package net.tarasandedevelopment.tarasande.injection.mixin.core.widget;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.tarasandedevelopment.tarasande.injection.accessor.ITextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(TextFieldWidget.class)
public class MixinTextFieldWidget implements ITextFieldWidget {

    @Unique
    private Color tarasande_color = null;

    @Unique
    private boolean tarasande_selectionHighlight = true;

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

    @Inject(method = "drawSelectionHighlight", at = @At("HEAD"), cancellable = true)
    public void cancelSelectionHighlight(MatrixStack matrices, int x1, int y1, int x2, int y2, CallbackInfo ci) {
        if (!tarasande_selectionHighlight)
            ci.cancel();
    }

    @Override
    public void tarasande_setColor(Color color) {
        this.tarasande_color = color;
    }

    @Override
    public Color tarasande_getColor() {
        return tarasande_color;
    }

    @Override
    public void tarasande_disableSelectionHighlight() {
        tarasande_selectionHighlight = false;
    }
}
