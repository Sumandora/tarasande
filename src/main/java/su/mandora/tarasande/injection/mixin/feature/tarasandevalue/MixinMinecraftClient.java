package su.mandora.tarasande.injection.mixin.feature.tarasandevalue;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.mandora.tarasande.feature.tarasandevalue.TarasandeValues;
import su.mandora.tarasande.feature.tarasandevalue.impl.DebugValues;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {

    @Shadow
    public int attackCooldown;

    @Shadow
    @Nullable
    public ClientPlayerEntity player;

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I"))
    public int unlockTicksPerFrame(int a, int b) {
        if (TarasandeValues.INSTANCE.getUnlockTicksPerFrame().getValue()) {
            return b;
        }
        return Math.min(a, b);
    }

    @Redirect(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;resetDebugHudChunk()V"), to = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;handleInputEvents()V")))
    public Screen passEventsInScreens(MinecraftClient instance) {
        if (TarasandeValues.INSTANCE.getPassEventsInScreens().getValue())
            if (player != null)
                return null;
        return instance.currentScreen;
    }

    @ModifyConstant(method = "tick", constant = @Constant(intValue = 10000))
    public int ignoreCooldown(int constant) {
        if (TarasandeValues.INSTANCE.getPassEventsInScreens().getValue())
            return attackCooldown;
        return constant;
    }

    @Redirect(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/RenderTickCounter;tickDelta:F", ordinal = 0))
    public float disableInterpolation(RenderTickCounter instance) {
        if (DebugValues.INSTANCE.getDisableInterpolation().getValue())
            return 1.0F;
        return instance.tickDelta;
    }

    @Inject(method = "getTickDelta", at = @At("HEAD"), cancellable = true)
    public void disableInterpolation(CallbackInfoReturnable<Float> cir) {
        if (DebugValues.INSTANCE.getDisableInterpolation().getValue())
            cir.setReturnValue(1.0F);
    }

}