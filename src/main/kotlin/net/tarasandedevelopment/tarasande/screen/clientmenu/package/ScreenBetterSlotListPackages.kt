package net.tarasandedevelopment.tarasande.screen.clientmenu.`package`

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.`package`.Package
import net.tarasandedevelopment.tarasande.screen.base.ScreenBetterParentPopupSettings
import net.tarasandedevelopment.tarasande.screen.base.ScreenBetterSlotList
import net.tarasandedevelopment.tarasande.screen.base.ScreenBetterSlotListEntry
import net.tarasandedevelopment.tarasande.screen.base.ScreenBetterSlotListWidget
import net.tarasandedevelopment.tarasande.util.render.RenderUtil

class ScreenBetterSlotListPackages : ScreenBetterSlotList(46, -1, FontWrapper.fontHeight() * 2 + 5) {

    private val list = TarasandeMain.get().managerPackage.list.map { p -> ScreenBetterSlotListEntryPackage(p) }

    init {
        this.provideElements(object : ScreenBetterSlotListWidget.ListProvider {
            override fun get(): List<ScreenBetterSlotListEntry> {
                return list
            }
        })
    }

    override fun init() {
        list.forEach {
            val width = (FontWrapper.getWidth(it.text()) * 2) + 5

            if (entryWidth <= width) {
                entryWidth = width
            }
        }
        super.init()

        if (TarasandeMain.get().clientValues.clientMenuBackButtons.value) {
            addDrawableChild(ButtonWidget(5, this.height - 25, 20, 20, Text.of("<-")) { RenderSystem.recordRenderCall { close() } })
        }
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(matrices, mouseX, mouseY, delta)

        this.renderTitle(matrices, "Packages")

        if (this.list.isNotEmpty() && this.list.size >= this.selected)
            FontWrapper.text(matrices, "Developed by: " + this.list[this.selected].`package`.modAuthors!!.joinToString(", "), this.width / 2F, height - 17F, centered = true)
    }

    class ScreenBetterSlotListEntryPackage(val `package`: Package) : ScreenBetterSlotListEntry() {

        override fun onDoubleClickEntry(mouseX: Double, mouseY: Double, mouseButton: Int) {
            super.onDoubleClickEntry(mouseX, mouseY, mouseButton)

            if (TarasandeMain.get().valueSystem.getValues(`package`).isNotEmpty())
                MinecraftClient.getInstance().setScreen(ScreenBetterParentPopupSettings(MinecraftClient.getInstance().currentScreen!!, `package`.modId!!, `package`))
        }

        fun text() = this.`package`.modId!! + " (" + this.`package`.modVersion!! + ")"

        override fun renderEntry(matrices: MatrixStack, index: Int, entryWidth: Int, entryHeight: Int, mouseX: Int, mouseY: Int, hovered: Boolean) {
            FontWrapper.textShadow(matrices, text(), entryWidth / 2.0f, entryHeight / 2.0f - FontWrapper.fontHeight(), -1, scale = 2.0f, centered = true)
        }
    }
}