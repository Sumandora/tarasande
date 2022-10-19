package net.tarasandedevelopment.tarasande.screen.clientmenu.forgefaker

import com.google.gson.JsonObject
import de.florianmichael.viaprotocolhack.util.VersionList
import net.minecraft.network.ClientConnection
import net.tarasandedevelopment.tarasande.screen.clientmenu.forgefaker.handler.Fml1NetClientHandler
import net.tarasandedevelopment.tarasande.screen.clientmenu.forgefaker.handler.ModernFmlNetClientHandler
import net.tarasandedevelopment.tarasande.screen.clientmenu.forgefaker.handler.ModernFmlState
import net.tarasandedevelopment.tarasande.screen.clientmenu.forgefaker.payload.IForgePayload
import net.tarasandedevelopment.tarasande.screen.clientmenu.forgefaker.payload.legacy.LegacyForgePayload
import net.tarasandedevelopment.tarasande.screen.clientmenu.forgefaker.payload.modern.ModernForgePayload

object ForgeCreator {

    fun createPayload(jsonObject: JsonObject): IForgePayload? {
        if (jsonObject.has("modinfo") && jsonObject.get("modinfo").isJsonObject) {
            return LegacyForgePayload(jsonObject.get("modinfo").asJsonObject)
        }
        if (jsonObject.has("forgeData") && jsonObject.get("forgeData").isJsonObject) {
            return ModernForgePayload(jsonObject.get("forgeData").asJsonObject)
        }
        return null
    }

    fun createNetHandler(connection: ClientConnection): IForgeNetClientHandler {
        if (VersionList.isNewerTo(VersionList.R1_18_2)) {
            return ModernFmlNetClientHandler(ModernFmlState.FML_4, connection)
        }
        if (VersionList.isNewerTo(VersionList.R1_17_1)) {
            return ModernFmlNetClientHandler(ModernFmlState.FML_3, connection)
        }
        if (VersionList.isNewerTo(VersionList.R1_12_2)) {
            return ModernFmlNetClientHandler(ModernFmlState.FML_2, connection)
        }
        return Fml1NetClientHandler(connection)
    }
}
