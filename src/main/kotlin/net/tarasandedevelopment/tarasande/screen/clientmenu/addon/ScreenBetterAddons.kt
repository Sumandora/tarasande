package net.tarasandedevelopment.tarasande.screen.clientmenu.addon

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.addon.Addon
import net.tarasandedevelopment.tarasande.screen.base.ScreenBetterParentPopupSettings
import net.tarasandedevelopment.tarasande.screen.base.ScreenBetterSlotList
import net.tarasandedevelopment.tarasande.screen.base.ScreenBetterSlotListEntry
import net.tarasandedevelopment.tarasande.screen.base.ScreenBetterSlotListWidget
import net.tarasandedevelopment.tarasande.util.render.RenderUtil

class ScreenBetterAddons : ScreenBetterSlotList(46, MinecraftClient.getInstance().textRenderer.fontHeight * 2 + 5) {

    private val list = TarasandeMain.get().managerAddon.list.map { p -> EntryAddon(p) }

    init {
        this.provideElements(object : ScreenBetterSlotListWidget.ListProvider {
            override fun get(): List<ScreenBetterSlotListEntry> {
                return list
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

        this.renderTitle(matrices, "Addons")

        if (this.list.isNotEmpty() && this.list.size >= this.selected)
            RenderUtil.textCenter(matrices, "Developed by: " + this.list[this.selected].addon.modAuthors!!.joinToString(", "), this.width / 2F, height - 17F, -1)
    }

    class EntryAddon(val addon: Addon) : ScreenBetterSlotListEntry() {

        override fun onDoubleClickEntry(mouseX: Double, mouseY: Double, mouseButton: Int) {
            super.onDoubleClickEntry(mouseX, mouseY, mouseButton)

            if (TarasandeMain.get().managerValue.getValues(addon).isNotEmpty())
                MinecraftClient.getInstance().setScreen(ScreenBetterParentPopupSettings(MinecraftClient.getInstance().currentScreen!!, addon.modId!!, addon))
        }

        override fun renderEntry(matrices: MatrixStack, index: Int, entryWidth: Int, entryHeight: Int, mouseX: Int, mouseY: Int, hovered: Boolean) {
            matrices.push()
            matrices.scale(2F, 2F, 2F)
            RenderUtil.textCenter(matrices, this.addon.modId!! + " (" + this.addon.modVersion!! + ")", entryWidth.toFloat() / 2F, 1F, -1)
            matrices.pop()
        }
    }
}