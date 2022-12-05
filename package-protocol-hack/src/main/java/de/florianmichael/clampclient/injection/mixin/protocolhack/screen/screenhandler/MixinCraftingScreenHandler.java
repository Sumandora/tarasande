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

package de.florianmichael.clampclient.injection.mixin.protocolhack.screen.screenhandler;

import de.florianmichael.vialegacy.protocol.LegacyProtocolVersion;
import de.florianmichael.viaprotocolhack.util.VersionList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(CraftingScreenHandler.class)
public abstract class MixinCraftingScreenHandler extends AbstractRecipeScreenHandler<CraftingInventory> {
    @Shadow @Final private CraftingInventory input;

    @Shadow @Final private CraftingResultInventory result;

    public MixinCraftingScreenHandler(ScreenHandlerType<?> screenHandlerType, int i) {
        super(screenHandlerType, i);
    }

    @Inject(method = "onContentChanged", at = @At("HEAD"))
    public void emulateCrafting(Inventory inventory, CallbackInfo ci) {
        if (VersionList.isOlderOrEqualTo(LegacyProtocolVersion.r1_6_4)) {
            ItemStack itemStack = ItemStack.EMPTY;
            //noinspection DataFlowIssue
            final Optional<CraftingRecipe> optional = MinecraftClient.getInstance().getNetworkHandler().getRecipeManager().getFirstMatch(RecipeType.CRAFTING, this.input, MinecraftClient.getInstance().world);
            if (optional.isPresent()) {
                itemStack = optional.get().craft(this.input);
            }

            this.result.setStack(0, itemStack);
            this.setPreviousTrackedSlot(0, itemStack);
            MinecraftClient.getInstance().getNetworkHandler().onScreenHandlerSlotUpdate(new ScreenHandlerSlotUpdateS2CPacket(syncId, getRevision(), 0, itemStack));
        }
    }
}
