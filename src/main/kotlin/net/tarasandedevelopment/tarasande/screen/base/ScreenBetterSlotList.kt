package net.tarasandedevelopment.tarasande.screen.base

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget
import net.minecraft.client.gui.widget.EntryListWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.tarasandedevelopment.tarasande.util.extension.minecraft.ButtonWidget
import net.tarasandedevelopment.tarasande.util.math.TimeUtil
import net.tarasandedevelopment.tarasande.util.render.font.FontWrapper

open class ScreenBetterSlotList(title: String, prevScreen: Screen?, private val top: Int, private val bottom: Int, var entryWidth: Int, val entryHeight: Int) : ScreenBetter(title, prevScreen) {

    private var listProvider: AlwaysSelectedEntryListWidgetScreenBetterSlotListWidget.ListProvider? = null
    var slotList: AlwaysSelectedEntryListWidgetScreenBetterSlotListWidget? = null
    var selected: Int = 0

    fun provideElements(provider: AlwaysSelectedEntryListWidgetScreenBetterSlotListWidget.ListProvider) {
        this.listProvider = provider
    }

    override fun init() {
        if (this.listProvider == null) return

        this.addDrawableChild(AlwaysSelectedEntryListWidgetScreenBetterSlotListWidget(this, client!!, this.listProvider, width, height, top, height - bottom - top, entryWidth, entryHeight).also {
            this.slotList = it
            this.slotList?.reload()
        })

        if (prevScreen != null) {
            this.addDrawableChild(ButtonWidget(3, height - 20 - 3, 20, 20, Text.literal("<-")) {
                MinecraftClient.getInstance().setScreen(prevScreen)
            })
        }
        super.init()
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(matrices, mouseX, mouseY, delta)
        FontWrapper.textShadow(matrices, title.string, width / 2F, top / 2 - (FontWrapper.fontHeight() / 2F), scale = 2F, centered = true)
    }
}

class AlwaysSelectedEntryListWidgetScreenBetterSlotListWidget(val parent: ScreenBetterSlotList, minecraft: MinecraftClient, private val listProvider: ListProvider?, width: Int, height: Int, top: Int, bottom: Int, private val entryWidth: Int, entryHeight: Int)
    : AlwaysSelectedEntryListWidget<EntryScreenBetterSlotListEntry>(minecraft, width, height, top, bottom, entryHeight) {

    fun reload() {
        this.clearEntries()
        if (this.listProvider == null) return

        for (entry in this.listProvider.get()) {
            entry.parentList = this
            this.addEntry(entry)
        }
    }

    interface ListProvider {
        fun get(): List<EntryScreenBetterSlotListEntry>
    }

    override fun getRowWidth() = entryWidth
    override fun getScrollbarPositionX() = this.width / 2 + (this.entryWidth / 2) + 14

    override fun appendNarrations(builder: NarrationMessageBuilder?) {
    }
}

open class EntryScreenBetterSlotListEntry(private val selectable: Boolean = true) : AlwaysSelectedEntryListWidget.Entry<EntryScreenBetterSlotListEntry>() {
    var parentList: EntryListWidget<EntryScreenBetterSlotListEntry>? = null
    private val clickTimer = TimeUtil()
    private var index = 0

    open fun isSelected(): Boolean {
        if (!selectable) {
            return false
        }
        return (this.parentList!! as AlwaysSelectedEntryListWidgetScreenBetterSlotListWidget).parent.selected == index
    }

    open fun renderEntry(matrices: MatrixStack, index: Int, entryWidth: Int, entryHeight: Int, mouseX: Int, mouseY: Int, hovered: Boolean) {
    }

    open fun onDoubleClickEntry(mouseX: Double, mouseY: Double, mouseButton: Int) {
    }

    open fun onSingleClickEntry(mouseX: Double, mouseY: Double, mouseButton: Int) {
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (selectable) {
            (this.parentList!! as AlwaysSelectedEntryListWidgetScreenBetterSlotListWidget).parent.selected = this.index
            this.parentList!!.setSelected(this)
        }

        if (!clickTimer.hasReached(300)) {
            onDoubleClickEntry(mouseX, mouseY, button)
        } else {
            onSingleClickEntry(mouseX, mouseY, button)
        }
        clickTimer.reset()
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun render(matrices: MatrixStack, index: Int, y: Int, x: Int, entryWidth: Int, entryHeight: Int, mouseX: Int, mouseY: Int, hovered: Boolean, tickDelta: Float) {
        this.index = index

        matrices.push()
        matrices.translate(x.toDouble(), y.toDouble(), 0.0)
        this.renderEntry(matrices, index, entryWidth, entryHeight, mouseX, mouseY, hovered)
        matrices.pop()

        if (this.isSelected())
            this.parentList!!.setSelected(this)
    }

    override fun getNarration(): MutableText = Text.empty()
}
