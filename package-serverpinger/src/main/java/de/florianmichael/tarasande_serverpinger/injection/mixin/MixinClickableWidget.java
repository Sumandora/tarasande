package de.florianmichael.tarasande_serverpinger.injection.mixin;

import de.florianmichael.tarasande_serverpinger.injection.accessor.IClickableWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClickableWidget.class)
public class MixinClickableWidget implements IClickableWidget {
    @Shadow private int x;

    @Shadow private int y;

    // I FUCKING HATE MY ENTIRE LIFE $$$$$$$$$$$$$$$$$$$$$$$$$$$$$44
    @Override
    public void tarasande_init(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
