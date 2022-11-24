package net.tarasandedevelopment.tarasande.systems.feature.screenextensionsystem.impl.serverpinger.panel

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget
import net.minecraft.client.network.ServerInfo
import net.minecraft.client.option.ServerList
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.tarasandedevelopment.tarasande.systems.screen.panelsystem.Panel
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import net.tarasandedevelopment.tarasande.util.render.font.FontWrapper

abstract class PanelServerInformation : Panel("Server Information", 300.0, FontWrapper.fontHeight() /* I can't access titleBarHeight yet TODO */ + 32.0, background = true, scissor = false) {

    @Suppress("LeakingThis")
    var server = updateServerInfo()

    abstract fun updateServerInfo(): ServerInfo

    private var tooltip: MutableList<Text>? = null
    private val emulatedWidget = MultiplayerServerListWidget(null, null, 0, 0, 0, 0, 0)
    protected var serverEntry = createEntry()

    protected fun createEntry() = emulatedWidget.ServerEntry(
        object : MultiplayerScreen(null) {
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
        }, server)

    override fun renderContent(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        val hovered = RenderUtil.isHovered(mouseX.toDouble(), mouseY.toDouble(), x, y + titleBarHeight, panelWidth, panelHeight - titleBarHeight)
        serverEntry.render(matrices, 0, y.toInt() + titleBarHeight, (x + 1.0).toInt(), (panelWidth + 5.0 - 2.0 /* sick, minecraft simple shifts everything by 5 units */).toInt(), (panelHeight - titleBarHeight).toInt(), mouseX, mouseY, hovered, MinecraftClient.getInstance().tickDelta)
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(matrices, mouseX, mouseY, delta)
        if(tooltip != null) {
            MinecraftClient.getInstance().currentScreen?.renderTooltip(matrices, tooltip, mouseX, mouseY)
            tooltip = null
        }
    }
}
