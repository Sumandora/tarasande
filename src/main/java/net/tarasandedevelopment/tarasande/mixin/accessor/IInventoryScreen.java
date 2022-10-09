package net.tarasandedevelopment.tarasande.mixin.accessor;

import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

public interface IInventoryScreen {

    void tarasande_onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType);

}
