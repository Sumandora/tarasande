package su.mandora.tarasande.injection.mixin.core.widget;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.OrderedText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.injection.accessor.ITextFieldWidget;

import java.awt.*;

@Mixin(TextFieldWidget.class)
public class MixinTextFieldWidget implements ITextFieldWidget {

    @Unique
    private Color tarasande_color = null;

    @Unique
    private boolean tarasande_selectionHighlight = true;

    @Redirect(method = "renderButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/OrderedText;III)I"))
    public int injectPreColor(DrawContext instance, TextRenderer textRenderer, OrderedText text, int x, int y, int color) {
        return instance.drawTextWithShadow(textRenderer, text, x, y, this.tarasande_color != null ? this.tarasande_color.getRGB() : color);
    }

    @ModifyConstant(method = "renderButton", constant = @Constant(intValue = TextFieldWidget.VERTICAL_CURSOR_COLOR))
    public int injectColor(int original) {
        return this.tarasande_color != null ? this.tarasande_color.getRGB() : original;
    }

    @Inject(method = "drawSelectionHighlight", at = @At("HEAD"), cancellable = true)
    public void cancelSelectionHighlight(DrawContext context, int x1, int y1, int x2, int y2, CallbackInfo ci) {
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
