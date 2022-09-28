package de.florianmichael.tarasande.screen

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import de.enzaxd.viaforge.equals.VersionList
import de.florianmichael.tarasande.screen.element.ScreenBetterSlotList
import de.florianmichael.tarasande.screen.element.ScreenBetterSlotListEntry
import de.florianmichael.tarasande.screen.element.ScreenBetterSlotListWidget
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.minecraft.text.TextColor
import net.minecraft.util.Formatting
import su.mandora.tarasande.TarasandeMain
import java.awt.Color

class ScreenBetterProtocolHack(parent: Screen) : ScreenBetterSlotList(
    parent,
    16,
    20,
    object : ScreenBetterSlotListWidget.ListProvider {

        override fun get(): List<ScreenBetterSlotListEntry> {
            return VersionList.getProtocols().map { p -> ScreenBetterProtocolHack.ProtocolEntry(p) }
        }
    }) {

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(matrices, mouseX, mouseY, delta)

        drawCenteredText(matrices, textRenderer, "Protocol Hack", width / 2, 8 - textRenderer.fontHeight / 2, -1)
    }

    class ProtocolEntry(val protocol: ProtocolVersion) : ScreenBetterSlotListEntry() {

        override fun renderEntry(matrices: MatrixStack, index: Int, entryWidth: Int, entryHeight: Int, mouseX: Int, mouseY: Int, hovered: Boolean) {
            val font = MinecraftClient.getInstance().textRenderer
            val text = Text.literal(this.protocol.name)

            matrices.push()
            matrices.scale(2F, 2F, 2F)
            // @formatting::off
            font.drawWithShadow(matrices, text.styled {
                it.withColor(
                    TextColor.fromFormatting(
                        if (TarasandeMain.get().protocolHack.version == this.protocol.version)
                            Formatting.GREEN else Formatting.RED)
                )
            }, entryWidth / 2F - (font.getWidth(text.string) / 2F), 0F, Color.white.rgb)
            // @formatting::on
            matrices.pop()
        }

        override fun onClickEntry(mouseX: Double, mouseY: Double, mouseButton: Int): Boolean {
            TarasandeMain.get().protocolHack.version = this.protocol.version
            return true
        }
    }
}
