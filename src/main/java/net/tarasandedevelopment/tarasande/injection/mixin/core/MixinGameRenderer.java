package net.tarasandedevelopment.tarasande.injection.mixin.core;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.injection.accessor.IGameRenderer;
import net.tarasandedevelopment.tarasande.system.base.grabbersystem.impl.GrabberReach;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameRenderer.class)
public class MixinGameRenderer implements IGameRenderer {

    @Unique
    private boolean tarasande_allowThroughWalls = false;

    @Unique
    private boolean tarasande_disableReachExtension = false;

    @Unique
    private double tarasande_reach = Math.sqrt((double) TarasandeMain.Companion.managerGrabber().getConstant(GrabberReach.class));

    @Redirect(method = "updateTargetedEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;raycast(DFZ)Lnet/minecraft/util/hit/HitResult;"))
    public HitResult throughWalls(Entity entity, double maxDistance, float tickDelta, boolean includeFluids) {
        if (tarasande_allowThroughWalls)
            return null;
        return entity.raycast(maxDistance, tickDelta, includeFluids);
    }

    @Redirect(method = "updateTargetedEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;getReachDistance()F"))
    public float blockReach(ClientPlayerInteractionManager clientPlayerInteractionManager) {
        return Math.max(clientPlayerInteractionManager.getReachDistance(), (float) tarasande_reach);
    }

    @ModifyConstant(method = "updateTargetedEntity", constant = @Constant(doubleValue = 9.0))
    public double reach(double original) {
        return tarasande_reach * tarasande_reach;
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
}
