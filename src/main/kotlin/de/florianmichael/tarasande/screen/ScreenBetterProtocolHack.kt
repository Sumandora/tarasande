package de.florianmichael.tarasande.screen

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import de.enzaxd.viaforge.equals.VersionList
import de.florianmichael.tarasande.util.render.RenderUtil
import de.florianmichael.tarasande.screen.element.ScreenBetterSlotList
import de.florianmichael.tarasande.screen.element.ScreenBetterSlotListEntry
import de.florianmichael.tarasande.screen.element.ScreenBetterSlotListWidget
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import su.mandora.tarasande.TarasandeMain
import java.awt.Color

class ScreenBetterProtocolHack(parent: Screen) : ScreenBetterSlotList(parent, 46, 12, object : ScreenBetterSlotListWidget.ListProvider {

        override fun get() = VersionList.getProtocols().map { p -> ProtocolEntry(p) }
}) {

    override fun init() {
        super.init()

        this.addDrawableChild(ButtonWidget(5, this.height - 25, 20, 20, Text.literal("<-")) {
            this.close()
        })
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(matrices, mouseX, mouseY, delta)

        this.renderTitle("Protocol Hack")
    }

    class ProtocolEntry(val protocol: ProtocolVersion) : ScreenBetterSlotListEntry() {

        override fun isSelected(): Boolean {
            return TarasandeMain.get().protocolHack.version == this.protocol.version
        }

        override fun renderEntry(matrices: MatrixStack, index: Int, entryWidth: Int, entryHeight: Int, mouseX: Int, mouseY: Int, hovered: Boolean) {
            RenderUtil.useMyStack(matrices)
            RenderUtil.textCenter(Text.literal(this.protocol.name), entryWidth.toFloat(), 0F, if (this.isSelected()) Color.green.rgb else Color.red.rgb)
            RenderUtil.ourStack()
        }

        override fun onClickEntry(mouseX: Double, mouseY: Double, mouseButton: Int): Boolean {
            TarasandeMain.get().protocolHack.version = this.protocol.version
            return true
        }
    }
}
