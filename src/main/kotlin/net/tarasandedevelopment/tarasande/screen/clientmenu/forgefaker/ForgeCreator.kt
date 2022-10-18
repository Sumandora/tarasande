package net.tarasandedevelopment.tarasande.screen.clientmenu.forgefaker

import com.google.gson.JsonObject
import net.minecraft.network.ClientConnection
import net.tarasandedevelopment.tarasande.screen.clientmenu.forgefaker.handler.Fml1NetClientHandler
import net.tarasandedevelopment.tarasande.screen.clientmenu.forgefaker.payload.IForgePayload
import net.tarasandedevelopment.tarasande.screen.clientmenu.forgefaker.payload.legacy.LegacyForgePayload
import net.tarasandedevelopment.tarasande.screen.clientmenu.forgefaker.payload.modern.ModernForgePayload

object ForgeCreator {

    fun createPayload(jsonObject: JsonObject): IForgePayload? {
        println(jsonObject.has("modinfo"))

        if (jsonObject.has("modinfo") && jsonObject.get("modinfo").isJsonObject) {
            return LegacyForgePayload(jsonObject.get("modinfo").asJsonObject)
        }

        if (jsonObject.has("forgeData") && jsonObject.get("forgeData").isJsonObject) {
            return ModernForgePayload(jsonObject.get("forgeData").asJsonObject)
        }

        return null
    }

    fun createNetHandler(connection: ClientConnection): IForgeNetClientHandler {
        return Fml1NetClientHandler(connection)
    }
}
