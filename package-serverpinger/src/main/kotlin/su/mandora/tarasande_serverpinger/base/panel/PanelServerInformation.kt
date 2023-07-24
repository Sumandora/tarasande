package su.mandora.tarasande_serverpinger.base.panel

import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget.ServerEntry
import net.minecraft.client.network.ServerInfo
import net.minecraft.client.option.ServerList
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.Text
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.screen.panelsystem.Panel
import su.mandora.tarasande.util.SERVER_INFORMATION_OFFSET
import su.mandora.tarasande.util.render.RenderUtil
import su.mandora.tarasande.util.render.font.FontWrapper
import su.mandora.tarasande_serverpinger.injection.accessor.IMultiplayerServerListWidgetSubServerEntry

var emptyServer = ServerInfo("", "", false).apply {
    ping = 0L
    label = ScreenTexts.EMPTY
    playerCountLabel = ScreenTexts.EMPTY
    online = false
}

fun ServerInfo.copy(): ServerInfo {
    return ServerInfo.fromNbt(toNbt())
}

open class PanelServerInformation(private val owner: Any, private val finish: (server: ServerInfo) -> Unit) : Panel("Server Information", 300.0, FontWrapper.fontHeight() /* I can't access titleBarHeight yet TODO */ + 32.0, background = true, scissor = false) {

    companion object {
        var tooltip: MutableList<Text>? = null

        val emulatedMultiplayerScreen = object : MultiplayerScreen(null) {
            override fun setMultiplayerScreenTooltip(tooltip: MutableList<Text>?) {
                Companion.tooltip = tooltip
            }

            override fun getServerList(): ServerList {
                return object : ServerList(mc) {
                    override fun size(): Int {
                        return 0
                    }

                    override fun saveFile() {
                        // what do I look like?
                    }
                }
            }
        }
    }

    var server = emptyServer.copy()
        set(value) {
            field = value
            createEntry()
        }

    private val emulatedWidget = MultiplayerServerListWidget(null, null, 0, 0, 0, 0, 0)
    private var serverEntry: ServerEntry? = null

    override fun getValueOwner() = owner

    fun createEntry() {
        // Prevent memory leaks
        val iterator = emulatedMultiplayerScreen.serverListPinger.clientConnections.iterator()
        while (iterator.hasNext()) {
            iterator.next().disconnect(Text.empty())
            iterator.remove()
        }
        serverEntry = emulatedWidget.ServerEntry(emulatedMultiplayerScreen, server).apply {
            (this as IMultiplayerServerListWidgetSubServerEntry).tarasande_setCompletionConsumer(finish)
        }
    }

    override fun renderContent(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        val hovered = RenderUtil.isHovered(mouseX.toDouble(), mouseY.toDouble(), x, y + titleBarHeight, panelWidth, panelHeight - titleBarHeight)
        serverEntry?.render(context, 0, y.toInt() + titleBarHeight, x.toInt(), (panelWidth + SERVER_INFORMATION_OFFSET).toInt(), (panelHeight - titleBarHeight).toInt(), mouseX, mouseY, hovered, delta)
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(context, mouseX, mouseY, delta)
        if (tooltip != null) {
            context.drawTooltip(mc.textRenderer, tooltip, mouseX, mouseY)
            tooltip = null
        }
    }
}
