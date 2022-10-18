package net.tarasandedevelopment.tarasande.screen.clientmenu.forgefaker.ui

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.tarasandedevelopment.tarasande.screen.base.ScreenBetterSlotList
import net.tarasandedevelopment.tarasande.screen.base.ScreenBetterSlotListEntry
import net.tarasandedevelopment.tarasande.screen.base.ScreenBetterSlotListWidget
import net.tarasandedevelopment.tarasande.screen.clientmenu.forgefaker.payload.IForgePayload
import net.tarasandedevelopment.tarasande.screen.clientmenu.forgefaker.payload.legacy.ModStruct
import net.tarasandedevelopment.tarasande.util.render.RenderUtil

class ScreenBetterForgeModList(parent: Screen, private val titleName: String, val struct: IForgePayload) : ScreenBetterSlotList(46, 400, MinecraftClient.getInstance().textRenderer.fontHeight * 2 + 5) {

    init {
        this.prevScreen = parent

        this.provideElements(object : ScreenBetterSlotListWidget.ListProvider {
            override fun get(): List<ScreenBetterForgeModListEntry> {
                return struct.installedMods().map { m -> ScreenBetterForgeModListEntry(m) }
            }
        })
    }

    override fun init() {
        super.init()

        this.addDrawableChild(ButtonWidget(5, this.height - 25, 20, 20, Text.of("<-")) {
            this.close()
        })
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(matrices, mouseX, mouseY, delta)

        this.renderTitle(matrices, this.titleName)
    }

    class ScreenBetterForgeModListEntry(private val modStruct: ModStruct) : ScreenBetterSlotListEntry() {

        override fun renderEntry(matrices: MatrixStack, index: Int, entryWidth: Int, entryHeight: Int, mouseX: Int, mouseY: Int, hovered: Boolean) {
            matrices.push()
            matrices.scale(2F, 2F, 2F)
            RenderUtil.textCenter(matrices, this.modStruct.modId + " (" + this.modStruct.modVersion + ")", entryWidth.toFloat() / 2F, 1F, -1)
            matrices.pop()
        }
    }
}
