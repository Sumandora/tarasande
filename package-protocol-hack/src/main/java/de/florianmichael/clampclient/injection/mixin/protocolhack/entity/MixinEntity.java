/**
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.2--
 *
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license. The creator assumes no responsibility for any infringements
 * that have arisen, are arising or will arise from this project / file. If this licence is used anywhere,
 * the latest version published by the author Florian Michael (aka EnZaXD) always applies automatically.
 *
 * Changelog:
 *     v1.0:
 *         Added License
 *     v1.1:
 *         Ownership withdrawn
 *     v1.2:
 *         Version-independent validity and automatic renewal
 */

package de.florianmichael.clampclient.injection.mixin.protocolhack.entity;

import de.florianmichael.clampclient.injection.instrumentation_1_8.LegacyConstants_1_8;
import de.florianmichael.clampclient.injection.mixininterface.IEntity_Protocol;
import de.florianmichael.clampclient.injection.mixininterface.ILivingEntity_Protocol;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.vialoadingbase.util.VersionListEnum;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import net.minecraft.block.Block;
import net.minecraft.block.SoulSandBlock;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.tarasandedevelopment.tarasande_protocol_hack.util.values.ProtocolHackValues;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("ConstantValue")
@Mixin(Entity.class)
public abstract class MixinEntity implements IEntity_Protocol {

    @Shadow
    public World world;
    @Shadow
    protected Object2DoubleMap<TagKey<Fluid>> fluidHeight;
    @Shadow
    private Vec3d pos;

