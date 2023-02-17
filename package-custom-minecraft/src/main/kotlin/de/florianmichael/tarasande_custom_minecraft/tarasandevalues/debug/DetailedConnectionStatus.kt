package de.florianmichael.tarasande_custom_minecraft.tarasandevalues.debug

import de.florianmichael.tarasande_custom_minecraft.TarasandeCustomMinecraft
import de.florianmichael.tarasande_custom_minecraft.viaversion.ViaVersionUtil
import net.minecraft.network.packet.c2s.login.LoginKeyC2SPacket
import net.tarasandedevelopment.tarasande.event.EventPacket
import net.tarasandedevelopment.tarasande.feature.tarasandevalue.impl.DebugValues
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.event.EventDispatcher
import java.util.concurrent.CopyOnWriteArrayList

object DetailedConnectionStatus {

    val showDetailedConnectionStatus = ValueBoolean(DebugValues, "Show detailed connection status", false, isEnabled = {
        if (TarasandeCustomMinecraft.tarasandeProtocolHackLoaded)
            !ViaVersionUtil.isLegacyLogin()
        else
            true
    })
    var connectionHistory = CopyOnWriteArrayList<String>()

    var connectionState = ConnectionState.UNKNOWN
        set(value) {
            if (value == ConnectionState.UNKNOWN)
                connectionHistory.clear()
            else
                connectionHistory.add(value.display)
            field = value
        }

    init {
        EventDispatcher.add(EventPacket::class.java) {
            if (it.type == EventPacket.Type.SEND && it.packet is LoginKeyC2SPacket) {
                if (connectionState != ConnectionState.UNKNOWN) connectionState = ConnectionState.ENCRYPTING
            }
        }
    }
}

