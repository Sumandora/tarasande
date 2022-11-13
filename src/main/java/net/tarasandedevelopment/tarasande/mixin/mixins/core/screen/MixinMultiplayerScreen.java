package net.tarasandedevelopment.tarasande.mixin.mixins.core.screen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.text.Text;
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.valuecomponent.ElementValueComponent;
import net.tarasandedevelopment.tarasande.systems.screen.panelsystem.api.ClickableWidgetPanelSidebar;
import net.tarasandedevelopment.tarasande.systems.screen.panelsystem.api.PanelElements;
import net.tarasandedevelopment.tarasande.endme.ADGADGADGADG;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerScreen.class)
public class MixinMultiplayerScreen extends Screen {

    protected MixinMultiplayerScreen(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("HEAD"))
    public void adoigjag(CallbackInfo ci) {
        addDrawableChild(new ClickableWidgetPanelSidebar(new PanelElements<ElementValueComponent>("Hello, World/Sidebar", 300.0) {
            {
                this.getElementList().add(ADGADGADGADG.provide(this).createValueComponent());
                this.getElementList().add(ADGADGADGADG.provide(this).createValueComponent());
                this.getElementList().add(ADGADGADGADG.provide(this).createValueComponent());
                this.getElementList().add(ADGADGADGADG.provide(this).createValueComponent());
                this.getElementList().add(ADGADGADGADG.provide(this).createValueComponent());
                this.getElementList().add(ADGADGADGADG.provide(this).createValueComponent());
            }
        }));
    }

}
