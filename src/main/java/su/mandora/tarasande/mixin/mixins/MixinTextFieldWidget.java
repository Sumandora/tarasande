package su.mandora.tarasande.mixin.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.mandora.tarasande.mixin.accessor.ITextFieldWidget;

import java.awt.*;

@Mixin(TextFieldWidget.class)
public abstract class MixinTextFieldWidget implements ITextFieldWidget {

    @Shadow
    private boolean selecting;

    @Shadow
    private boolean editable;
    @Shadow
    private String text;
    private Color color = null;

    @Shadow
    protected abstract void erase(int offset);

    @Inject(method = "mouseClicked", at = @At("HEAD"))
    public void injectMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (MinecraftClient.getInstance().currentScreen != null)
            for (Element element : MinecraftClient.getInstance().currentScreen.children())
                if (element != this) {
                    if (element instanceof TextFieldWidget)
                        ((TextFieldWidget) element).setTextFieldFocused(false);
                }
    }

    @Redirect(method = "renderButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;drawWithShadow(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/OrderedText;FFI)I"))
    public int hookedDrawWithShadowOrderedText(TextRenderer textRenderer, MatrixStack matrices, OrderedText text, float x, float y, int color) {
        return textRenderer.drawWithShadow(matrices, text, x, y, this.color != null ? this.color.getRGB() : color);
    }

    @Redirect(method = "renderButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;drawWithShadow(Lnet/minecraft/client/util/math/MatrixStack;Ljava/lang/String;FFI)I"))
    public int hookedDrawWithShadowString(TextRenderer textRenderer, MatrixStack matrices, String text, float x, float y, int color) {
        return textRenderer.drawWithShadow(matrices, text, x, y, this.color != null ? this.color.getRGB() : color);
    }

    @ModifyConstant(method = "renderButton", constant = @Constant(intValue = -3092272))
    public int cursorColor(int original) {
        return this.color != null ? this.color.getRGB() : original;
    }

    @Override
    public boolean isEditable() {
        return editable;
    }

    @Override
    public void setSelecting(boolean selecting) {
        this.selecting = selecting;
    }

    @Override
    public void eraseOffset(int offset) {
        this.erase(offset);
    }

    @Override
    public void setForceText(String text) {
        this.text = text;
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
    }
}
