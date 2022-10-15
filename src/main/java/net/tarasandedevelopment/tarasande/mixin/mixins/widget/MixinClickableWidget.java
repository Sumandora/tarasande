package net.tarasandedevelopment.tarasande.mixin.mixins.widget;

import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.math.MatrixStack;
import net.tarasandedevelopment.tarasande.mixin.accessor.IClickableWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClickableWidget.class)
public class MixinClickableWidget implements IClickableWidget {

    @Unique
    private boolean shouldCancelBackground;

    @Redirect(method = "renderButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ClickableWidget;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V"))
    public void hookedDrawTexture(ClickableWidget instance, MatrixStack matrixStack, int a, int b, int c, int d, int e, int f) {
        if (!this.shouldCancelBackground)
            instance.drawTexture(matrixStack, a, b, c, d, e, f);
    }

    @Inject(method = "playDownSound", at = @At("HEAD"), cancellable = true)
    public void injectPlayDownSound(SoundManager soundManager, CallbackInfo ci) {
        if (this.shouldCancelBackground)
            ci.cancel();
    }

    @Override
    public void tarasande_removeBackground() {
        this.shouldCancelBackground = true;
    }
}
