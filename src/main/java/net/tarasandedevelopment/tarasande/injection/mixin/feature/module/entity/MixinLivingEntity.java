package net.tarasandedevelopment.tarasande.injection.mixin.feature.module.entity;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.tarasandedevelopment.tarasande.injection.accessor.ILivingEntity;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ManagerModule;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.movement.ModuleFastClimb;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.player.ModuleNoFall;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.player.ModuleNoStatusEffect;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
    public double hookFastClimb(double original) {
        if ((Object) this == MinecraftClient.getInstance().player)
            if (isClimbing()) {
                ModuleFastClimb moduleFastClimb = ManagerModule.INSTANCE.get(ModuleFastClimb.class);
                if (moduleFastClimb.getEnabled())
                    original *= moduleFastClimb.getMultiplier().getValue();
            }
        return original;
    }

    @Inject(method = "fall", at = @At("HEAD"), cancellable = true)
    public void hookNoFall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition, CallbackInfo ci) {
        if ((Object) this == MinecraftClient.getInstance().player)
            if (onGround) {
                final ModuleNoFall moduleNoFall = ManagerModule.INSTANCE.get(ModuleNoFall.class);
                if (moduleNoFall.getEnabled() && moduleNoFall.getMode().isSelected(0) && moduleNoFall.getGroundSpoofMode().isSelected(1))
                    ci.cancel();
            }
    }

    @Inject(method = "hasStatusEffect", at = @At("RETURN"), cancellable = true)
    public void hookNoStatusEffect_hasStatusEffect(StatusEffect effect, CallbackInfoReturnable<Boolean> cir) {
        if (tarasande_forceHasStatusEffect) {
            tarasande_forceHasStatusEffect = false;
        } else {
            if ((Object) this == MinecraftClient.getInstance().player) {
                final ModuleNoStatusEffect moduleNoStatusEffect = ManagerModule.INSTANCE.get(ModuleNoStatusEffect.class);
                if (moduleNoStatusEffect.getEnabled() && moduleNoStatusEffect.getEffects().getList().contains(effect)) {
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
                final ModuleNoStatusEffect moduleNoStatusEffect = ManagerModule.INSTANCE.get(ModuleNoStatusEffect.class);
                if (moduleNoStatusEffect.getEnabled() && moduleNoStatusEffect.getEffects().getList().contains(effect)) {
                    cir.setReturnValue(null);
                }
            }
        }
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
}
