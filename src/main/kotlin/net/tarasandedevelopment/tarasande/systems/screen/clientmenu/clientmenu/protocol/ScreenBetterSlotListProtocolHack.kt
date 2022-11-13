package net.tarasandedevelopment.tarasande.systems.screen.clientmenu.clientmenu.protocol

import com.mojang.blaze3d.systems.RenderSystem
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import de.florianmichael.viaprotocolhack.util.VersionList
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.protocolhack.extension.getSpecialName
import net.tarasandedevelopment.tarasande.protocolhack.platform.ProtocolHackValues
import net.tarasandedevelopment.tarasande.screen.base.ScreenBetterSlotList
import net.tarasandedevelopment.tarasande.screen.base.ScreenBetterSlotListEntry
import net.tarasandedevelopment.tarasande.screen.base.ScreenBetterSlotListWidget
import net.tarasandedevelopment.tarasande.util.render.font.FontWrapper
import java.awt.Color

class ScreenBetterSlotListProtocolHack : ScreenBetterSlotList(46, 12) {

    init {
        this.provideElements(object : ScreenBetterSlotListWidget.ListProvider {
            override fun get(): List<ScreenBetterSlotListEntry> {
                return VersionList.getProtocols().map { p -> ScreenBetterSlotListEntryProtocol(p) }
            }
        })
    }

    override fun init() {
        val providedList = this.listProvider!!.get()

        for (entry in providedList)
            if (entry is ScreenBetterSlotListEntryProtocol)
                if (TarasandeMain.instance.protocolHack.clientsideVersion == entry.protocol.version)
                    selected = providedList.indexOf(entry)

        super.init()

        if (TarasandeMain.clientValues().clientMenuBackButtons.value) {
            addDrawableChild(ButtonWidget(5, this.height - 25, 20, 20, Text.of("<-")) { RenderSystem.recordRenderCall { close() } })
        }
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(matrices, mouseX, mouseY, delta)

        this.renderTitle(matrices, "Protocol Hack")
    }

    class ScreenBetterSlotListEntryProtocol(val protocol: ProtocolVersion) : ScreenBetterSlotListEntry() {

        override fun renderEntry(matrices: MatrixStack, index: Int, entryWidth: Int, entryHeight: Int, mouseX: Int, mouseY: Int, hovered: Boolean) {
            FontWrapper.textShadow(matrices, protocol.getSpecialName(), entryWidth / 2F, 0F, if (this.isSelected()) Color.green.rgb else Color.red.rgb, centered = true)
        }

        override fun onSingleClickEntry(mouseX: Double, mouseY: Double, mouseButton: Int) {
            TarasandeMain.instance.protocolHack.version.value = this.protocol.version.toDouble()
            TarasandeMain.instance.protocolHack.update(this.protocol)
            super.onSingleClickEntry(mouseX, mouseY, mouseButton)
        }
    }
}
