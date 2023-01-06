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

import de.florianmichael.clampclient.injection.instrumentation_1_8.PlayerAndLivingEntityMovementEmulation_1_8;
import de.florianmichael.clampclient.injection.instrumentation_1_8.MathHelper_1_8;
import de.florianmichael.clampclient.injection.mixininterface.ILivingEntity_Protocol;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.vialoadingbase.util.VersionListEnum;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.SkeletonHorseEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.TagKey;
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
@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity implements ILivingEntity_Protocol {

    @Shadow
    protected boolean jumping;

    public MixinLivingEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "getPreferredEquipmentSlot", at = @At("HEAD"), cancellable = true)
    private static void removeShieldSlotPreference(ItemStack stack, CallbackInfoReturnable<EquipmentSlot> cir) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(VersionListEnum.r1_9_3tor1_9_4) && stack.isOf(Items.SHIELD)) {
            cir.setReturnValue(EquipmentSlot.MAINHAND);
        }
    }

    @Shadow
    protected abstract float getBaseMovementSpeedMultiplier();

    @Shadow public float sidewaysSpeed;

    @Shadow public float forwardSpeed;

    @Shadow protected abstract void swimUpward(TagKey<Fluid> fluid);

    @Shadow protected abstract boolean shouldSwimInFluids();

    @Shadow public int jumpingCooldown;

    @Redirect(method = "applyMovementInput", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;jumping:Z"))
    private boolean disableJumpOnLadder(LivingEntity self) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(VersionListEnum.r1_13_2)) {
            return false;
        }

        return jumping;
    }

    @Redirect(method = "travel",
            slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/entity/effect/StatusEffects;DOLPHINS_GRACE:Lnet/minecraft/entity/effect/StatusEffect;")),
            at = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;horizontalCollision:Z", ordinal = 0))
    private boolean disableClimbing(LivingEntity self) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(VersionListEnum.r1_13_2)) {
            return false;
        }

        return horizontalCollision;
    }

    @ModifyVariable(method = "applyFluidMovingSpeed", ordinal = 0, at = @At("HEAD"), argsOnly = true)
    private boolean modifyMovingDown(boolean movingDown) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(VersionListEnum.r1_13_2)) {
            return true;
        }

        return movingDown;
    }

    @Redirect(method = "travel",
            slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/entity/effect/StatusEffects;LEVITATION:Lnet/minecraft/entity/effect/StatusEffect;", ordinal = 0)),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;onLanding()V", ordinal = 0))
    private void dontResetLevitationFallDistance(LivingEntity instance) {
        if (ViaLoadingBase.getTargetVersion().isNewerThan(VersionListEnum.r1_12_2)) {
            instance.onLanding();
        }
    }

    @Redirect(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isSprinting()Z", ordinal = 0))
    private boolean modifySwimSprintSpeed(LivingEntity self) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(VersionListEnum.r1_12_2)) {
            return false;
        }
        return self.isSprinting();
    }

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getFluidHeight(Lnet/minecraft/registry/tag/TagKey;)D"))
    private double redirectFluidHeight(LivingEntity instance, TagKey<Fluid> tagKey) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(VersionListEnum.r1_12_2) && tagKey == FluidTags.WATER) {
            if (instance.getFluidHeight(tagKey) > 0) {
                return 1;
            }
        }
        return instance.getFluidHeight(tagKey);
    }

    @Inject(method = "applyFluidMovingSpeed", at = @At("HEAD"), cancellable = true)
    private void modifySwimSprintFallSpeed(double gravity, boolean movingDown, Vec3d velocity, CallbackInfoReturnable<Vec3d> ci) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(VersionListEnum.r1_12_2) && !hasNoGravity()) {
            ci.setReturnValue(new Vec3d(velocity.x, velocity.y - 0.02, velocity.z));
        }
    }

    @ModifyConstant(method = "tickMovement", constant = @Constant(doubleValue = 0.003D))
    public double modifyVelocityZero(final double constant) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(VersionListEnum.r1_8)) {
            return 0.005D;
        }
        return constant;
    }

    @Inject(method = "canEnterTrapdoor", at = @At("HEAD"), cancellable = true)
    private void onCanEnterTrapdoor(CallbackInfoReturnable<Boolean> ci) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(VersionListEnum.r1_8)) {
            ci.setReturnValue(false);
        }
    }

    @ModifyConstant(method = "travel", constant = @Constant(floatValue = 0.9F))
    private float changeEntitySpeed(float constant) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(VersionListEnum.r1_12_2)) {
            //noinspection ConstantConditions
            if ((Entity) this instanceof SkeletonHorseEntity) {
                return this.getBaseMovementSpeedMultiplier(); // 0.96F
            }
            return 0.8F;
        }
        return constant;
    }

    @Redirect(method = "travel", at = @At(value = "INVOKE", target = "Ljava/lang/Math;cos(D)D"))
    public double fixCosTable(double a) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(VersionListEnum.r1_18_2)) {
            return MathHelper.cos((float) a);
        }
        return Math.cos(a);
    }

    @Redirect(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getFluidHeight(Lnet/minecraft/registry/tag/TagKey;)D"))
    public double fixLavaMovement(LivingEntity instance, TagKey<Fluid> tagKey) {
        double height = instance.getFluidHeight(tagKey);

        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(VersionListEnum.r1_15_2)) {
            height += getSwimHeight() + 4;
        }
        return height;
    }

    @ModifyConstant(method = "isBlocking", constant = @Constant(intValue = 5))
    public int shieldBlockCounter(int constant) {
        if(ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(VersionListEnum.r1_8)) {
            return 0;
        }
        return constant;
    }

    @Redirect(method = "tickCramming", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isClient()Z"))
    public boolean revertOnlyPlayerCramming(World instance) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(VersionListEnum.r1_19_1tor1_19_2)) {
            return false;
        }
        return instance.isClient();
    }


    @Unique
    private final PlayerAndLivingEntityMovementEmulation_1_8 a18PlayerAndLivingEntityMovementEmulation = new PlayerAndLivingEntityMovementEmulation_1_8((LivingEntity)(Object) this);

    @Redirect(method = "jump", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isSprinting()Z"))
    public boolean fixMathHelperTable(LivingEntity instance) {
        if (!ProtocolHackValues.INSTANCE.getLegacyTest().getValue()) return instance.isSprinting();

        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(VersionListEnum.r1_8)) {
            if (instance.isSprinting()) {
                float f = this.getYaw() * 0.017453292F;
                // this casts are very important, please: don't delete them
                this.getVelocity().x -= (double)(MathHelper_1_8.sin(f) * 0.2F);
                this.getVelocity().z += (double)(MathHelper_1_8.cos(f) * 0.2F);
            }
            return false;
        }
        return instance.isSprinting();
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isSleeping()Z", ordinal = 1))
    public boolean removeNewCheck(LivingEntity instance) {
        if (!ProtocolHackValues.INSTANCE.getLegacyTest().getValue()) return instance.isSleeping();

        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(VersionListEnum.r1_8)) {
            return false;
        }
        return instance.isSleeping();
    }

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;travel(Lnet/minecraft/util/math/Vec3d;)V"))
    public void replaceMovementCode(LivingEntity instance, Vec3d movementInput) {
        if (!ProtocolHackValues.INSTANCE.getLegacyTest().getValue()) {
            instance.travel(movementInput);
            return;
        }

        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(VersionListEnum.r1_8) && instance instanceof ClientPlayerEntity) {
            a18PlayerAndLivingEntityMovementEmulation.movePlayerWithHeading(this.sidewaysSpeed, this.forwardSpeed);
        } else {
            instance.travel(movementInput);
        }
    }

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;shouldSwimInFluids()Z"))
    public boolean removeNewSwimHandling(LivingEntity instance) {
        if (!ProtocolHackValues.INSTANCE.getLegacyTest().getValue()) return shouldSwimInFluids();
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(VersionListEnum.r1_8) && (Object) this instanceof ClientPlayerEntity) {
            return false;
        }
        return shouldSwimInFluids();
    }

    @Unique
    private int protocolhack_previousJumpingCooldown;

    @Inject(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V", ordinal = 2, shift = At.Shift.AFTER))
    public void doShit(CallbackInfo ci) {
        if (!ProtocolHackValues.INSTANCE.getLegacyTest().getValue()) return;
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(VersionListEnum.r1_8) && (Object) this instanceof ClientPlayerEntity) {
            protocolhack_previousJumpingCooldown = this.jumpingCooldown;
        }
    }

    @Inject(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;tickFallFlying()V", shift = At.Shift.BEFORE))
    public void doShit2(CallbackInfo ci) {
        if (!ProtocolHackValues.INSTANCE.getLegacyTest().getValue()) return;
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(VersionListEnum.r1_8) && (Object) this instanceof ClientPlayerEntity) {
            this.jumpingCooldown = protocolhack_previousJumpingCooldown;
            protocolhack_getPlayerLivingEntityMovementWrapper().customJump();
        }
    }

    @Override
    public PlayerAndLivingEntityMovementEmulation_1_8 protocolhack_getPlayerLivingEntityMovementWrapper() {
        return a18PlayerAndLivingEntityMovementEmulation;
    }
}
