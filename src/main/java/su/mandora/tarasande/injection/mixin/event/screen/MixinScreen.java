package su.mandora.tarasande.injection.mixin.event.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.realms.gui.screen.RealmsNotificationsScreen;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.event.EventDispatcher;
import su.mandora.tarasande.event.impl.EventChildren;
import su.mandora.tarasande.event.impl.EventScreenRender;

import java.util.ArrayList;

@Mixin(Screen.class)
public abstract class MixinScreen {

    @Shadow
    protected abstract Element addDrawableChild(Element drawableElement);

    @Shadow private boolean screenInitialized;

    @Inject(method = "init(Lnet/minecraft/client/MinecraftClient;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;init()V", shift = At.Shift.AFTER))
    public void hookEventChildrenFirst(MinecraftClient client, int width, int height, CallbackInfo ci) {
        callEventChildren();
    }

    @Inject(method = "initTabNavigation", at = @At("RETURN"))
    public void hookEventChildren(CallbackInfo ci) {
        if (this.screenInitialized) callEventChildren();
    }

    private void callEventChildren() {
        final EventChildren eventChildren = new EventChildren((Screen) (Object) this, new ArrayList<>());
        EventDispatcher.INSTANCE.call(eventChildren);
        eventChildren.getElements().forEach(this::addDrawableChild);
    }

    @Inject(method = "render", at = @At("HEAD"))
    public void hookEventScreenRenderPre(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        final Screen thisScreen = (Screen) (Object) this;
        // Mojang manages to surprise me in so many ways
        if (thisScreen instanceof RealmsNotificationsScreen) {
            return;
        }

        EventDispatcher.INSTANCE.call(new EventScreenRender(matrices, thisScreen, EventScreenRender.State.PRE));
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void hookEventScreenRenderPost(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        final Screen thisScreen = (Screen) (Object) this;
        // Mojang manages to surprise me in so many ways
        if (thisScreen instanceof RealmsNotificationsScreen) {
            return;
        }

        EventDispatcher.INSTANCE.call(new EventScreenRender(matrices, thisScreen, EventScreenRender.State.POST));
    }
}
