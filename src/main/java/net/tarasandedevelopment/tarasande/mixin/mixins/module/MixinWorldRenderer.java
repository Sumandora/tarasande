package net.tarasandedevelopment.tarasande.mixin.mixins.module;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.biome.Biome;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.module.render.ModuleFog;
import net.tarasandedevelopment.tarasande.module.render.ModuleRain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class MixinWorldRenderer {

    @Shadow
    public static int getLightmapCoordinates(BlockRenderView world, BlockPos pos) {
        return 0;
    }

    @Unique
    private boolean tarasande_forceRain = false;

    @Inject(method = "renderSky(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/util/math/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V", at = @At("HEAD"), cancellable = true)
    public void hookFog(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, Camera camera, boolean bl, Runnable runnable, CallbackInfo ci) {
        if (!TarasandeMain.Companion.get().getDisabled())
            if (TarasandeMain.Companion.get().getManagerModule().get(ModuleFog.class).getEnabled())
                ci.cancel();
    }

    @Redirect(method = "renderWeather", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getRainGradient(F)F"))
    public float hookRain(ClientWorld instance, float v) {
        tarasande_forceRain = false;
        if (!TarasandeMain.Companion.get().getDisabled()) {
            ModuleRain moduleRain = TarasandeMain.Companion.get().getManagerModule().get(ModuleRain.class);
            if (moduleRain.getEnabled()) {
                tarasande_forceRain = true;
                return (float) moduleRain.getGradient().getValue();
            }
        }
        return instance.getRainGradient(v);
    }

    @Redirect(method = "renderWeather", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/Biome;getPrecipitation()Lnet/minecraft/world/biome/Biome$Precipitation;"))
    public Biome.Precipitation hookRain(Biome instance) {
        if (tarasande_forceRain)
            return Biome.Precipitation.RAIN;
        return instance.getPrecipitation();
    }

    @Redirect(method = "renderWeather", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;getLightmapCoordinates(Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/util/math/BlockPos;)I"))
    public int hookRain(BlockRenderView world, BlockPos pos) {
        if (tarasande_forceRain)
            return -1;
        return getLightmapCoordinates(world, pos);
    }
}
