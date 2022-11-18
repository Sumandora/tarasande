package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.screen.screenhandler;

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

@SuppressWarnings("ConstantConditions")
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
