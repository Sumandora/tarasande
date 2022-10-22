package net.tarasandedevelopment.tarasande.mixin.mixins.screens;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.event.EventChildren;
import net.tarasandedevelopment.tarasande.event.EventScreenRender;
import net.tarasandedevelopment.tarasande.screen.widget.panel.ClickableWidgetPanel;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Screen.class)
public abstract class MixinScreen {

    @Shadow
    @Nullable
    protected MinecraftClient client;

    @Shadow
    protected abstract <T extends Element> T addDrawableChild(T drawableElement);

    @Shadow public abstract List<? extends Element> children();

    @Inject(method = "render", at = @At("HEAD"))
    public void injectRender(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        TarasandeMain.Companion.get().getEventDispatcher().call(new EventScreenRender(matrices, (Screen) (Object) this, mouseX, mouseY));
    }

    @Inject(method = "init(Lnet/minecraft/client/MinecraftClient;II)V", at = @At("RETURN"))
    public void injectInit(MinecraftClient client, int width, int height, CallbackInfo ci) {
        final EventChildren eventChildren = new EventChildren((Screen) (Object) this);
        TarasandeMain.Companion.get().getEventDispatcher().call(eventChildren);

        for (Element element : eventChildren.get())
            this.addDrawableChild(element);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void injectTick(CallbackInfo ci) {
        for (Element child : this.children())
            if (child instanceof ClickableWidgetPanel clickableWidgetPanel)
                clickableWidgetPanel.tick();
    }
}
