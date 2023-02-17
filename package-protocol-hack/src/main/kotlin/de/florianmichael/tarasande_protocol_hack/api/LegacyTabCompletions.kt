package de.florianmichael.tarasande_protocol_hack.api

import com.viaversion.viaversion.api.protocol.packet.PacketWrapper
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import com.viaversion.viaversion.api.type.Type
import com.viaversion.viaversion.protocols.protocol1_12_1to1_12.ServerboundPackets1_12_1
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.Protocol1_13To1_12_2
import de.florianmichael.tarasande_protocol_hack.TarasandeProtocolHack
import de.florianmichael.vialoadingbase.ViaLoadingBase

class LegacyTabCompletions {

    fun requestCompletion(string: String) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_12_2)) {
            PacketWrapper.create(ServerboundPackets1_12_1.TAB_COMPLETE, TarasandeProtocolHack.viaConnection).apply {
                write(Type.STRING, string)
                write(Type.BOOLEAN, false)
                write(Type.BOOLEAN, false)
                write(Type.OPTIONAL_POSITION, null)

                sendToServer(Protocol1_13To1_12_2::class.java)
            }
        }
    }
}
