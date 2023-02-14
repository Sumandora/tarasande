package net.tarasandedevelopment.tarasande.injection.mixin.event.screen;

import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.realms.gui.screen.RealmsNotificationsScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.tarasandedevelopment.tarasande.event.EventChildren;
import net.tarasandedevelopment.tarasande.event.EventScreenRender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.event.EventDispatcher;

import java.util.ArrayList;

@Mixin(Screen.class)
public abstract class MixinScreen {

    @Shadow
    protected abstract Element addDrawableChild(Element drawableElement);

    @Inject(method = "clearAndInit", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;init()V"))
    public void hookEventChildren(CallbackInfo ci) {
        final EventChildren eventChildren = new EventChildren((Screen) (Object) this, new ArrayList<>());
        EventDispatcher.INSTANCE.call(eventChildren);
        eventChildren.getElements().forEach(this::addDrawableChild);
    }

    @Inject(method = "render", at = @At("HEAD"))
    public void hookEventScreenRender(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        final Screen thisScreen = (Screen) (Object) this;
        // Mojang manages to surprise me in so many ways
        if (thisScreen instanceof RealmsNotificationsScreen) {
            return;
        }

        EventDispatcher.INSTANCE.call(new EventScreenRender(matrices, thisScreen, EventScreenRender.State.PRE));
    }
}
