/*
 * Copyright (c) FlorianMichael as EnZaXD 2022
 * Created on 7/9/22, 10:26 AM
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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.village.MerchantInventory;
import net.minecraft.village.TradeOfferList;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MerchantScreenHandler.class)
public abstract class MixinMerchantScreenHandler extends ScreenHandler {

    public MixinMerchantScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId) {
        super(type, syncId);
    }

    @Shadow public abstract TradeOfferList getRecipes();

    @Shadow @Final private MerchantInventory merchantInventory;

    @Inject(method = "switchTo", at = @At("HEAD"), cancellable = true)
    private void injectSwitchTo(int recipeId, CallbackInfo ci) {
        if (VersionList.isNewerTo(VersionList.R1_13_2))
            return;

        ci.cancel();

        if (recipeId >= getRecipes().size())
            return;

        var interactionManager = MinecraftClient.getInstance().interactionManager;
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        assert player != null;
        assert interactionManager != null;

        // move 1st input slot to inventory
        if (!merchantInventory.getStack(0).isEmpty()) {
            int count = merchantInventory.getStack(0).getCount();
            interactionManager.clickSlot(syncId, 0, 0, SlotActionType.QUICK_MOVE, player);
            if (count == merchantInventory.getStack(0).getCount())
                return;
        }

        // move 2nd input slot to inventory
        if (!merchantInventory.getStack(1).isEmpty()) {
            int count = merchantInventory.getStack(1).getCount();
            interactionManager.clickSlot(syncId, 1, 0, SlotActionType.QUICK_MOVE, player);
            if (count == merchantInventory.getStack(1).getCount())
                return;
        }

        // refill the slots
        if (merchantInventory.getStack(0).isEmpty() && merchantInventory.getStack(1).isEmpty()) {
            autofill(interactionManager, player, 0, getRecipes().get(recipeId).getAdjustedFirstBuyItem());
            autofill(interactionManager, player, 1, getRecipes().get(recipeId).getSecondBuyItem());
        }
    }

    @Unique
    private void autofill(ClientPlayerInteractionManager interactionManager, ClientPlayerEntity player,
                          int inputSlot, ItemStack stackNeeded) {
        if (stackNeeded.isEmpty())
            return;

        int slot;
        for (slot = 3; slot < 39; slot++) {
            ItemStack stack = slots.get(slot).getStack();
            if (stack.getItem() == stackNeeded.getItem() && ItemStack.areNbtEqual(stack, stackNeeded)) {
                break;
            }
        }
        if (slot == 39)
            return;

        boolean wasHoldingItem = !player.currentScreenHandler.getCursorStack().isEmpty();
        interactionManager.clickSlot(syncId, slot, 0, SlotActionType.PICKUP, player);
        interactionManager.clickSlot(syncId, slot, 0, SlotActionType.PICKUP_ALL, player);
        interactionManager.clickSlot(syncId, inputSlot, 0, SlotActionType.PICKUP, player);
        if (wasHoldingItem)
            interactionManager.clickSlot(syncId, slot, 0, SlotActionType.PICKUP, player);
    }

    @Inject(method = "canInsertIntoSlot", at = @At("HEAD"), cancellable = true)
    private void injectCanInsertIntoSlot(ItemStack stack, Slot slot, CallbackInfoReturnable<Boolean> ci) {
        if (VersionList.isOlderOrEqualTo(VersionList.R1_13_2))
            ci.setReturnValue(true);
    }
}
