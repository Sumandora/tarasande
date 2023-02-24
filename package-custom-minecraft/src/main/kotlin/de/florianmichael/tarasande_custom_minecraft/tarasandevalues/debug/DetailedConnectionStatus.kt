package de.florianmichael.tarasande_custom_minecraft.tarasandevalues.debug

import de.florianmichael.tarasande_custom_minecraft.TarasandeCustomMinecraft
import de.florianmichael.tarasande_custom_minecraft.viaversion.ViaVersionUtil
import net.tarasandedevelopment.tarasande.feature.statusrenderer.StatusRenderer
import net.tarasandedevelopment.tarasande.feature.tarasandevalue.impl.DebugValues
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean

object DetailedConnectionStatus {

    val showDetailedConnectionStatus = ValueBoolean(DebugValues, "Show detailed connection status", false, isEnabled = {
        if (TarasandeCustomMinecraft.viaFabricPlusLoaded)
            !ViaVersionUtil.isLegacyLogin()
        else
            true
    })

    fun updateConnectionState(connectionState: ConnectionState) {
        if (!showDetailedConnectionStatus.value) return

        StatusRenderer.setStatus(mc.currentScreen!!, connectionState.display)
    }
}
