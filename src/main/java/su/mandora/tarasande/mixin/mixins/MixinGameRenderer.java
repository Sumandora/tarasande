package su.mandora.tarasande.mixin.mixins;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.TarasandeMain;
import su.mandora.tarasande.event.EventMovementFovMultiplier;
import su.mandora.tarasande.event.EventRender3D;
import su.mandora.tarasande.event.EventUpdateTargetedEntity;
import su.mandora.tarasande.mixin.accessor.IGameRenderer;

@Mixin(GameRenderer.class)
public class MixinGameRenderer implements IGameRenderer {

    boolean allowThroughWalls = false;
    boolean disableReachExtension = false;
    double reach = 3.0;
    @Shadow
    private float fovMultiplier;

    @Redirect(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;render(Lnet/minecraft/client/util/math/MatrixStack;FJZLnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/GameRenderer;Lnet/minecraft/client/render/LightmapTextureManager;Lnet/minecraft/util/math/Matrix4f;)V"))
    public void hookedRender(WorldRenderer instance, MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f positionMatrix) {
        instance.render(matrices, tickDelta, limitTime, renderBlockOutline, camera, gameRenderer, lightmapTextureManager, positionMatrix);
        TarasandeMain.Companion.get().getManagerEvent().call(new EventRender3D(matrices, positionMatrix));
    }

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

    @Inject(method = "updateFovMultiplier", at = @At("TAIL"))
    public void injectUpdateMovementFovMultiplier(CallbackInfo ci) {
        EventMovementFovMultiplier eventMovementFovMultiplier = new EventMovementFovMultiplier(fovMultiplier);
        TarasandeMain.Companion.get().getManagerEvent().call(eventMovementFovMultiplier);
        fovMultiplier = eventMovementFovMultiplier.getMovementFovMultiplier();
    }

    @ModifyConstant(method = "updateTargetedEntity", constant = @Constant(doubleValue = 9.0))
    public double reach(double original) {
        return reach * reach;
    }

    @Redirect(method = "updateTargetedEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;hasExtendedReach()Z"))
    public boolean hookedHasExtendedReach(ClientPlayerInteractionManager instance) {
        if (!disableReachExtension)
            return instance.hasExtendedReach();
        else
            return false;
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
