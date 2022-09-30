package su.mandora.tarasande.mixin.mixins;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.TarasandeMain;
import su.mandora.tarasande.event.EventUpdateTargetedEntity;
import su.mandora.tarasande.mixin.accessor.IGameRenderer;

@Mixin(GameRenderer.class)
public class MixinGameRenderer implements IGameRenderer {

    boolean allowThroughWalls = false;
    boolean disableReachExtension = false;
    double reach = 3.0;
    @Shadow
    private float fovMultiplier;

    @Inject(method = "updateTargetedEntity", at = @At("HEAD"))
    public void injectUpdateTargetedEntity(float tickDelta, CallbackInfo ci) {
        TarasandeMain.Companion.get().getManagerEvent().call(new EventUpdateTargetedEntity());
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
