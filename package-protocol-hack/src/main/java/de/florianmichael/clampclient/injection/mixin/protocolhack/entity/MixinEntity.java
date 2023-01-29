/**
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.2--
 * <p>
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license. The creator assumes no responsibility for any infringements
 * that have arisen, are arising or will arise from this project / file. If this licence is used anywhere,
 * the latest version published by the author Florian Michael (aka EnZaXD) always applies automatically.
 * <p>
 * Changelog:
 * v1.0:
 * Added License
 * v1.1:
 * Ownership withdrawn
 * v1.2:
 * Version-independent validity and automatic renewal
 */

package de.florianmichael.clampclient.injection.mixin.protocolhack.entity;

import de.florianmichael.clampclient.injection.instrumentation_1_12_2.Raytrace_1_8to1_12_2;
import de.florianmichael.clampclient.injection.instrumentation_1_8.definition.LegacyConstants_1_8;
import de.florianmichael.clampclient.injection.mixininterface.IEntity_Protocol;
import de.florianmichael.clampclient.injection.mixininterface.ILivingEntity_Protocol;
import de.florianmichael.tarasande_protocol_hack.definition.entitydimension.base.EntityDimension;
import de.florianmichael.tarasande_protocol_hack.definition.entitydimension.EntityDimensionsDefinition;
import de.florianmichael.tarasande_protocol_hack.definition.entitydimension.wrapper.EntityDimensionWrapper;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import kotlin.jvm.functions.Function1;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import de.florianmichael.tarasande_protocol_hack.util.values.ProtocolHackValues;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ManagerModule;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.exploit.ModuleNoPitchLimit;
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

    @Shadow
    public abstract Box getBoundingBox();

    @Shadow
    public abstract Vec3d getVelocity();

    @Shadow
    public abstract void setVelocity(Vec3d velocity);

    @Shadow
    public abstract void setPos(double x, double y, double z);

    @Shadow
    public abstract void setBoundingBox(Box boundingBox);

    @Shadow
    private float pitch;

    @Shadow
    private float yaw;

    @Shadow
    public float prevPitch;

    @Shadow
    public float prevYaw;

    @Shadow
    public abstract EntityPose getPose();

    @Unique
    private EntityDimension<?> protocolhack_replacedDimension;

    @Unique
    private Function1<Entity, Float> protocolhack_replacedEyeHeight;

    @ModifyConstant(method = "movementInputToVelocity", constant = @Constant(doubleValue = 1E-7))
    private static double injectMovementInputToVelocity(double epsilon) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_13_2)) {
            return 1E-4;
        }
        return epsilon;
    }

    @Inject(method = "setPosition(DDD)V", at = @At("HEAD"))
    public void onPosition(double x, double y, double z, CallbackInfo ci) {
        if (ProtocolHackValues.INSTANCE.getEntityDimensionReplacements().getValue()) {
            final EntityDimensionWrapper entityDimensionWrapper = EntityDimensionsDefinition.INSTANCE.getWrapper();

            if (protocolhack_replacedDimension == null) protocolhack_replacedDimension = entityDimensionWrapper.getDimension((Entity) (Object) this);
            if (protocolhack_replacedEyeHeight == null) protocolhack_replacedEyeHeight = entityDimensionWrapper.getEyeHeight((Entity) (Object) this);
        }
    }

    @Inject(method = "calculateBoundingBox", at = @At("RETURN"), cancellable = true)
    public void onCalculateBoundingBox(CallbackInfoReturnable<Box> cir) {
        if (protocolhack_replacedDimension != null) cir.setReturnValue(protocolhack_replacedDimension.getBoxAt((Entity) (Object) this, this.getPose(), this.pos));
    }

    @Inject(method = "getHeight", at = @At("RETURN"), cancellable = true)
    public void onGetHeight(CallbackInfoReturnable<Float> cir) {
        if (protocolhack_replacedDimension != null) cir.setReturnValue(protocolhack_replacedDimension.getHeight((Entity) (Object) this, getPose()));
    }

    @Inject(method = "getWidth", at = @At("RETURN"), cancellable = true)
    public void onGetWidth(CallbackInfoReturnable<Float> cir) {
        if (protocolhack_replacedDimension != null) cir.setReturnValue(protocolhack_replacedDimension.getWidth((Entity) (Object) this, getPose()));
    }

    @Inject(method = "getStandingEyeHeight", at = @At("HEAD"), cancellable = true)
    public void onGetStandingEyeHeight(CallbackInfoReturnable<Float> cir) {
        if (protocolhack_replacedEyeHeight != null) cir.setReturnValue(protocolhack_replacedEyeHeight.invoke((Entity) (Object) this));
    }

    @Inject(method = "getCameraPosVec", at = @At("HEAD"), cancellable = true)
    public void onGetCameraPosVec(float tickDelta, CallbackInfoReturnable<Vec3d> cir) {
        if (!ProtocolHackValues.INSTANCE.getReplaceRayTrace().getValue()) return;

        cir.setReturnValue(Raytrace_1_8to1_12_2.CLASS_WRAPPER.getPositionEyes((Entity) (Object) this, tickDelta));
    }

    @Inject(method = "getVelocityAffectingPos", at = @At("HEAD"), cancellable = true)
    public void injectGetVelocityAffectingPos(CallbackInfoReturnable<BlockPos> cir) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_14_4)) {
            cir.setReturnValue(new BlockPos(pos.x, getBoundingBox().minY - 1, pos.z));
        }
    }

    @Inject(method = "getRotationVector(FF)Lnet/minecraft/util/math/Vec3d;", at = @At("HEAD"), cancellable = true)
    public void onGetRotationVector(float pitch, float yaw, CallbackInfoReturnable<Vec3d> cir) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_12_2)) {
            cir.setReturnValue(Vec3d.fromPolar(pitch, yaw));
        }
    }

    @Inject(method = "setSwimming", at = @At("HEAD"), cancellable = true)
    private void onSetSwimming(boolean swimming, CallbackInfo ci) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_12_2) && swimming) {
            ci.cancel();
        }
    }

    // I-EEE 754
    @Inject(method = "setPosition(DDD)V", at = @At("HEAD"), cancellable = true)
    public void fixRoundingConvention(double x, double y, double z, CallbackInfo ci) {
        if (ProtocolHackValues.INSTANCE.getEmulatePlayerMovement().getValue() && (Object) this == MinecraftClient.getInstance().player) {
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
        if (ProtocolHackValues.INSTANCE.getEmulatePlayerMovement().getValue()) {
            ci.setReturnValue(false);
            return;
        }

        if (ViaLoadingBase.getTargetVersion().isNewerThan(ProtocolVersion.v1_12_2)) {
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
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_8)) {
            cir.setReturnValue(0.1F);
        }
    }

    @Redirect(method = {"setYaw", "setPitch"}, at = @At(value = "INVOKE", target = "Ljava/lang/Float;isFinite(F)Z"))
    public boolean modifyIsFinite(float f) {
        return Float.isFinite(f) || ((Object) this instanceof ClientPlayerEntity && ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_12_2));
    }

    @ModifyConstant(method = "checkBlockCollision", constant = @Constant(doubleValue = 1.0E-7))
    public double changeBlockCollisionConstant(double constant) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_19_1)) {
            return 0.001;
        }
        return constant;
    }

    // Not relevant for GamePlay
    @Redirect(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;onLanding()V"))
    public void revertOnLanding(Entity instance) {
        if (ViaLoadingBase.getTargetVersion().isNewerThanOrEqualTo(ProtocolVersion.v1_19)) {
            instance.onLanding();
        }
    }

    @Inject(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;updateWaterState()Z", shift = At.Shift.BEFORE))
    public void tickLegacyWaterMovement(CallbackInfo ci) {
        if (ProtocolHackValues.INSTANCE.getEmulatePlayerMovement().getValue() && (Object) this == MinecraftClient.getInstance().player) {
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

    @Override
    public void protocolhack_setAngles(float yaw, float pitch) {
        float f = this.pitch;
        float f1 = this.yaw;
        this.yaw = (float) ((double) this.yaw + (double) yaw * 0.15D);
        this.pitch = (float) ((double) this.pitch - (double) pitch * 0.15D);

        if (!ManagerModule.INSTANCE.get(ModuleNoPitchLimit.class).getEnabled().getValue()) {
            this.pitch = MathHelper.clamp(this.pitch, -90.0F, 90.0F);
        }

        this.prevPitch += this.pitch - f;
        this.prevYaw += this.yaw - f1;
    }


    @Unique
    private Vec3i serverPos = new Vec3i(0, 0, 0);

    @Override
    public Vec3i protocolhack_getServerPos() {
        return serverPos;
    }
}
