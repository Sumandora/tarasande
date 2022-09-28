package de.florianmichael.tarasande.mixin.mixins;

import de.florianmichael.tarasande.mixin.accessor.IClickableWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClickableWidget.class)
public class MixinClickableWidget implements IClickableWidget {

    @Unique
    private boolean shouldCancelBackground;

    @Unique
    private boolean validateAllMouseButtons;

    @Unique
    private int lastMouseButton;

    @Redirect(method = "renderButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ClickableWidget;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V"))
    public void removeBackground(ClickableWidget instance, MatrixStack matrixStack, int a, int b, int c, int d, int e, int f) {
        if (!this.shouldCancelBackground)
            instance.drawTexture(matrixStack, a, b, c, d, e, f);
    }

    @Inject(method = "playDownSound", at = @At("HEAD"), cancellable = true)
    public void removeSound(SoundManager soundManager, CallbackInfo ci) {
        if (this.shouldCancelBackground)
            ci.cancel();
    }

    @Inject(method = "isValidClickButton", at = @At("HEAD"), cancellable = true)
    public void removeCheck(int button, CallbackInfoReturnable<Boolean> cir) {
        if (this.validateAllMouseButtons)
            cir.setReturnValue(true);
    }

    @Inject(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ClickableWidget;onClick(DD)V", shift = At.Shift.BEFORE))
    public void hookTracker(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        this.lastMouseButton = button;
    }

    @Override
    public void florianMichael_removeBackground() {
        this.shouldCancelBackground = true;
    }

    @Override
    public void florianMichael_validateAllMouseButtons() {
        this.validateAllMouseButtons = true;
    }

    @Override
    public int florianMichael_getLastMouseButton() {
        return this.lastMouseButton;
    }
}
