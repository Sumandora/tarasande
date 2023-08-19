package su.mandora.tarasande.injection.mixin.core;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.injection.accessor.IGameRenderer;
import su.mandora.tarasande.util.MinecraftConstantsKt;

@Mixin(GameRenderer.class)
public class MixinGameRenderer implements IGameRenderer {

    @Shadow
    @Final
    MinecraftClient client;

    @Unique
    private boolean tarasande_allowThroughWalls = false;

    @Unique
    private boolean tarasande_disableReachExtension = false;

    @Unique
    private double tarasande_reach = MinecraftConstantsKt.DEFAULT_REACH;

    @Unique
    private double tarasande_blockreach = MinecraftConstantsKt.DEFAULT_BLOCK_REACH;

    @Unique
    private boolean tarasande_selfInflicted = false;

    @Inject(method = "updateTargetedEntity", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;crosshairTarget:Lnet/minecraft/util/hit/HitResult;", ordinal = 0, shift = At.Shift.AFTER), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;getReachDistance()F")))
    public void throughWalls(float tickDelta, CallbackInfo ci) {
        if (tarasande_allowThroughWalls)
            client.crosshairTarget = null;
    }

    @Redirect(method = "updateTargetedEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;getReachDistance()F"))
    public float blockReach(ClientPlayerInteractionManager clientPlayerInteractionManager) {
        return (float) tarasande_blockreach;
    }

    @Redirect(method = "updateTargetedEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;squaredDistanceTo(Lnet/minecraft/util/math/Vec3d;)D"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/util/hit/EntityHitResult;getPos()Lnet/minecraft/util/math/Vec3d;")))
    public double reach(Vec3d instance, Vec3d vec) {
        return Math.pow(instance.distanceTo(vec) / tarasande_reach * MinecraftConstantsKt.DEFAULT_REACH, 2);
    }

    @Redirect(method = "updateTargetedEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;hasExtendedReach()Z"))
    public boolean ignoreExtendedReach(ClientPlayerInteractionManager instance) {
        if (tarasande_disableReachExtension)
            return false;

        return instance.hasExtendedReach();
    }

    @Override
    public boolean tarasande_isAllowThroughWalls() {
        return tarasande_allowThroughWalls;
    }

    @Override
    public void tarasande_setAllowThroughWalls(boolean allowThroughWalls) {
        this.tarasande_allowThroughWalls = allowThroughWalls;
    }

    @Override
    public boolean tarasande_isDisableReachExtension() {
        return tarasande_disableReachExtension;
    }

    @Override
    public void tarasande_setDisableReachExtension(boolean disableReachExtension) {
        this.tarasande_disableReachExtension = disableReachExtension;
    }

    @Override
    public double tarasande_getReach() {
        return tarasande_reach;
    }

    @Override
    public void tarasande_setReach(double reach) {
        this.tarasande_reach = reach;
    }

    @Override
    public double tarasande_getBlockReach() {
        return tarasande_blockreach;
    }

    @Override
    public void tarasande_setBlockReach(double reach) {
        this.tarasande_blockreach = reach;
    }

    @Override
    public boolean tarasande_isSelfInflicted() {
        return tarasande_selfInflicted;
    }

    @Override
    public void tarasande_setSelfInflicted(boolean selfInflicted) {
        tarasande_selfInflicted = selfInflicted;
    }
}
