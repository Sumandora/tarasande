package de.florianmichael.tarasande.mixin.mixins;

import de.florianmichael.tarasande.event.EventChildren;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.TarasandeMain;
import su.mandora.tarasande.screen.widget.panel.ClickableWidgetPanel;

import java.util.List;

@Mixin(Screen.class)
public abstract class MixinScreen {

    @Shadow protected abstract <T extends Element & Drawable> T addDrawableChild(Element drawableElement);

    @Shadow public abstract List<? extends Element> children();

    @Inject(method = "init(Lnet/minecraft/client/MinecraftClient;II)V", at = @At("RETURN"))
    public void hookCustomChildren(MinecraftClient client, int width, int height, CallbackInfo ci) {
        final EventChildren eventChildren = new EventChildren((Screen) (Object) this);
        TarasandeMain.Companion.get().getManagerEvent().call(eventChildren);

        for (Element element : eventChildren.get())
            this.addDrawableChild(element);
    }

    @Inject(method = "tick", at = @At("RETURN"))
    public void injectTick(CallbackInfo ci) {
        for (Element child : this.children())
            if (child instanceof ClickableWidgetPanel clickableWidgetPanel)
                clickableWidgetPanel.tick();
    }
}
