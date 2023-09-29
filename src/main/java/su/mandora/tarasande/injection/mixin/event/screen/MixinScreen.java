package su.mandora.tarasande.injection.mixin.event.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.realms.gui.screen.RealmsNotificationsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.event.EventDispatcher;
import su.mandora.tarasande.event.impl.EventChildren;
import su.mandora.tarasande.event.impl.EventScreenRender;

@Mixin(Screen.class)
public abstract class MixinScreen {

    @Shadow
    private boolean screenInitialized;

    @Inject(method = "init(Lnet/minecraft/client/MinecraftClient;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;init()V", shift = At.Shift.AFTER))
    public void hookEventChildrenFirst(MinecraftClient client, int width, int height, CallbackInfo ci) {
        EventDispatcher.INSTANCE.call(new EventChildren((Screen) (Object) this));
    }

    @Inject(method = "initTabNavigation", at = @At("RETURN"))
    public void hookEventChildren(CallbackInfo ci) {
        if (this.screenInitialized)
            EventDispatcher.INSTANCE.call(new EventChildren((Screen) (Object) this));
    }


    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;renderBackground(Lnet/minecraft/client/gui/DrawContext;IIF)V", shift = At.Shift.AFTER))
    public void hookEventScreenRenderPre(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        final Screen thisScreen = (Screen) (Object) this;
        // Mojang manages to surprise me in so many ways
        if (thisScreen instanceof RealmsNotificationsScreen) {
            return;
        }

        EventDispatcher.INSTANCE.call(new EventScreenRender(context, thisScreen, EventScreenRender.State.PRE));
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void hookEventScreenRenderPost(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        final Screen thisScreen = (Screen) (Object) this;
        // Mojang manages to surprise me in so many ways
        if (thisScreen instanceof RealmsNotificationsScreen) {
            return;
        }

        EventDispatcher.INSTANCE.call(new EventScreenRender(context, thisScreen, EventScreenRender.State.POST));
    }
}
