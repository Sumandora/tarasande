package de.florianmichael.tarasande_rejected_features.tarasandevalues

import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.tarasandedevelopment.tarasande.feature.tarasandevalue.impl.TargetingValues
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean

object ClosedInventory {

    private val closedInventory = ValueBoolean(TargetingValues, "Closed inventory", false)

    fun shouldBlock(): Boolean {
        return closedInventory.value && mc.currentScreen is HandledScreen<*>
    }

}