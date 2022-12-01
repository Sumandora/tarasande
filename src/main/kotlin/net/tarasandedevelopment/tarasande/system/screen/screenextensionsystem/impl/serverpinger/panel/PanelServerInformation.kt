package net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.serverpinger.panel

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget.ServerEntry
import net.minecraft.client.network.ServerInfo
import net.minecraft.client.option.ServerList
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.Text
import net.tarasandedevelopment.tarasande.system.screen.panelsystem.Panel
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import net.tarasandedevelopment.tarasande.util.render.font.FontWrapper

var emptyServer = ServerInfo("", "", false).apply {
    ping = 0L
    label = ScreenTexts.EMPTY
    playerCountLabel = ScreenTexts.EMPTY
    online = false
}

fun ServerInfo.copy(): ServerInfo {
    return ServerInfo.fromNbt(toNbt())
}

open class PanelServerInformation(private val owner: Any) : Panel("Server Information", 300.0, FontWrapper.fontHeight() /* I can't access titleBarHeight yet TODO */ + 32.0, background = true, scissor = false) {

    var server = emptyServer.copy()
        set(value) {
            field = value
            createEntry()
        }

    private var tooltip: MutableList<Text>? = null
    private val emulatedWidget = MultiplayerServerListWidget(null, null, 0, 0, 0, 0, 0)
    private val emulatedMultiplayerScreen = object : MultiplayerScreen(null) {
        override fun setTooltip(tooltip: MutableList<Text>?) {
            this@PanelServerInformation.tooltip = tooltip
        }

        override fun getServerList(): ServerList {
            return object : ServerList(MinecraftClient.getInstance()) {
                override fun size(): Int {
                    return 0
                }

                override fun saveFile() {
                    // what do I look like?
                }
            }
        }
    }
    private var serverEntry: ServerEntry? = null

    override fun getValueOwner() = owner

    fun createEntry() {
        serverEntry = emulatedWidget.ServerEntry(emulatedMultiplayerScreen, server)
    }

    override fun renderContent(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        val hovered = RenderUtil.isHovered(mouseX.toDouble(), mouseY.toDouble(), x, y + titleBarHeight, panelWidth, panelHeight - titleBarHeight)
        serverEntry?.render(matrices, 0, y.toInt() + titleBarHeight, x.toInt(), (panelWidth + 5.0 /* sick, minecraft simple shifts everything by 5 units */).toInt(), (panelHeight - titleBarHeight).toInt(), mouseX, mouseY, hovered, MinecraftClient.getInstance().tickDelta)
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(matrices, mouseX, mouseY, delta)
        if (tooltip != null) {
            MinecraftClient.getInstance().currentScreen?.renderTooltip(matrices, tooltip, mouseX, mouseY)
            tooltip = null
        }
    }
}
