package su.mandora.tarasande.mixin.mixins;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.TarasandeMain;
import su.mandora.tarasande.event.EventRender3D;
import su.mandora.tarasande.mixin.accessor.IGameRenderer;

@Mixin(GameRenderer.class)
public class MixinGameRenderer implements IGameRenderer {

    boolean allowThroughWalls = false;
    double reach = 3.0;

    @Inject(method = "renderWorld", at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/GameRenderer;renderHand:Z"))
    public void injectRender(float tickDelta, long limitTime, MatrixStack matrix, CallbackInfo ci) {
        TarasandeMain.Companion.get().getManagerEvent().call(new EventRender3D(matrix));
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

    @Override
    public void setAllowThroughWalls(boolean allowThroughWalls) {
        this.allowThroughWalls = allowThroughWalls;
    }

    @Override
    public boolean isAllowThroughWalls() {
        return allowThroughWalls;
    }

    @Override
    public void setReach(double reach) {
        this.reach = reach;
    }

    @Override
    public double getReach() {
        return reach;
    }
}
