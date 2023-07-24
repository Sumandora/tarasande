package su.mandora.tarasande_protocol_spoofer.viaversion

import com.viaversion.viaversion.api.protocol.packet.PacketWrapper
import com.viaversion.viaversion.api.type.Type
import com.viaversion.viaversion.protocols.protocol1_12to1_11_1.ServerboundPackets1_12
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.Protocol1_13To1_12_2
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack
import net.raphimc.vialoader.util.VersionEnum
import su.mandora.tarasande_protocol_spoofer.TarasandeProtocolSpoofer

object ViaVersionUtil {

    fun builtForgeChannelMappings() {
        Protocol1_13To1_12_2.MAPPINGS.channelMappings["FML|HS"] = "fml:hs"
        Protocol1_13To1_12_2.MAPPINGS.channelMappings["FML|MP"] = "fml:mp"
        Protocol1_13To1_12_2.MAPPINGS.channelMappings["FML"] = "minecraft:fml"
        Protocol1_13To1_12_2.MAPPINGS.channelMappings["FORGE"] = "minecraft:forge"
    }

    fun sendLegacyPluginMessage(channel: String, value: ByteArray): Boolean {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_12_2)) {
            val customPayload = PacketWrapper.create(ServerboundPackets1_12.PLUGIN_MESSAGE, TarasandeProtocolSpoofer.clientConnection!!.channel.attr(ProtocolHack.LOCAL_VIA_CONNECTION).get())
            customPayload.write(Type.STRING, channel)
            customPayload.write(Type.REMAINING_BYTES, value)

            customPayload.sendToServerRaw()
            return true
        }
        return false
    }
}
