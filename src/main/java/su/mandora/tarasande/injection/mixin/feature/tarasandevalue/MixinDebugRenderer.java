package su.mandora.tarasande.injection.mixin.feature.tarasandevalue;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.feature.tarasandevalue.impl.debug.MinecraftDebugger;

@Mixin(DebugRenderer.class)
public class MixinDebugRenderer {

    @Shadow
    @Final
    public DebugRenderer.Renderer collisionDebugRenderer;

    @Shadow
    @Final
    public DebugRenderer.Renderer chunkLoadingDebugRenderer;

    @Shadow
    @Final
    public DebugRenderer.Renderer blockOutlineDebugRenderer;

    @Shadow
    @Final
    public DebugRenderer.Renderer skyLightDebugRenderer;

    @Shadow
    @Final
    public DebugRenderer.Renderer waterDebugRenderer;

    @Inject(method = "render", at = @At("RETURN"))
    public void forceDebugRenderer(MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, double cameraX, double cameraY, double cameraZ, CallbackInfo ci) {
        if (MinecraftDebugger.INSTANCE.getCollision().getValue())
            this.collisionDebugRenderer.render(matrices, vertexConsumers, cameraX, cameraY, cameraZ);
        if (MinecraftDebugger.INSTANCE.getChunkLoading().getValue())
            this.chunkLoadingDebugRenderer.render(matrices, vertexConsumers, cameraX, cameraY, cameraZ);
        if (MinecraftDebugger.INSTANCE.getBlockOutline().getValue())
            this.blockOutlineDebugRenderer.render(matrices, vertexConsumers, cameraX, cameraY, cameraZ);
        if (MinecraftDebugger.INSTANCE.getSkyLight().getValue())
            this.skyLightDebugRenderer.render(matrices, vertexConsumers, cameraX, cameraY, cameraZ);
        if (MinecraftDebugger.INSTANCE.getWater().getValue())
            this.waterDebugRenderer.render(matrices, vertexConsumers, cameraX, cameraY, cameraZ);
    }
}
