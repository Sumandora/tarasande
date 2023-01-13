package net.tarasandedevelopment.tarasande_protocol_hack.platform.betacraft

import net.minecraft.client.gui.screen.ConnectScreen
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.network.ServerAddress
import net.minecraft.client.network.ServerInfo
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Formatting
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.screen.base.AlwaysSelectedEntryListWidgetScreenBetterSlotListWidget
import net.tarasandedevelopment.tarasande.screen.base.EntryScreenBetterSlotListEntry
import net.tarasandedevelopment.tarasande.screen.base.ScreenBetterSlotList
import net.tarasandedevelopment.tarasande.util.math.TimeUtil
import net.tarasandedevelopment.tarasande.util.render.font.FontWrapper

class ScreenBetterSlotListBetaCraftServers(title: String, parent: Screen, servers: ArrayList<BetaCraftServ>) : ScreenBetterSlotList(title, parent, 46, -10, 320, 20) {
    private val reloadTimer = TimeUtil()
    private var reloadButton: ButtonWidget? = null

    init {
        updateElements(servers)
    }

    private fun updateElements(servers: ArrayList<BetaCraftServ>) {
        this.provideElements(object : AlwaysSelectedEntryListWidgetScreenBetterSlotListWidget.ListProvider {
            override fun get() = servers.map { EntryScreenBetterSlotListEntryBetaCraft(it) }
        })
    }

    override fun tick() {
        super.tick()
        if (reloadButton != null) reloadButton!!.active = reloadTimer.hasReached(5000L)
    }

    class EntryScreenBetterSlotListEntryBetaCraft(val server: BetaCraftServ) : EntryScreenBetterSlotListEntry() {

        override fun onDoubleClickEntry(mouseX: Double, mouseY: Double, mouseButton: Int) {
            ConnectScreen.connect(mc.currentScreen, mc, ServerAddress.parse(server.address), ServerInfo(server.displayName, server.address, false))
        }

        override fun renderEntry(matrices: MatrixStack, index: Int, entryWidth: Int, entryHeight: Int, mouseX: Int, mouseY: Int, hovered: Boolean) {
            FontWrapper.text(matrices, server.displayName + (if (server.onlineMode) Formatting.GREEN.toString() + " [Online-Mode]" else ""), entryWidth.toFloat() / 2F, entryHeight / 4F, centered = true)
        }
    }
}
