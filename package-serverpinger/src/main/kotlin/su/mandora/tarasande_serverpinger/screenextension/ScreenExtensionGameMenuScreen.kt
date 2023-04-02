package su.mandora.tarasande_serverpinger.screenextension

import net.minecraft.client.gui.Element
import net.minecraft.client.gui.screen.ConnectScreen
import net.minecraft.client.gui.screen.DirectConnectScreen
import net.minecraft.client.gui.screen.GameMenuScreen
import net.minecraft.client.gui.screen.TitleScreen
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen
import net.minecraft.client.network.ServerAddress
import net.minecraft.client.network.ServerInfo
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.screen.panelsystem.impl.button.PanelButton
import su.mandora.tarasande.system.screen.screenextensionsystem.ScreenExtension
import su.mandora.tarasande.util.player.PlayerUtil
import su.mandora.tarasande.util.string.StringUtil

class ScreenExtensionGameMenuScreen : ScreenExtension<GameMenuScreen>(GameMenuScreen::class.java) {

    private var serverEntry = ServerInfo("", "", false)

    override fun createElements(screen: GameMenuScreen): MutableList<Element> {
        return mutableListOf(PanelButton.createButton(screen.width / 2 - 48, 3, 98, 25, StringUtil.uncoverTranslation("selectServer.direct")) {
            mc.setScreen(DirectConnectScreen(screen, {
                if (it) {
                    PlayerUtil.disconnect()
                    ConnectScreen.connect(MultiplayerScreen(TitleScreen()), mc, ServerAddress.parse(serverEntry.address), serverEntry)
                } else {
                    mc.setScreen(screen)
                }
            }, serverEntry))
        })
    }
}