    @ModifyConstant(method = "movementInputToVelocity", constant = @Constant(doubleValue = 1E-7))
    private static double injectMovementInputToVelocity(double epsilon) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(VersionListEnum.r1_13_2)) {
            return 1E-4;
        }

        return epsilon;
    }

    @Shadow
    public abstract Box getBoundingBox();

    @Shadow
    public abstract Vec3d getVelocity();

    @Shadow
    public abstract void setVelocity(Vec3d velocity);

    @Shadow public abstract void setPos(double x, double y, double z);

    @Shadow public abstract void setBoundingBox(Box boundingBox);

    @Inject(method = "getVelocityAffectingPos", at = @At("HEAD"), cancellable = true)
    public void injectGetVelocityAffectingPos(CallbackInfoReturnable<BlockPos> cir) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(VersionListEnum.r1_14_4))
            cir.setReturnValue(new BlockPos(pos.x, getBoundingBox().minY - 1, pos.z));
    }

    @Inject(method = "setSwimming", at = @At("HEAD"), cancellable = true)
    private void onSetSwimming(boolean swimming, CallbackInfo ci) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(VersionListEnum.r1_12_2) && swimming)
            ci.cancel();
    }

    // I-EEE 754
    @Inject(method = "setPosition(DDD)V", at = @At("HEAD"), cancellable = true)
    public void fixRoundingConvention(double x, double y, double z, CallbackInfo ci) {
        if (!ProtocolHackValues.INSTANCE.getLegacyTest().getValue()) return;

        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(VersionListEnum.r1_8) && (Object) this instanceof ClientPlayerEntity) {
            this.setPos(x, y, z);
            this.setBoundingBox(new Box(
                    x - (double) LegacyConstants_1_8.PLAYER_MODEL_WIDTH / 2.0F,
                    y,
                    z - (double) LegacyConstants_1_8.PLAYER_MODEL_WIDTH / 2.0F,
                    x + (double) LegacyConstants_1_8.PLAYER_MODEL_WIDTH / 2.0F,
                    y + (double) LegacyConstants_1_8.PLAYER_MODEL_HEIGHT,
                    z + (double) LegacyConstants_1_8.PLAYER_MODEL_WIDTH / 2.0F
            ));
            ci.cancel();
        }
    }

    @SuppressWarnings("deprecation")
    @Inject(method = "updateMovementInFluid", at = @At("HEAD"), cancellable = true)
    private void modifyFluidMovementBoundingBox(TagKey<Fluid> fluidTag, double d, CallbackInfoReturnable<Boolean> ci) {
        if (ProtocolHackValues.INSTANCE.getLegacyTest().getValue()) {
            ci.setReturnValue(false);
            return;
        }

        if (ViaLoadingBase.getTargetVersion().isNewerThan(VersionListEnum.r1_12_2)) {
            return;
        }

        Box box = getBoundingBox().expand(0, -0.4, 0).contract(0.001);
        int minX = MathHelper.floor(box.minX);
        int maxX = MathHelper.ceil(box.maxX);
        int minY = MathHelper.floor(box.minY);
        int maxY = MathHelper.ceil(box.maxY);
        int minZ = MathHelper.floor(box.minZ);
        int maxZ = MathHelper.ceil(box.maxZ);

        if (!world.isRegionLoaded(minX, minY, minZ, maxX, maxY, maxZ))
            ci.setReturnValue(false);

        double waterHeight = 0;
        boolean foundFluid = false;
        Vec3d pushVec = Vec3d.ZERO;

        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for (int x = minX; x < maxX; x++) {
            for (int y = minY - 1; y < maxY; y++) {
                for (int z = minZ; z < maxZ; z++) {
                    mutable.set(x, y, z);
                    FluidState state = world.getFluidState(mutable);
                    if (state.isIn(fluidTag)) {
                        double height = y + state.getHeight(world, mutable);
                        if (height >= box.minY - 0.4)
                            waterHeight = Math.max(height - box.minY + 0.4, waterHeight);
                        if (y >= minY && maxY >= height) {
                            foundFluid = true;
                            pushVec = pushVec.add(state.getVelocity(world, mutable));
                        }
                    }
                }
            }
        }

        if (pushVec.length() > 0) {
            pushVec = pushVec.normalize().multiply(0.014);
            setVelocity(getVelocity().add(pushVec));
        }

        this.fluidHeight.put(fluidTag, waterHeight);
        ci.setReturnValue(foundFluid);
    }

    @Inject(method = "getTargetingMargin", at = @At("HEAD"), cancellable = true)
    public void expandHitBox(CallbackInfoReturnable<Float> cir) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(VersionListEnum.r1_8))
            cir.setReturnValue(0.1F);
    }

    @Redirect(method = {"setYaw", "setPitch"}, at = @At(value = "INVOKE", target = "Ljava/lang/Float;isFinite(F)Z"))
    public boolean modifyIsFinite(float f) {
        //noinspection ConstantConditions
        return Float.isFinite(f) || ((Object) this instanceof ClientPlayerEntity && ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(VersionListEnum.r1_12_2));
    }

    @ModifyConstant(method = "checkBlockCollision", constant = @Constant(doubleValue = 1.0E-7))
    public double changeBlockCollisionConstant(double constant) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(VersionListEnum.r1_19_1tor1_19_2)) {
            return 0.001;
        }
        return constant;
    }

    // Not relevant for GamePlay
    @Redirect(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;onLanding()V"))
    public void revertOnLanding(Entity instance) {
        if (ViaLoadingBase.getTargetVersion().isNewerThanOrEqualTo(VersionListEnum.r1_19)) {
            instance.onLanding();
        }
    }

    @Inject(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;updateWaterState()Z", shift = At.Shift.BEFORE))
    public void tickLegacyWaterMovement(CallbackInfo ci) {
        if (!ProtocolHackValues.INSTANCE.getLegacyTest().getValue()) return;

        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(VersionListEnum.r1_8) && (Object) this instanceof ClientPlayerEntity) {
            ((ILivingEntity_Protocol) this).protocolhack_getPlayerLivingEntityMovementWrapper().handleWaterMovement();
        }
    }

    @Unique
    private boolean protocolhack_outsideBorder = false;

    @Override
    public boolean protocolhack_isOutsideBorder() {
        return protocolhack_outsideBorder;
    }

    @Override
    public void protocolhack_setOutsideBorder(boolean outsideBorder) {
        this.protocolhack_outsideBorder = outsideBorder;
    }

    @Unique
    private boolean protocolhack_inWeb = false;

    @Override
    public boolean protocolhack_isInWeb() {
        return protocolhack_inWeb;
    }

    @Override
    public void protocolhack_setInWeb(boolean inWeb) {
        this.protocolhack_inWeb = inWeb;
    }

    @Unique
    private boolean protocolhack_inWater = false;

    @Override
    public boolean protocolhack_isInWater() {
        return protocolhack_inWater;
    }

    @Override
    public void protocolhack_setInWater(boolean inWater) {
        this.protocolhack_inWater = inWater;
    }
}
