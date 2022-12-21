package de.florianmichael.tarasande_protocol_spoofer.spoofer.forgefaker

import com.google.gson.JsonObject
import de.florianmichael.tarasande_protocol_spoofer.TarasandeProtocolSpoofer
import de.florianmichael.tarasande_protocol_spoofer.spoofer.EntrySidebarPanelToggleableForgeFaker
import de.florianmichael.tarasande_protocol_spoofer.spoofer.forgefaker.handler.Fml1NetClientHandler
import de.florianmichael.tarasande_protocol_spoofer.spoofer.forgefaker.handler.ModernFmlNetClientHandler
import de.florianmichael.tarasande_protocol_spoofer.spoofer.forgefaker.handler.ModernFmlState
import de.florianmichael.tarasande_protocol_spoofer.spoofer.forgefaker.payload.IForgePayload
import de.florianmichael.tarasande_protocol_spoofer.spoofer.forgefaker.payload.legacy.LegacyForgePayload
import de.florianmichael.tarasande_protocol_spoofer.spoofer.forgefaker.payload.modern.ModernForgePayload
import de.florianmichael.tarasande_protocol_spoofer.viaversion.ViaVersionUtil
import net.minecraft.network.ClientConnection
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.multiplayer.ScreenExtensionSidebarMultiplayerScreen

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
        val forgeFaker = TarasandeMain.managerScreenExtension().get(ScreenExtensionSidebarMultiplayerScreen::class.java).sidebar.get(EntrySidebarPanelToggleableForgeFaker::class.java)

        if (TarasandeProtocolSpoofer.isVia() && forgeFaker.autoDetectFmlHandlerWithViaVersion.value) {
            return ViaVersionUtil.createForgeHandler(connection)
        }

        if (forgeFaker.fmlHandler.isSelected(1)) return ModernFmlNetClientHandler(ModernFmlState.FML_2, connection)
        if (forgeFaker.fmlHandler.isSelected(2)) return ModernFmlNetClientHandler(ModernFmlState.FML_3, connection)
        if (forgeFaker.fmlHandler.isSelected(3)) return ModernFmlNetClientHandler(ModernFmlState.FML_4, connection)

        return Fml1NetClientHandler(connection)
    }
}
