package su.mandora.tarasande_custom_minecraft.tarasandevalues.debug

import su.mandora.tarasande.feature.statusrenderer.StatusRenderer
import su.mandora.tarasande.feature.tarasandevalue.impl.DebugValues
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande_custom_minecraft.TarasandeCustomMinecraft
import su.mandora.tarasande_custom_minecraft.viaversion.ViaVersionUtil

object DetailedConnectionStatus {

    private val showDetailedConnectionStatus = ValueBoolean(DebugValues, "Show detailed connection status", false, isEnabled = {
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
