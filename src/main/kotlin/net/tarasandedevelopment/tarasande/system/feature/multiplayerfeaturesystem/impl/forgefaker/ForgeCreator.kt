package net.tarasandedevelopment.tarasande.system.feature.multiplayerfeaturesystem.impl.forgefaker

import com.google.gson.JsonObject
import net.minecraft.network.ClientConnection
import net.tarasandedevelopment.tarasande.system.feature.multiplayerfeaturesystem.impl.forgefaker.handler.ModernFmlNetClientHandler
import net.tarasandedevelopment.tarasande.system.feature.multiplayerfeaturesystem.impl.forgefaker.handler.ModernFmlState
import net.tarasandedevelopment.tarasande.system.feature.multiplayerfeaturesystem.impl.forgefaker.payload.IForgePayload
import net.tarasandedevelopment.tarasande.system.feature.multiplayerfeaturesystem.impl.forgefaker.payload.legacy.LegacyForgePayload
import net.tarasandedevelopment.tarasande.system.feature.multiplayerfeaturesystem.impl.forgefaker.payload.modern.ModernForgePayload

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
        return ModernFmlNetClientHandler(ModernFmlState.FML_4, connection)
    }
}
