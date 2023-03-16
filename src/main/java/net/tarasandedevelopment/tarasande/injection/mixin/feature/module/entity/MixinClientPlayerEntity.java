package net.tarasandedevelopment.tarasande.injection.mixin.feature.module.entity;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.util.math.Vec3d;
import net.tarasandedevelopment.tarasande.system.base.grabbersystem.ManagerGrabber;
import net.tarasandedevelopment.tarasande.system.base.grabbersystem.impl.GrabberDefaultFlightSpeed;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ManagerModule;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.exploit.ModulePortalScreen;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.movement.ModuleFlight;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.movement.ModuleNoSlowdown;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.movement.ModuleSprint;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntity extends AbstractClientPlayerEntity {

    @Unique
    private boolean tarasande_flight = false;

    @Unique
    private float tarasande_flightSpeed;

    public MixinClientPlayerEntity(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Shadow
    public abstract float getYaw(float tickDelta);

    @Shadow
    public abstract float getPitch(float tickDelta);

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"))
    public boolean hookNoSlowdown(ClientPlayerEntity clientPlayerEntity) {
        if ((Object) this == MinecraftClient.getInstance().player) {
            ModuleNoSlowdown moduleNoSlowdown = ManagerModule.INSTANCE.get(ModuleNoSlowdown.class);
            if (moduleNoSlowdown.getEnabled().getValue()) {
                if (moduleNoSlowdown.isActionEnabled(moduleNoSlowdown.getActions()))
                    return false;
            }
        }
        return clientPlayerEntity.isUsingItem();
    }

    @ModifyConstant(method = "tickMovement", constant = @Constant(floatValue = 0.2F))
    public float hookNoSlowdown(float original) {
        if ((Object) this == MinecraftClient.getInstance().player) {
            ModuleNoSlowdown moduleNoSlowdown = ManagerModule.INSTANCE.get(ModuleNoSlowdown.class);
            if (moduleNoSlowdown.getEnabled().getValue()) {
                if (moduleNoSlowdown.isActionEnabled(moduleNoSlowdown.getActions()))
                    return (float) moduleNoSlowdown.getSlowdown().getValue();
            }
        }
        return original;
    }

    @Redirect(method = "tickMovement", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerAbilities;flying:Z"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;knockDownwards()V"), to = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getJumpingMount()Lnet/minecraft/entity/JumpingMount;")))
    public boolean hookFlight(PlayerAbilities instance) {
        tarasande_flight = false;
        if ((Object) this == MinecraftClient.getInstance().player) {
            tarasande_flightSpeed = (float) ManagerGrabber.INSTANCE.getConstant(GrabberDefaultFlightSpeed.class);
            ModuleFlight moduleFlight = ManagerModule.INSTANCE.get(ModuleFlight.class);
            if (moduleFlight.getEnabled().getValue() && moduleFlight.getMode().isSelected(0)) {
                tarasande_flight = true;
                tarasande_flightSpeed *= (float) moduleFlight.getFlightSpeed().getValue();
                return true;
            }
        }
        return instance.flying;
    }

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerAbilities;getFlySpeed()F"))
    public float hookFlightSpeed(PlayerAbilities instance) {
        return tarasande_flight ? tarasande_flightSpeed : instance.getFlySpeed();
    }

    @Override
    public void travel(Vec3d movementInput) {
        if ((Object) this == MinecraftClient.getInstance().player) {
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
        } else {
            super.travel(movementInput);
        }
    }

    @Redirect(method = "updateNausea", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;shouldPause()Z"))
    public boolean hookPortalScreen(Screen instance) {
        if ((Object) this == MinecraftClient.getInstance().player) {
            if (ManagerModule.INSTANCE.get(ModulePortalScreen.class).getEnabled().getValue())
                return true;
        }
        return instance.shouldPause();
    }

    @Redirect(method = "isWalking", at = @At(value = "FIELD", target = "Lnet/minecraft/client/input/Input;movementForward:F"))
    public float hookSprint(Input instance) {
        if ((Object) this == MinecraftClient.getInstance().player) {
            ModuleSprint moduleSprint = ManagerModule.INSTANCE.get(ModuleSprint.class);
            if (moduleSprint.getEnabled().getValue() && moduleSprint.getAllowBackwards().isEnabled().invoke() && moduleSprint.getAllowBackwards().getValue())
                return instance.getMovementInput().length();
        }
        return instance.movementForward;
    }
}
