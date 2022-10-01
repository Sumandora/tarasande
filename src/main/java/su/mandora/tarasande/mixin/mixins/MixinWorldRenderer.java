package su.mandora.tarasande.mixin.mixins;

import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.TarasandeMain;
import su.mandora.tarasande.event.EventRainGradient;
import su.mandora.tarasande.event.EventRender3D;
import su.mandora.tarasande.mixin.accessor.IWorldRenderer;
import su.mandora.tarasande.module.render.ModuleFog;

@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer implements IWorldRenderer {

    @Shadow
    public static int getLightmapCoordinates(BlockRenderView world, BlockPos pos) {
        return 0;
    }

    @Shadow
    private Frustum frustum;


    @Inject(method = "render", at = @At("TAIL"))
    public void injectRender(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f positionMatrix, CallbackInfo ci) {
        TarasandeMain.Companion.get().getManagerEvent().call(new EventRender3D(matrices, positionMatrix));
    }

    @Inject(method = "renderSky(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/util/math/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V", at = @At("HEAD"), cancellable = true)
    public void injectRenderSky(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, Camera camera, boolean bl, Runnable runnable, CallbackInfo ci) {
        if (TarasandeMain.Companion.get().getManagerModule().get(ModuleFog.class).getEnabled())
            ci.cancel();
    }

    private boolean forceRain = false;

    @Redirect(method = "renderWeather", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getRainGradient(F)F"))
    public float hookedGetRainGradient(ClientWorld instance, float v) {
        EventRainGradient eventRainGradient = new EventRainGradient(v);
        TarasandeMain.Companion.get().getManagerEvent().call(eventRainGradient);
        forceRain = eventRainGradient.getDirty();
        return eventRainGradient.getGradient();
    }

    @Redirect(method = "renderWeather", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/Biome;getPrecipitation()Lnet/minecraft/world/biome/Biome$Precipitation;"))
    public Biome.Precipitation hookedGetPrecipitation(Biome instance) {
        if (forceRain)
            return Biome.Precipitation.RAIN;
        return instance.getPrecipitation();
    }

    @Redirect(method = "renderWeather", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;getLightmapCoordinates(Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/util/math/BlockPos;)I"))
    public int hookedGetLightmapCoordinates(BlockRenderView world, BlockPos pos) {
        if (forceRain)
            return -1;
        return getLightmapCoordinates(world, pos);
    }

    @Override
    public Frustum tarasande_getFrustum() {
        return frustum;
    }
}
