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

package de.florianmichael.clampclient.injection.mixin.protocolhack;

import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.vialoadingbase.util.VersionListEnum;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ManagerModule;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.movement.ModuleSneak;
import net.tarasandedevelopment.tarasande_protocol_hack.util.values.ProtocolHackValues;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public class MixinKeyboardInput extends Input {

    @ModifyVariable(method = "tick", at = @At(value = "LOAD", ordinal = 0), argsOnly = true)
    private boolean injectTick(boolean slowDown) {
        ModuleSneak moduleSneak = ManagerModule.INSTANCE.get(ModuleSneak.class);
        if (moduleSneak.getEnabled().getValue() && moduleSneak.getDontSlowdown().getValue()) {
            return slowDown;
        }

        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(VersionListEnum.r1_13_2)) {
            return this.sneaking;
        } else if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(VersionListEnum.r1_14_4)) {
            return !MinecraftClient.getInstance().player.isSpectator() && (this.sneaking || slowDown);
        }
        return slowDown;
    }

    // I-EEE 754
    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/input/KeyboardInput;movementSideways:F", ordinal = 1, shift = At.Shift.BEFORE), cancellable = true)
    public void fixRoundingConvention(boolean slowDown, float f, CallbackInfo ci) {
        if (ProtocolHackValues.INSTANCE.getEmulatePlayerMovement().getValue()) {
            ci.cancel();

            this.movementSideways = (float)((double)this.movementSideways * 0.3D);
            this.movementForward = (float)((double)this.movementForward * 0.3D);
        }
    }
}
