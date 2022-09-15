package su.mandora.tarasande.mixin.mixins;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.WorldRenderer;
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
import su.mandora.tarasande.mixin.accessor.IWorldRenderer;
import su.mandora.tarasande.module.render.ModuleFog;
import su.mandora.tarasande.module.render.ModuleRain;

@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer implements IWorldRenderer {

    @Shadow
    public static int getLightmapCoordinates(BlockRenderView world, BlockPos pos) {
        return 0;
    }

    @Shadow private Frustum frustum;

    @Inject(method = "renderSky(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/util/math/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V", at = @At("HEAD"), cancellable = true)
    public void injectRenderSky(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, Camera camera, boolean bl, Runnable runnable, CallbackInfo ci) {
        if (TarasandeMain.Companion.get().getManagerModule().get(ModuleFog.class).getEnabled())
            ci.cancel();
    }

    @Redirect(method = "renderWeather", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getRainGradient(F)F"))
    public float hookedGetRainGradient(ClientWorld instance, float v) {
        ModuleRain moduleRain = TarasandeMain.Companion.get().getManagerModule().get(ModuleRain.class);
        if(moduleRain.getEnabled())
            return (float) moduleRain.getGradient().getValue();
        return instance.getRainGradient(v);
    }

    @Redirect(method = "renderWeather", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/Biome;getPrecipitation()Lnet/minecraft/world/biome/Biome$Precipitation;"))
    public Biome.Precipitation hookedGetPrecipitation(Biome instance) {
        ModuleRain moduleRain = TarasandeMain.Companion.get().getManagerModule().get(ModuleRain.class);
        if(moduleRain.getEnabled())
            return Biome.Precipitation.RAIN;
        return instance.getPrecipitation();
    }

    @Redirect(method = "renderWeather", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;getLightmapCoordinates(Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/util/math/BlockPos;)I"))
    public int hookedGetLightmapCoordinates(BlockRenderView world, BlockPos pos) {
        ModuleRain moduleRain = TarasandeMain.Companion.get().getManagerModule().get(ModuleRain.class);
        if(moduleRain.getEnabled())
            return -1;
        return getLightmapCoordinates(world, pos);
    }

    @Override
    public Frustum tarasande_getFrustum() {
        return frustum;
    }
}
