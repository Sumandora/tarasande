package net.tarasandedevelopment.tarasande.injection.mixin.feature.module.entity;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.injection.accessor.IClientPlayerEntity;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.exploit.ModulePortalScreen;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.movement.ModuleFlight;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.movement.ModuleNoSlowdown;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.player.ModuleNoFall;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.player.ModuleNoStatusEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntity extends AbstractClientPlayerEntity implements IClientPlayerEntity {

    @Unique
    private boolean tarasande_flight;

    @Unique
    private float tarasande_flightSpeed;

    @Unique
    private boolean tarasande_forceHasStatusEffect;

    @Unique
    private boolean tarasande_forceGetStatusEffect;

    public MixinClientPlayerEntity(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Shadow
    public abstract float getYaw(float tickDelta);

    @Shadow
    public abstract float getPitch(float tickDelta);

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;canSprint()Z"), to = @At(value = "INVOKE", target = "Lnet/minecraft/client/input/Input;hasForwardMovement()Z")))
    public boolean hookNoSlowdown(ClientPlayerEntity clientPlayerEntity) {
        ModuleNoSlowdown moduleNoSlowdown = TarasandeMain.Companion.managerModule().get(ModuleNoSlowdown.class);
        if (moduleNoSlowdown.getEnabled()) {
            if (moduleNoSlowdown.isActionEnabled(moduleNoSlowdown.getActions()))
                return false;
        }
        return clientPlayerEntity.isUsingItem();
    }

    @ModifyConstant(method = "tickMovement", constant = @Constant(floatValue = 0.2F))
    public float hookNoSlowdown(float original) {
        ModuleNoSlowdown moduleNoSlowdown = TarasandeMain.Companion.managerModule().get(ModuleNoSlowdown.class);
        if (moduleNoSlowdown.getEnabled()) {
            if (moduleNoSlowdown.isActionEnabled(moduleNoSlowdown.getActions()))
                return (float) moduleNoSlowdown.getSlowdown().getValue();
        }
        return original;
    }

    @Redirect(method = "tickMovement", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerAbilities;flying:Z"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;knockDownwards()V"), to = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getJumpingMount()Lnet/minecraft/entity/JumpingMount;")))
    public boolean hookFlight(PlayerAbilities instance) {
        tarasande_flight = false;
        tarasande_flightSpeed = 0.05F;
        ModuleFlight moduleFlight = TarasandeMain.Companion.managerModule().get(ModuleFlight.class);
        if (moduleFlight.getEnabled() && moduleFlight.getMode().isSelected(0)) {
            tarasande_flight = true;
            // TODO Read that constant
            tarasande_flightSpeed = 0.05F * (float) moduleFlight.getFlightSpeed().getValue();
            return true;
        }
        return instance.flying;
    }

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerAbilities;getFlySpeed()F"))
    public float hookFlightSpeed(PlayerAbilities instance) {
        return tarasande_flight ? tarasande_flightSpeed : instance.getFlySpeed();
    }

    @Override
    public void travel(Vec3d movementInput) {
        boolean fallFlying = this.getFlag(Entity.FALL_FLYING_FLAG_INDEX);

        boolean flying = getAbilities().flying;
        float flySpeed = getAbilities().getFlySpeed();

        if (tarasande_flight) {
            getAbilities().flying = tarasande_flight;
            getAbilities().setFlySpeed(tarasande_flightSpeed);
        }

        super.travel(movementInput);

        getAbilities().flying = flying;
        getAbilities().setFlySpeed(flySpeed);

        if (tarasande_flight)
            this.setFlag(Entity.FALL_FLYING_FLAG_INDEX, fallFlying);
    }

    @Redirect(method = "updateNausea", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;shouldPause()Z"))
    public boolean hookPortalScreen(Screen instance) {
        if (TarasandeMain.Companion.managerModule().get(ModulePortalScreen.class).getEnabled())
            return true;
        return instance.shouldPause();
    }

    @Override
    public boolean hasStatusEffect(StatusEffect effect) {
        final boolean originalValue = super.hasStatusEffect(effect);
        if (tarasande_forceHasStatusEffect) {
            tarasande_forceHasStatusEffect = false;
        } else {
            final ModuleNoStatusEffect moduleNoStatusEffect = TarasandeMain.Companion.managerModule().get(ModuleNoStatusEffect.class);
            if (moduleNoStatusEffect.getEnabled() && moduleNoStatusEffect.getEffects().getList().contains(effect)) {
                return false;
            }
        }
        return originalValue;
    }

    @Override
    public StatusEffectInstance getStatusEffect(StatusEffect effect) {
        final StatusEffectInstance originalValue = super.getStatusEffect(effect);
        if (tarasande_forceGetStatusEffect) {
            tarasande_forceGetStatusEffect = false;
        } else {
            final ModuleNoStatusEffect moduleNoStatusEffect = TarasandeMain.Companion.managerModule().get(ModuleNoStatusEffect.class);
            if (moduleNoStatusEffect.getEnabled() && moduleNoStatusEffect.getEffects().getList().contains(effect)) {
                return null;
            }
        }
        return originalValue;
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

    @Override
    protected void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {
        if(onGround) {
            final ModuleNoFall moduleNoFall = TarasandeMain.Companion.managerModule().get(ModuleNoFall.class);
            if(moduleNoFall.getEnabled() && moduleNoFall.getMode().isSelected(0) && moduleNoFall.getGroundSpoofMode().isSelected(1))
                return;
        }
        super.fall(heightDifference, onGround, state, landedPosition);
    }
}
