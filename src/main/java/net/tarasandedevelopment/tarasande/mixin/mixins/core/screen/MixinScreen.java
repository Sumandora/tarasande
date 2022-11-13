package net.tarasandedevelopment.tarasande.mixin.mixins.core.screen;

import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.tarasandedevelopment.tarasande.systems.screen.panelsystem.api.ClickableWidgetPanel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Screen.class)
public abstract class MixinScreen {

    @Shadow
    public abstract List<? extends Element> children();

    @Inject(method = "tick", at = @At("HEAD"))
    public void tickClickableWidgetPanel(CallbackInfo ci) {
        for (Element child : this.children())
            if (child instanceof ClickableWidgetPanel clickableWidgetPanel)
                clickableWidgetPanel.tick();
    }
}
