package su.mandora.tarasande_protocol_spoofer.tarasandevalues.forge

import com.google.gson.JsonObject
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack
import net.minecraft.network.ClientConnection
import net.raphimc.vialoader.util.VersionEnum
import su.mandora.tarasande_protocol_spoofer.tarasandevalues.forge.handler.Fml1NetClientHandler
import su.mandora.tarasande_protocol_spoofer.tarasandevalues.forge.handler.ModernFmlNetClientHandler
import su.mandora.tarasande_protocol_spoofer.tarasandevalues.forge.handler.ModernFmlState
import su.mandora.tarasande_protocol_spoofer.tarasandevalues.forge.payload.IForgePayload
import su.mandora.tarasande_protocol_spoofer.tarasandevalues.forge.payload.legacy.LegacyForgePayload
import su.mandora.tarasande_protocol_spoofer.tarasandevalues.forge.payload.modern.ModernForgePayload

object ForgeCreator {

    fun createPayload(jsonObject: JsonObject?): IForgePayload? {
        if (jsonObject == null) {
            return null
        }
        if (jsonObject.has("modinfo") && jsonObject.get("modinfo").isJsonObject) {
            return LegacyForgePayload(jsonObject.get("modinfo").asJsonObject)
        }
        if (jsonObject.has("forgeData") && jsonObject.get("forgeData").isJsonObject) {
            return ModernForgePayload(jsonObject.get("forgeData").asJsonObject)
        }
        return null
    }

    fun createNetHandler(connection: ClientConnection): IForgeNetClientHandler {
        if (ProtocolHack.getTargetVersion().isNewerThan(VersionEnum.r1_18_2)) return ModernFmlNetClientHandler(ModernFmlState.FML_4, connection)
        if (ProtocolHack.getTargetVersion().isNewerThan(VersionEnum.r1_17_1)) return ModernFmlNetClientHandler(ModernFmlState.FML_3, connection)
        if (ProtocolHack.getTargetVersion().isNewerThan(VersionEnum.r1_12_2)) return ModernFmlNetClientHandler(ModernFmlState.FML_2, connection)

        return Fml1NetClientHandler(connection)
    }
}
