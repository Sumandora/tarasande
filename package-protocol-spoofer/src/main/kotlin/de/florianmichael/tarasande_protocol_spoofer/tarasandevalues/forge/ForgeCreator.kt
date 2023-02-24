package de.florianmichael.tarasande_protocol_spoofer.tarasandevalues.forge

import com.google.gson.JsonObject
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import de.florianmichael.tarasande_protocol_spoofer.tarasandevalues.forge.handler.Fml1NetClientHandler
import de.florianmichael.tarasande_protocol_spoofer.tarasandevalues.forge.handler.ModernFmlNetClientHandler
import de.florianmichael.tarasande_protocol_spoofer.tarasandevalues.forge.handler.ModernFmlState
import de.florianmichael.tarasande_protocol_spoofer.tarasandevalues.forge.payload.IForgePayload
import de.florianmichael.tarasande_protocol_spoofer.tarasandevalues.forge.payload.legacy.LegacyForgePayload
import de.florianmichael.tarasande_protocol_spoofer.tarasandevalues.forge.payload.modern.ModernForgePayload
import de.florianmichael.vialoadingbase.ViaLoadingBase
import net.minecraft.network.ClientConnection

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
        if (ViaLoadingBase.getTargetVersion().isNewerThan(ProtocolVersion.v1_18_2)) return ModernFmlNetClientHandler(ModernFmlState.FML_4, connection)
        if (ViaLoadingBase.getTargetVersion().isNewerThan(ProtocolVersion.v1_17_1)) return ModernFmlNetClientHandler(ModernFmlState.FML_3, connection)
        if (ViaLoadingBase.getTargetVersion().isNewerThan(ProtocolVersion.v1_12_2)) return ModernFmlNetClientHandler(ModernFmlState.FML_2, connection)

        return Fml1NetClientHandler(connection)
    }
}
