package net.tarasandedevelopment.tarasande.mixin.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.tarasandedevelopment.tarasande.mixin.accessor.IGameRenderer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.event.EventEntityRaycast;
import net.tarasandedevelopment.tarasande.event.EventUpdateTargetedEntity;

import java.util.function.Predicate;

@Mixin(GameRenderer.class)
public class MixinGameRenderer implements IGameRenderer {

    boolean allowThroughWalls = false;
    boolean disableReachExtension = false;
    double reach = 3.0;

    @Inject(method = "updateTargetedEntity", at = @At("HEAD"))
    public void injectPreUpdateTargetedEntity(float tickDelta, CallbackInfo ci) {
        TarasandeMain.Companion.get().getManagerEvent().call(new EventUpdateTargetedEntity(EventUpdateTargetedEntity.State.PRE));
    }

    @Inject(method = "updateTargetedEntity", at = @At("RETURN"))
    public void injectPostUpdateTargetedEntity(float tickDelta, CallbackInfo ci) {
        TarasandeMain.Companion.get().getManagerEvent().call(new EventUpdateTargetedEntity(EventUpdateTargetedEntity.State.POST));
    }

    @Redirect(method = "updateTargetedEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;raycast(DFZ)Lnet/minecraft/util/hit/HitResult;"))
    public HitResult hookedRaycast(Entity entity, double maxDistance, float tickDelta, boolean includeFluids) {
        if (allowThroughWalls)
            return null;
        return entity.raycast(maxDistance, tickDelta, includeFluids);
    }

    @Redirect(method = "updateTargetedEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;getReachDistance()F"))
    public float hookedGetReachDistance(ClientPlayerInteractionManager clientPlayerInteractionManager) {
        float actualBlockReach = clientPlayerInteractionManager.getReachDistance();
        if (reach > actualBlockReach)
            return (float) reach;
        return actualBlockReach;
    }

    @ModifyConstant(method = "updateTargetedEntity", constant = @Constant(doubleValue = 9.0))
    public double reach(double original) {
        return reach * reach;
    }

    @Redirect(method = "updateTargetedEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;hasExtendedReach()Z"))
    public boolean hookedHasExtendedReach(ClientPlayerInteractionManager instance) {
        if (disableReachExtension)
            return false;

        return instance.hasExtendedReach();
    }

    @Redirect(method = "updateTargetedEntity", at =
    @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/projectile/ProjectileUtil;raycast(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Ljava/util/function/Predicate;D)Lnet/minecraft/util/hit/EntityHitResult;"))
    public @Nullable EntityHitResult hookedRaycast(Entity entity, Vec3d min, Vec3d max, Box box, Predicate<Entity> predicate, double d) {
        EventEntityRaycast eventEntityRaycast = new EventEntityRaycast(MinecraftClient.getInstance().crosshairTarget);
        TarasandeMain.Companion.get().getManagerEvent().call(eventEntityRaycast);
        if (eventEntityRaycast.getCancelled()) {
            return null;
        }
        return ProjectileUtil.raycast(entity, min, max, box, predicate, d);
    }

    @Override
    public boolean tarasande_isAllowThroughWalls() {
        return allowThroughWalls;
    }

    @Override
    public void tarasande_setAllowThroughWalls(boolean allowThroughWalls) {
        this.allowThroughWalls = allowThroughWalls;
    }

    @Override
    public boolean tarasande_isDisableReachExtension() {
        return disableReachExtension;
    }

    @Override
    public void tarasande_setDisableReachExtension(boolean disableReachExtension) {
        this.disableReachExtension = disableReachExtension;
    }

    @Override
    public double tarasande_getReach() {
        return reach;
    }

    @Override
    public void tarasande_setReach(double reach) {
        this.reach = reach;
    }
}
