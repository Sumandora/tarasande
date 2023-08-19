package su.mandora.tarasande.injection.mixin.feature.module.entity;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.mandora.tarasande.injection.accessor.ILivingEntity;
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule;
import su.mandora.tarasande.system.feature.modulesystem.impl.movement.ModuleAirJump;
import su.mandora.tarasande.system.feature.modulesystem.impl.movement.ModuleFastClimb;
import su.mandora.tarasande.system.feature.modulesystem.impl.movement.ModuleStep;
import su.mandora.tarasande.system.feature.modulesystem.impl.movement.ModuleWaterSpeed;
import su.mandora.tarasande.system.feature.modulesystem.impl.player.ModuleNoEffect;
import su.mandora.tarasande.system.feature.modulesystem.impl.player.ModuleNoFall;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity implements ILivingEntity {

    @Unique
    private boolean tarasande_forceHasStatusEffect;
    @Unique
    private boolean tarasande_forceGetStatusEffect;

    public MixinLivingEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow
    public abstract float getYaw(float tickDelta);

    @Shadow
    public abstract boolean isClimbing();

    @Shadow
    public abstract boolean hasStatusEffect(StatusEffect effect);

    @Shadow
    @Nullable
    public abstract StatusEffectInstance getStatusEffect(StatusEffect effect);

    @ModifyConstant(method = "applyMovementInput", constant = @Constant(doubleValue = 0.2))
    public double hookFastClimb_ascend(double original) {
        if ((Object) this == MinecraftClient.getInstance().player)
            if (isClimbing()) {
                ModuleFastClimb moduleFastClimb = ManagerModule.INSTANCE.get(ModuleFastClimb.class);
                if (moduleFastClimb.getEnabled().getValue())
                    original *= moduleFastClimb.getAscendMultiplier().getValue();
            }
        return original;
    }

    @Redirect(method = "applyClimbingSpeed", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;clamp(DDD)D"))
    public double hookFastClimb(double value, double min, double max) {
        if ((Object) this == MinecraftClient.getInstance().player)
            if (isClimbing()) {
                ModuleFastClimb moduleFastClimb = ManagerModule.INSTANCE.get(ModuleFastClimb.class);
                if (moduleFastClimb.getEnabled().getValue()) {
                    min *= moduleFastClimb.getMaxHorizontalVelocityMultiplier().getValue();
                    max *= moduleFastClimb.getMaxHorizontalVelocityMultiplier().getValue();
                }
            }
        return MathHelper.clamp(value, min, max);
    }

    @ModifyConstant(method = "applyClimbingSpeed", constant = @Constant(doubleValue = (double)-0.15f, ordinal = 2))
    public double hookFastClimb_descend(double original) {
        if ((Object) this == MinecraftClient.getInstance().player)
            if (isClimbing()) {
                ModuleFastClimb moduleFastClimb = ManagerModule.INSTANCE.get(ModuleFastClimb.class);
                if (moduleFastClimb.getEnabled().getValue())
                    original *= moduleFastClimb.getDescendMultiplier().getValue();
            }
        return original;
    }

    @Inject(method = "fall", at = @At("HEAD"), cancellable = true)
    public void hookNoFall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition, CallbackInfo ci) {
        if ((Object) this == MinecraftClient.getInstance().player)
            if (onGround) {
                ModuleNoFall moduleNoFall = ManagerModule.INSTANCE.get(ModuleNoFall.class);
                if (moduleNoFall.getEnabled().getValue() && moduleNoFall.getMode().isSelected(0) && moduleNoFall.getGroundSpoofMode().isSelected(1))
                    ci.cancel();
            }
    }

    @Inject(method = "hasStatusEffect", at = @At("RETURN"), cancellable = true)
    public void hookNoStatusEffect_hasStatusEffect(StatusEffect effect, CallbackInfoReturnable<Boolean> cir) {
        if (tarasande_forceHasStatusEffect) {
            tarasande_forceHasStatusEffect = false;
        } else {
            if ((Object) this == MinecraftClient.getInstance().player) {
                ModuleNoEffect moduleNoEffect = ManagerModule.INSTANCE.get(ModuleNoEffect.class);
                if (moduleNoEffect.getEnabled().getValue() && moduleNoEffect.getEffects().isSelected(effect)) {
                    cir.setReturnValue(false);
                }
            }
        }
    }

    @Inject(method = "getStatusEffect", at = @At("RETURN"), cancellable = true)
    public void hookNoStatusEffect_getStatusEffect(StatusEffect effect, CallbackInfoReturnable<StatusEffectInstance> cir) {
        if (tarasande_forceGetStatusEffect) {
            tarasande_forceGetStatusEffect = false;
        } else {
            if ((Object) this == MinecraftClient.getInstance().player) {
                ModuleNoEffect moduleNoEffect = ManagerModule.INSTANCE.get(ModuleNoEffect.class);
                if (moduleNoEffect.getEnabled().getValue() && moduleNoEffect.getEffects().isSelected(effect)) {
                    cir.setReturnValue(null);
                }
            }
        }
    }

    @Redirect(method = "getJumpBoostVelocityModifier", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;hasStatusEffect(Lnet/minecraft/entity/effect/StatusEffect;)Z"))
    public boolean hookStep(LivingEntity instance, StatusEffect effect) {
        if ((Object) this == MinecraftClient.getInstance().player) {
            ModuleStep moduleStep = ManagerModule.INSTANCE.get(ModuleStep.class);
            if (moduleStep.getEnabled().getValue() && moduleStep.getIgnoreJumpBoost().getValue() && moduleStep.getInPrediction()) {
                return false;
            }
        }
        return instance.hasStatusEffect(effect);
    }

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE",target = "Lnet/minecraft/entity/LivingEntity;isOnGround()Z", ordinal = 0), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;swimUpward(Lnet/minecraft/registry/tag/TagKey;)V", ordinal = 1)))
    public boolean hookAirJump(LivingEntity instance) {
        if ((Object) this == MinecraftClient.getInstance().player) {
            ModuleAirJump moduleAirJump = ManagerModule.INSTANCE.get(ModuleAirJump.class);
            if (moduleAirJump.getEnabled().getValue())
                return true;
        }

        return instance.isOnGround();
    }

    @Override
    public boolean tarasande_forceHasStatusEffect(StatusEffect effect) {
        tarasande_forceHasStatusEffect = true;
        return hasStatusEffect(effect);
    }

    @Override
    public StatusEffectInstance tarasande_forceGetStatusEffect(StatusEffect effect) {
        tarasande_forceGetStatusEffect = true;
        return getStatusEffect(effect);
    }

    @ModifyArg(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;applyFluidMovingSpeed(DZLnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;"), index = 0)
    public double hookWaterSpeed(double gravity) {
        if ((Object) this == MinecraftClient.getInstance().player) {
            ModuleWaterSpeed moduleWaterSpeed = ManagerModule.INSTANCE.get(ModuleWaterSpeed.class);
            if (moduleWaterSpeed.getEnabled().getValue())
                gravity *= moduleWaterSpeed.getGravityMultiplier().getValue();
        }

        return gravity;
    }
}
