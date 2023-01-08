package net.tarasandedevelopment.tarasande_protocol_hack.platform.betacraft

import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.tarasandedevelopment.tarasande.screen.base.AlwaysSelectedEntryListWidgetScreenBetterSlotListWidget
import net.tarasandedevelopment.tarasande.screen.base.EntryScreenBetterSlotListEntry
import net.tarasandedevelopment.tarasande.screen.base.ScreenBetterSlotList
import net.tarasandedevelopment.tarasande.util.extension.minecraft.ButtonWidget
import net.tarasandedevelopment.tarasande.util.math.TimeUtil
import net.tarasandedevelopment.tarasande.util.render.font.FontWrapper

class ScreenBetterSlotListBetaCraftServers(private val titleName: String, parent: Screen, servers: ArrayList<BetaCraftServ>) : ScreenBetterSlotList(46, -10, 320, 20, parent) {
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

    override fun init() {
        super.init()

        addDrawableChild(ButtonWidget(3, 3, 100, 20, Text.literal("Reload")) {
            EntrySidebarPanelBetaCraftServers.createLookupThread {
                updateElements(it)
                reloadTimer.reset()
            }
        }.also { reloadButton = it })
    }

    override fun tick() {
        super.tick()
        if (reloadButton != null) reloadButton!!.active = reloadTimer.hasReached(5000L)
    }

    class EntryScreenBetterSlotListEntryBetaCraft(val server: BetaCraftServ) : EntryScreenBetterSlotListEntry() {

        override fun onDoubleClickEntry(mouseX: Double, mouseY: Double, mouseButton: Int) {}

        override fun renderEntry(matrices: MatrixStack, index: Int, entryWidth: Int, entryHeight: Int, mouseX: Int, mouseY: Int, hovered: Boolean) {
            FontWrapper.text(matrices, server.displayName, entryWidth.toFloat() / 2F, 1F, centered = true)
        }
    }
}
