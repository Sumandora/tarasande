package su.mandora.tarasande_rejected_features.tarasandevalues

import net.minecraft.client.gui.screen.ingame.HandledScreen
import su.mandora.tarasande.feature.tarasandevalue.impl.TargetingValues
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean

object ClosedInventory {

    private val closedInventory = ValueBoolean(TargetingValues, "Closed inventory", false)

    fun shouldBlock(): Boolean {
        return closedInventory.value && mc.currentScreen is HandledScreen<*>
    }

}