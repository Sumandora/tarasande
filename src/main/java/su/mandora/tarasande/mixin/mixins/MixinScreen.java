package su.mandora.tarasande.mixin.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.TarasandeMain;
import su.mandora.tarasande.event.EventScreenRender;
import su.mandora.tarasande.mixin.accessor.IScreen;

import java.util.List;

@Mixin(Screen.class)
public class MixinScreen implements IScreen {

    @Shadow
    @Nullable
    protected MinecraftClient client;

    @Shadow
    @Final
    private List<Drawable> drawables;

    @Shadow
    @Final
    private List<Selectable> selectables;

    @Inject(method = "render", at = @At("TAIL"))
    public void injectRender(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        TarasandeMain.Companion.get().getManagerEvent().call(new EventScreenRender(matrices));
    }

    @Override
    public List<Drawable> tarasande_getDrawables() {
        return drawables;
    }

    @Override
    public List<Selectable> tarasande_getSelectables() {
        return selectables;
    }
}
