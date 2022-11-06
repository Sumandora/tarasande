package net.tarasandedevelopment.tarasande.screen.clientmenu.protocol

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import de.florianmichael.viaprotocolhack.util.VersionList
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.screen.base.ScreenBetterSlotList
import net.tarasandedevelopment.tarasande.screen.base.ScreenBetterSlotListEntry
import net.tarasandedevelopment.tarasande.screen.base.ScreenBetterSlotListWidget
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import java.awt.Color

class ScreenBetterSlotListProtocolHack : ScreenBetterSlotList(46, 12) {

    private val specialNames = HashMap<ProtocolVersion, String>()

    init {
        this.provideElements(object : ScreenBetterSlotListWidget.ListProvider {
            override fun get(): List<ScreenBetterSlotListEntry> {
                return VersionList.getProtocols().map { p -> ScreenBetterSlotListEntryProtocol(p, this@ScreenBetterSlotListProtocolHack) }
            }
        })

        specialNames[ProtocolVersion.v1_9_3] = "1.9.3-1.9.4"
        specialNames[ProtocolVersion.v1_11_1] = "1.11.1-1.11.2"
        specialNames[ProtocolVersion.v1_16_4] = "1.16.4-1.16.5"
        specialNames[ProtocolVersion.v1_18] = "1.18-1.18.1"
        specialNames[ProtocolVersion.v1_19_1] = "1.19.1-1.19.2"
        specialNames[ProtocolVersion.v1_19_3] = "22w44a"
    }

    override fun init() {
        val providedList = this.listProvider!!.get()

        for (entry in providedList)
            if (entry is ScreenBetterSlotListEntryProtocol)
                if (TarasandeMain.get().protocolHack.clientsideVersion == entry.protocol.version)
                    selected = providedList.indexOf(entry)

        super.init()

        this.addDrawableChild(ButtonWidget(5, this.height - 25, 20, 20, Text.of("<-")) {
            this.close()
        })
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(matrices, mouseX, mouseY, delta)

        this.renderTitle(matrices, "Protocol Hack")
    }

    class ScreenBetterSlotListEntryProtocol(val protocol: ProtocolVersion, private val parent: ScreenBetterSlotListProtocolHack) : ScreenBetterSlotListEntry() {

        override fun renderEntry(matrices: MatrixStack, index: Int, entryWidth: Int, entryHeight: Int, mouseX: Int, mouseY: Int, hovered: Boolean) {
            var name = protocol.name
            if (parent.specialNames.containsKey(protocol)) {
                name = parent.specialNames[protocol]
            }
            RenderUtil.font().textShadow(matrices, name, entryWidth / 2F, 0F, if (this.isSelected()) Color.green.rgb else Color.red.rgb, centered = true)
        }

        override fun onSingleClickEntry(mouseX: Double, mouseY: Double, mouseButton: Int) {
            TarasandeMain.get().protocolHack.version.value = this.protocol.version.toDouble()
            super.onSingleClickEntry(mouseX, mouseY, mouseButton)
        }
    }
}
