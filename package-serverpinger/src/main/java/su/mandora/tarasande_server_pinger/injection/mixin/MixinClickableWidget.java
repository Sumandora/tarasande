package su.mandora.tarasande_server_pinger.injection.mixin;

import net.minecraft.client.gui.widget.ClickableWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import su.mandora.tarasande_server_pinger.injection.accessor.IClickableWidget;

@Mixin(ClickableWidget.class)
public class MixinClickableWidget implements IClickableWidget {
    @Shadow
    private int x;

    @Shadow
    private int y;

    @Override
    public void tarasande_init(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
