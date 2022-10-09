package net.tarasandedevelopment.tarasande.mixin.mixins;

import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.tarasandedevelopment.tarasande.mixin.accessor.IInventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(InventoryScreen.class)
public abstract class MixinInventoryScreen implements IInventoryScreen {

    @Shadow protected abstract void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType);

    @Override
    public void tarasande_onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType) {
        this.onMouseClick(slot, slotId, button, actionType);
    }
}
