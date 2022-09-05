package su.mandora.tarasande.mixin.mixins;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import su.mandora.tarasande.mixin.accessor.IHandledScreen;

@Mixin(HandledScreen.class)
public class MixinHandledScreen implements IHandledScreen {
    @Shadow
    protected int x;

    @Shadow
    protected int y;

    @Shadow
    protected int backgroundWidth;

    @Shadow
    protected int backgroundHeight;

    @Override
    public int tarasande_getX() {
        return this.x;
    }

    @Override
    public int tarasande_getY() {
        return this.y;
    }

    @Override
    public int tarasande_getBackgroundWidth() {
        return this.backgroundWidth;
    }

    @Override
    public int tarasande_getBackgroundHeight() {
        return this.backgroundHeight;
    }
}
