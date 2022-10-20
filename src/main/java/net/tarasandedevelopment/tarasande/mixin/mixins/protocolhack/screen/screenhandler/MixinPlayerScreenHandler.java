/*
 * Copyright (c) FlorianMichael as EnZaXD 2022
 * Created on 7/9/22, 10:28 AM
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

package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.screen.screenhandler;

import de.florianmichael.viaprotocolhack.util.VersionList;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(PlayerScreenHandler.class)
public abstract class MixinPlayerScreenHandler extends AbstractRecipeScreenHandler<CraftingInventory> {

    public MixinPlayerScreenHandler(ScreenHandlerType<?> screenHandlerType, int i) {
        super(screenHandlerType, i);
    }

    @Redirect(method = "<init>",
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/screen/PlayerScreenHandler$2;<init>(Lnet/minecraft/screen/PlayerScreenHandler;Lnet/minecraft/inventory/Inventory;III)V")),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/PlayerScreenHandler;addSlot(Lnet/minecraft/screen/slot/Slot;)Lnet/minecraft/screen/slot/Slot;", ordinal = 0))
    private Slot redirectAddOffhandSlot(PlayerScreenHandler screenHandler, Slot slot) {
        if (VersionList.isOlderOrEqualTo(VersionList.R1_8))
            return null;
        return addSlot(slot);
    }

    @ModifyVariable(method = "transferSlot", ordinal = 0, at = @At(value = "STORE", ordinal = 0))
    private EquipmentSlot injectTransferSlot(EquipmentSlot slot) {
        if (VersionList.isOlderOrEqualTo(VersionList.R1_8) && slot == EquipmentSlot.OFFHAND)
            return EquipmentSlot.MAINHAND;
        else
            return slot;
    }
}
