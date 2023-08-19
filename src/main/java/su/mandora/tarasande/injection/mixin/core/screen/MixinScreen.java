package su.mandora.tarasande.injection.mixin.core.screen;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.injection.accessor.IScreen;
import su.mandora.tarasande.system.screen.panelsystem.api.ClickableWidgetPanel;

import java.util.List;

@Mixin(Screen.class)
public abstract class MixinScreen implements IScreen {

    @Shadow
    public abstract List<? extends Element> children();

    @Shadow protected abstract <T extends Element & Drawable> T addDrawableChild(T drawableElement);

    @Inject(method = "tick", at = @At("HEAD"))
    public void tickClickableWidgetPanel(CallbackInfo ci) {
        for (Element child : this.children())
            if (child instanceof ClickableWidgetPanel clickableWidgetPanel)
                clickableWidgetPanel.tick();
    }

    @Override
    public <T> T tarasande_addDrawableChild(T t) {
        return (T) addDrawableChild((Element & Drawable) t);
    }
}
