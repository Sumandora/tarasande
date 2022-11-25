package net.tarasandedevelopment.tarasande.system.feature.screenextensionsystem.impl.inventory

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import net.minecraft.client.gui.screen.ingame.InventoryScreen
import net.minecraft.screen.slot.SlotActionType
import net.tarasandedevelopment.tarasande.system.feature.screenextensionsystem.ScreenExtensionButton
import net.tarasandedevelopment.tarasande.util.extension.andNewer

class ScreenExtensionButtonCraftingDupe : ScreenExtensionButton<InventoryScreen>("Crafting Dupe", InventoryScreen::class.java, version = ProtocolVersion.v1_17.andNewer()) {

    override fun onClick(current: InventoryScreen) {
        current.screenHandler.getSlot(0).also {
            current.onMouseClick(it, it.id, 0, SlotActionType.THROW)
        }
    }
}

