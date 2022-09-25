package de.enzaxd.viaforge.injection.mixin.entity;

import de.enzaxd.viaforge.equals.ProtocolEquals;
import de.enzaxd.viaforge.equals.VersionList;
import de.enzaxd.viaforge.injection.access.IClientPlayerEntity_Protocol;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Shadow protected boolean jumping;

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Redirect(method = "applyMovementInput", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;jumping:Z"))
    private boolean disableJumpOnLadder(LivingEntity self) {
        if (ProtocolEquals.isOlderOrEqualTo(VersionList.R1_13_2))
            return false;

        return jumping;
    }

    @Redirect(method = "travel",
            slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/entity/effect/StatusEffects;DOLPHINS_GRACE:Lnet/minecraft/entity/effect/StatusEffect;")),
            at = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;horizontalCollision:Z", ordinal = 0))
    private boolean disableClimbing(LivingEntity self) {
        if (ProtocolEquals.isOlderOrEqualTo(VersionList.R1_13_2))
            return false;
        return horizontalCollision;
    }

    // TODO: when @At(reverse = true) is added to mixin:
    //@Redirect(method = "travel", slice = @Slice(to = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getBaseMovementSpeedMultiplier()F")), at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isSprinting()Z", ordinal = 0, reverse = true))
    @Redirect(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isSprinting()Z", ordinal = 0))
    private boolean modifySwimSprintSpeed(LivingEntity self) {
        if (ProtocolEquals.isOlderOrEqualTo(VersionList.R1_12_2))
            return false;
        return self.isSprinting();
    }

    @Inject(method = "getPreferredEquipmentSlot", at = @At("HEAD"), cancellable = true)
    private static void removeShieldSlotPreference(ItemStack stack, CallbackInfoReturnable<EquipmentSlot> cir) {
        if (ProtocolEquals.isOlderOrEqualTo(VersionList.R1_9_4) && stack.isOf(Items.SHIELD))
            cir.setReturnValue(EquipmentSlot.MAINHAND);
    }

    @Inject(method = "tickHandSwing", at = @At("HEAD"))
    private void onTickHandSwing(CallbackInfo ci) {
        if (ProtocolEquals.isOlderOrEqualTo(VersionList.R1_8) && this instanceof IClientPlayerEntity_Protocol)
            ((IClientPlayerEntity_Protocol) this).florianMichael_unCancelSwings();
    }

    @ModifyConstant(method = "tickMovement", constant = @Constant(doubleValue = 0.003D))
    public double modifyVelocityZero(final double constant) {
        if (ProtocolEquals.isOlderOrEqualTo(VersionList.R1_8)) return 0.005D;
        return constant;
    }

    @Inject(method = "canEnterTrapdoor", at = @At("HEAD"), cancellable = true)
    private void onCanEnterTrapdoor(CallbackInfoReturnable<Boolean> ci) {
        if (ProtocolEquals.isOlderOrEqualTo(VersionList.R1_8))
            ci.setReturnValue(false);
    }
}
