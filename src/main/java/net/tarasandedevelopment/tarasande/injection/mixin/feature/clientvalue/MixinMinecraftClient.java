package net.tarasandedevelopment.tarasande.injection.mixin.feature.clientvalue;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {

    @Shadow
    public int attackCooldown;

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I"))
    public int unlockTicksPerFrame(int a, int b) {
        if (TarasandeMain.Companion.clientValues().getUnlockTicksPerFrame().getValue()) {
            return b;
        }
        return Math.min(a, b);
    }

    @Redirect(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;resetDebugHudChunk()V"), to = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;handleInputEvents()V")))
    public Screen passEventsInScreens(MinecraftClient instance) {
        if (TarasandeMain.Companion.clientValues().getPassEventsInScreens().getValue())
            if (MinecraftClient.getInstance().player != null)
                return null;
        return instance.currentScreen;
    }

    @ModifyConstant(method = "tick", constant = @Constant(intValue = 10000))
    public int ignoreCooldown(int constant) {
        if (TarasandeMain.Companion.clientValues().getPassEventsInScreens().getValue())
            return attackCooldown;
        return constant;
    }
}