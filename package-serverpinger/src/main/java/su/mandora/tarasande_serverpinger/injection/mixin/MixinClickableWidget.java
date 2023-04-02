package su.mandora.tarasande_serverpinger.injection.mixin;

import su.mandora.tarasande_serverpinger.injection.accessor.IClickableWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClickableWidget.class)
public class MixinClickableWidget implements IClickableWidget {
    @Shadow private int x;

    @Shadow private int y;

    @Override
    public void tarasande_init(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
