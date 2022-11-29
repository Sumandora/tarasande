package de.florianmichael.tarasande_protocol_spoofer.multiplayerfeature.forgefaker

import com.google.gson.JsonObject
import de.florianmichael.tarasande_protocol_spoofer.multiplayerfeature.forgefaker.handler.Fml1NetClientHandler
import net.minecraft.network.ClientConnection
import de.florianmichael.tarasande_protocol_spoofer.multiplayerfeature.forgefaker.handler.ModernFmlNetClientHandler
import de.florianmichael.tarasande_protocol_spoofer.multiplayerfeature.forgefaker.handler.ModernFmlState
import de.florianmichael.tarasande_protocol_spoofer.multiplayerfeature.forgefaker.payload.IForgePayload
import de.florianmichael.tarasande_protocol_spoofer.multiplayerfeature.forgefaker.payload.legacy.LegacyForgePayload
import de.florianmichael.tarasande_protocol_spoofer.multiplayerfeature.forgefaker.payload.modern.ModernForgePayload
import net.minecraft.SharedConstants

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
        var clientsideVersion = SharedConstants.getGameVersion().protocolVersion

        if (System.getProperty("target-version") != null) {
            clientsideVersion = Integer.parseInt(System.getProperty("target-version"))
        }

        if (clientsideVersion > 758 /* 1.18.2 */) {
            return ModernFmlNetClientHandler(ModernFmlState.FML_4, connection)
        }
        if (clientsideVersion > 756 /* 1.17.1 */) {
            return ModernFmlNetClientHandler(ModernFmlState.FML_3, connection)
        }
        if (clientsideVersion > 340 /* 1.12.2 */) {
            return ModernFmlNetClientHandler(ModernFmlState.FML_2, connection)
        }
        return Fml1NetClientHandler(connection)
    }
}
