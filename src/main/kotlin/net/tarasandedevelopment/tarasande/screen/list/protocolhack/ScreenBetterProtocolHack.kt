package net.tarasandedevelopment.tarasande.screen.list.protocolhack

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import de.florianmichael.viaprotocolhack.util.VersionList
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.minecraft.text.TextColor
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.screen.element.ScreenBetterSlotList
import net.tarasandedevelopment.tarasande.screen.element.ScreenBetterSlotListEntry
import net.tarasandedevelopment.tarasande.screen.element.ScreenBetterSlotListWidget
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import java.awt.Color

class ScreenBetterProtocolHack(parent: Screen) : ScreenBetterSlotList(parent, 46, 12, object : ScreenBetterSlotListWidget.ListProvider {
        override fun get() = VersionList.getProtocols().map { p -> ProtocolEntry(p) }
}) {

    override fun init() {
        super.init()

        this.addDrawableChild(ButtonWidget(5, this.height - 25, 20, 20, Text.of("<-")) {
            this.close()
        })

        this.addDrawableChild(ButtonWidget(this.width / 2 - 49, this.height - 25, 98, 20, this.generateAutoDetectText()) {
            TarasandeMain.get().protocolHack.toggleAuto()
            it.message = this.generateAutoDetectText()
        })
    }

    private fun generateAutoDetectText() = Text.literal("Auto Detect").styled { it.withColor(TextColor.fromRgb((if (TarasandeMain.get().protocolHack.isAuto()) Color.green.rgb else Color.red.rgb))) }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(matrices, mouseX, mouseY, delta)

        this.renderTitle(matrices, "Protocol Hack")
    }

    class ProtocolEntry(val protocol: ProtocolVersion) : ScreenBetterSlotListEntry() {

        override fun isSelected(): Boolean {
            return TarasandeMain.get().protocolHack.clientsideVersion() == this.protocol.version
        }

        private fun colorShift(input: Color): Int {
            return if (TarasandeMain.get().protocolHack.isAuto()) input.darker().darker().darker().rgb else input.rgb
        }

        override fun renderEntry(matrices: MatrixStack, index: Int, entryWidth: Int, entryHeight: Int, mouseX: Int, mouseY: Int, hovered: Boolean) {
            RenderUtil.textCenter(matrices, Text.of(this.protocol.name), entryWidth.toFloat(), 0F, if (this.isSelected()) colorShift(Color.green) else colorShift(Color.red))
        }

        override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
            return !TarasandeMain.get().protocolHack.isAuto() && super.mouseClicked(mouseX, mouseY, button)
        }

        override fun onClickEntry(mouseX: Double, mouseY: Double, mouseButton: Int): Boolean {
            TarasandeMain.get().protocolHack.setVersion(this.protocol.version)
            return true
        }
    }
}
