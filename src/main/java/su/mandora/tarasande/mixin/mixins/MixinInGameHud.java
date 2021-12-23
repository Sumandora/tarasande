package su.mandora.tarasande.mixin.mixins;

import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.TarasandeMain;
import su.mandora.tarasande.event.EventRender2D;
import su.mandora.tarasande.mixin.accessor.IInGameHud;

@Mixin(InGameHud.class)
public abstract class MixinInGameHud implements IInGameHud {

    @Shadow
    protected abstract void renderHotbarItem(int x, int y, float tickDelta, PlayerEntity player, ItemStack stack, int seed);

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;enableBlend()V", shift = At.Shift.BEFORE, ordinal = 0))
    public void injectRender(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        TarasandeMain.Companion.get().getManagerEvent().call(new EventRender2D(matrices));
    }

    @Override
    public void invokeRenderHotbarItem(int x, int y, float tickDelta, PlayerEntity player, ItemStack stack, int seed) {
        renderHotbarItem(x, y, tickDelta, player, stack, seed);
    }
}
