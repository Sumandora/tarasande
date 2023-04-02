package su.mandora.tarasande_custom_minecraft.injection.mixin;

import su.mandora.tarasande_custom_minecraft.tarasandevalues.DesignValues;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import su.mandora.tarasande.util.render.RenderUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntryListWidget.class)
public abstract class MixinEntryListWidget {

    @Shadow public abstract void setScrollAmount(double amount);

    @Shadow public abstract double getScrollAmount();

    @Shadow public abstract int getMaxScroll();

    @Unique
    private double tarasande_Offset;
    @Unique
    private double tarasande_speed;

    @Inject(method = "render", at = @At("HEAD"))
    public void injectRender(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (DesignValues.INSTANCE.getSmoothScrolling().getValue()) {
            this.setScrollAmount(this.getScrollAmount() - this.tarasande_speed);
            this.tarasande_Offset = MathHelper.clamp(this.tarasande_Offset + this.tarasande_speed, Math.min(-getMaxScroll(), 0), 0);
            this.tarasande_speed = MathHelper.clamp(this.tarasande_speed - this.tarasande_speed * RenderUtil.INSTANCE.getDeltaTime() * 0.01, -100.0, 100.0);
        }
    }

    @Inject(method = "mouseScrolled", at = @At("HEAD"), cancellable = true)
    public void addSmoothScrolling(double mouseX, double mouseY, double amount, CallbackInfoReturnable<Boolean> cir) {
        if (DesignValues.INSTANCE.getSmoothScrolling().getValue()) {
            this.tarasande_speed += amount * 3;
            cir.setReturnValue(true);
        }
    }
}
