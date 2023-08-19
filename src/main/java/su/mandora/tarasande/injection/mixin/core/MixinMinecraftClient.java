package su.mandora.tarasande.injection.mixin.core;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.TarasandeMain;
import su.mandora.tarasande.util.render.RenderUtil;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {

    @Shadow
    @Final
    public GameOptions options;

    @Shadow
    @Nullable
    public ClientPlayerEntity player;

    @Unique
    private long tarasande_startTime = 0L;

    @Shadow
    public abstract void setScreen(@Nullable Screen screen);

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;setOverlay(Lnet/minecraft/client/gui/screen/Overlay;)V", shift = At.Shift.AFTER))
    public void lateLoadClient(RunArgs args, CallbackInfo ci) {
        TarasandeMain.INSTANCE.onLateLoad();
    }

    @Inject(method = "render", at = @At("HEAD"))
    public void trackRenderStart(boolean tick, CallbackInfo ci) {
        this.tarasande_startTime = System.nanoTime();
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void calculateDeltaTime(boolean tick, CallbackInfo ci) {
        RenderUtil.INSTANCE.setDeltaTime((System.nanoTime() - this.tarasande_startTime) / 1000000.0 /*nanos to millis*/);
    }
}
