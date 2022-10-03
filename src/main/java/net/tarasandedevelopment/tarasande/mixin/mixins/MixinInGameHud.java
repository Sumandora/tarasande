package net.tarasandedevelopment.tarasande.mixin.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.event.EventRender2D;
import net.tarasandedevelopment.tarasande.mixin.accessor.IInGameHud;
import net.tarasandedevelopment.tarasande.util.math.MathUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class MixinInGameHud implements IInGameHud {

    @Shadow
    protected abstract void renderHotbarItem(int x, int y, float tickDelta, PlayerEntity player, ItemStack stack, int seed);

    @Inject(method = "render", at = @At("TAIL"))
    public void injectRender(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        TarasandeMain.Companion.get().getManagerEvent().call(new EventRender2D(matrices));
    }

    @Override
    public void tarasande_invokeRenderHotbarItem(int x, int y, float tickDelta, PlayerEntity player, ItemStack stack, int seed) {
        renderHotbarItem(x, y, tickDelta, player, stack, seed);
    }

    @Override
    public void tarasande_invokeRenderHotbarItem(int x, int y, float tickDelta, MatrixStack matrices, ItemStack stack) {
        final Vec3d position = MathUtil.INSTANCE.fromMatrices(matrices);

        renderHotbarItem((int) (position.x + x), (int) (position.y + y), tickDelta, MinecraftClient.getInstance().player, stack, 0);
    }
}
