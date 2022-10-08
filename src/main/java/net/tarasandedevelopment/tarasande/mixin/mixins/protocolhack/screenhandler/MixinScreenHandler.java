/*
 * Copyright (c) FlorianMichael as EnZaXD 2022
 * Created on 7/9/22, 1:46 AM
 *
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.0--
 *
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license.
 */

package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.screenhandler;

import de.florianmichael.viaprotocolhack.util.VersionList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.tarasandedevelopment.tarasande.mixin.accessor.protocolhack.IScreenHandler_Protocol;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenHandler.class)
public class MixinScreenHandler implements IScreenHandler_Protocol {

    @Unique
    private short lastActionId = 0;

    @Inject(method = "internalOnSlotClick", at = @At("HEAD"), cancellable = true)
    private void injectInternalOnSlotClick(int slot, int clickData, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        if (VersionList.isOlderOrEqualTo(VersionList.R1_8) && actionType == SlotActionType.SWAP && clickData == 40) {
            ci.cancel();
        }
    }

    @Override
    public short tarasande_getAndIncrementLastActionId() {
        return ++lastActionId;
    }
}
