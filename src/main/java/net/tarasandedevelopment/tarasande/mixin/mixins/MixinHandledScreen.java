package net.tarasandedevelopment.tarasande.mixin.mixins;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.text.Text;
import net.tarasandedevelopment.tarasande.mixin.accessor.IHandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(HandledScreen.class)
public class MixinHandledScreen extends Screen implements IHandledScreen {
    @Shadow
    protected int x;

    @Shadow
    protected int y;

    @Shadow
    protected int backgroundWidth;

    @Shadow
    protected int backgroundHeight;

    protected MixinHandledScreen(Text title) {
        super(title);
    }

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

    @Override
    public Text tarasande_getTitle() {
        return title;
    }
}
