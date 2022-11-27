package net.tarasandedevelopment.tarasande.system.feature.screenextensionsystem.impl.inventory

import net.minecraft.client.gui.screen.ingame.InventoryScreen
import net.minecraft.screen.slot.SlotActionType
import net.tarasandedevelopment.tarasande.system.feature.screenextensionsystem.ScreenExtensionButton

class ScreenExtensionButtonCraftingDupe : ScreenExtensionButton<InventoryScreen>("Crafting Dupe", InventoryScreen::class.java) {

    override fun onClick(current: InventoryScreen) {
        current.screenHandler.getSlot(0).also {
            current.onMouseClick(it, it.id, 0, SlotActionType.THROW)
        }
    }
}

