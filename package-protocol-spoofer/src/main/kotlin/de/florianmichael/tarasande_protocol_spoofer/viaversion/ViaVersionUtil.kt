package de.florianmichael.tarasande_protocol_spoofer.viaversion

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.Protocol1_13To1_12_2
import de.florianmichael.tarasande_protocol_spoofer.spoofer.forgefaker.IForgeNetClientHandler
import de.florianmichael.tarasande_protocol_spoofer.spoofer.forgefaker.handler.Fml1NetClientHandler
import de.florianmichael.tarasande_protocol_spoofer.spoofer.forgefaker.handler.ModernFmlNetClientHandler
import de.florianmichael.tarasande_protocol_spoofer.spoofer.forgefaker.handler.ModernFmlState
import de.florianmichael.vialoadingbase.util.VersionList
import net.minecraft.network.ClientConnection

object ViaVersionUtil {

    fun builtForgeChannelMappings() {
        Protocol1_13To1_12_2.MAPPINGS.channelMappings["FML|HS"] = "fml:hs"
        Protocol1_13To1_12_2.MAPPINGS.channelMappings["FML|MP"] = "fml:mp"
        Protocol1_13To1_12_2.MAPPINGS.channelMappings["FML"] = "minecraft:fml"
        Protocol1_13To1_12_2.MAPPINGS.channelMappings["FORGE"] = "minecraft:forge"
    }

    fun createForgeHandler(connection: ClientConnection): IForgeNetClientHandler {
        if (VersionList.isNewerTo(ProtocolVersion.v1_18_2)) return ModernFmlNetClientHandler(ModernFmlState.FML_4, connection)
        if (VersionList.isNewerTo(ProtocolVersion.v1_17_1)) return ModernFmlNetClientHandler(ModernFmlState.FML_3, connection)
        if (VersionList.isNewerTo(ProtocolVersion.v1_12_2)) return ModernFmlNetClientHandler(ModernFmlState.FML_2, connection)

        return Fml1NetClientHandler(connection)
    }
}
