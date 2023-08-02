package su.mandora.tarasande.injection.mixin.event;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.event.EventDispatcher;
import su.mandora.tarasande.event.impl.EventRender3D;

@Mixin(WorldRenderer.class)
public class MixinWorldRenderer {

    @Inject(method = "render", at = @At("HEAD"))
    public void hookPreEventRender3D(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f positionMatrix, CallbackInfo ci) {
        EventDispatcher.INSTANCE.call(new EventRender3D(matrices, positionMatrix, EventRender3D.State.PRE));
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void hookPostEventRender3D(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f positionMatrix, CallbackInfo ci) {
        EventDispatcher.INSTANCE.call(new EventRender3D(matrices, positionMatrix, EventRender3D.State.POST));
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
    }
}
