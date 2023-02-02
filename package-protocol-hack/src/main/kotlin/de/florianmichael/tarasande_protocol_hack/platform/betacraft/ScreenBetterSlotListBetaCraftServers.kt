package de.florianmichael.tarasande_protocol_hack.platform.betacraft

import net.minecraft.client.gui.screen.ConnectScreen
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.network.ServerAddress
import net.minecraft.client.network.ServerInfo
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.util.math.TimeUtil
import net.tarasandedevelopment.tarasande.util.render.font.FontWrapper
import net.tarasandedevelopment.tarasande.util.screen.EntryScreenBetterSlotList
import net.tarasandedevelopment.tarasande.util.screen.ScreenBetterSlotList
import kotlin.math.max

class ScreenBetterSlotListBetaCraftServers(title: String, parent: Screen) : ScreenBetterSlotList(title, parent, 46, -10) {
    private val reloadTimer = TimeUtil()
    private var reloadButton: ButtonWidget? = null

    fun updateElements(servers: ArrayList<BetaCraftServ>) {
        this.provideElements {
            servers.map { EntryScreenBetterSlotListBetaCraft(it) }
        }
        mc.executeSync {
            reload()
        }
    }

    override fun tick() {
        if (reloadButton != null) reloadButton!!.active = reloadTimer.hasReached(5000L)
    }

    class EntryScreenBetterSlotListBetaCraft(val server: BetaCraftServ) : EntryScreenBetterSlotList(max(320, FontWrapper.getWidth(format(server))), 20) {
        companion object {
            private fun format(server: BetaCraftServ) = server.displayName + (if (server.onlineMode) Formatting.GREEN.toString() + " [Online-Mode]" else "")
        }

        val title by lazy { format(server) }

        override fun getNarration(): Text {
            return Text.of(title)
        }

        override fun onDoubleClickEntry(mouseX: Double, mouseY: Double, mouseButton: Int) {
            ConnectScreen.connect(mc.currentScreen, mc, ServerAddress.parse(server.address), ServerInfo(server.displayName, server.address, false))
        }

        override fun renderEntry(matrices: MatrixStack, index: Int, entryWidth: Int, entryHeight: Int, mouseX: Int, mouseY: Int, hovered: Boolean) {
            FontWrapper.text(matrices, title, entryWidth / 2F, entryHeight / 2F - FontWrapper.fontHeight() / 2F, centered = true)
        }
    }
}
