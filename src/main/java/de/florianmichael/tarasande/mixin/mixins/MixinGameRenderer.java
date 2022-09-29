package de.florianmichael.tarasande.mixin.mixins;

import de.florianmichael.tarasande.module.player.ModuleNoMiningTrace;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.TarasandeMain;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {

    @Inject(method = "updateTargetedEntity", at =
    @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/projectile/ProjectileUtil;raycast(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Ljava/util/function/Predicate;D)Lnet/minecraft/util/hit/EntityHitResult;"),
            cancellable = true)
    public void hookHack(float tickDelta, CallbackInfo ci) {
        if (TarasandeMain.Companion.get().getManagerModule().get(ModuleNoMiningTrace.class).shouldDo() && MinecraftClient.getInstance().crosshairTarget.getType() == HitResult.Type.BLOCK) {
            MinecraftClient.getInstance().getProfiler().pop();
            ci.cancel();
        }
    }
}
