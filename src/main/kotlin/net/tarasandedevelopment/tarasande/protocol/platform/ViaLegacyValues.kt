@file:Suppress("ClassName")

package net.tarasandedevelopment.tarasande.protocol.platform

import net.tarasandedevelopment.tarasande.value.ValueBoolean

object ViaLegacyValues {

    // General
    val filterItemGroups = ValueBoolean(this, "Filter item groups", true)

    // 1.14 -> 1.13.2
    val smoothOutMerchantScreens = ValueBoolean(this, "Smooth out merchant screens (1.14 -> 1.13.2)", true)
}