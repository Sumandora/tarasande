package net.tarasandedevelopment.tarasande.screen.element

import com.google.common.annotations.Beta
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget
import net.minecraft.client.gui.widget.EntryListWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.tarasandedevelopment.tarasande.screen.ScreenBetter
import net.tarasandedevelopment.tarasande.util.render.RenderUtil

open class ScreenBetterSlotList(private val top: Int, private val bottom: Int, private val entryHeight: Int) : ScreenBetter(null) {

    var slotList: ScreenBetterSlotListWidget? = null
    var listProvider: ScreenBetterSlotListWidget.ListProvider? = null
    var selected: Int = 0

    constructor(top: Int, entryHeight: Int) : this(top, -10, entryHeight)

    fun provideElements(provider: ScreenBetterSlotListWidget.ListProvider) {
        this.listProvider = provider
    }

    @Beta
    fun renderTitle(matrices: MatrixStack?, title: String) {
        matrices?.push()
        matrices?.scale(2F, 2F, 2F)
        RenderUtil.textCenter(matrices, title, width / 4F, 10F - textRenderer.fontHeight / 4)
        matrices?.pop()
    }

    override fun init() {
        if (this.listProvider == null) return

        this.addDrawableChild(ScreenBetterSlotListWidget(this, client!!, this.listProvider, width, height, top, height - bottom - top, entryHeight).also {
            this.slotList = it
            this.slotList?.reload()
        })

        super.init()
    }
}

class ScreenBetterSlotListWidget(val parent: ScreenBetterSlotList, minecraft: MinecraftClient, private val listProvider: ListProvider?, width: Int, height: Int, top: Int, bottom: Int, entryHeight: Int)
    : AlwaysSelectedEntryListWidget<ScreenBetterSlotListEntry>(minecraft, width, height, top, bottom, entryHeight) {

    fun reload() {
        this.clearEntries()
        if (this.listProvider == null) return

        for (entry in this.listProvider.get()) {
            entry.parentList = this
            this.addEntry(entry)
        }
    }

    override fun getScrollbarPositionX() = width - 6 // sick hardcoded value, thx mojang

    interface ListProvider {
        fun get(): List<ScreenBetterSlotListEntry>
    }

    override fun appendNarrations(builder: NarrationMessageBuilder?) {
    }
}

open class ScreenBetterSlotListEntry : AlwaysSelectedEntryListWidget.Entry<ScreenBetterSlotListEntry>() {
    var parentList: EntryListWidget<ScreenBetterSlotListEntry>? = null
    private var lastClick: Long = 0
    private var index = 0

    fun isSelected() = (this.parentList!! as ScreenBetterSlotListWidget).parent.selected == index

    open fun renderEntry(matrices: MatrixStack, index: Int, entryWidth: Int, entryHeight: Int, mouseX: Int, mouseY: Int, hovered: Boolean) {
    }

    open fun onDoubleClickEntry(mouseX: Double, mouseY: Double, mouseButton: Int) {
    }
    open fun onSingleClickEntry(mouseX: Double, mouseY: Double, mouseButton: Int) {
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        (this.parentList!! as ScreenBetterSlotListWidget).parent.selected = this.index

        this.onSingleClickEntry(mouseX, mouseY, button)
        if (System.currentTimeMillis() - lastClick < 300) {
            onDoubleClickEntry(mouseX, mouseY, button)
        }
        lastClick = System.currentTimeMillis()
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun render(matrices: MatrixStack?, index: Int, y: Int, x: Int, entryWidth: Int, entryHeight: Int, mouseX: Int, mouseY: Int, hovered: Boolean, tickDelta: Float) {
        this.index = index

        matrices?.push()
        matrices?.translate(x.toDouble(), y.toDouble(), 0.0)
        this.renderEntry(matrices!!, index, entryWidth / 2, entryHeight, mouseX, mouseY, hovered)
        matrices.pop()

        if (this.isSelected())
            this.parentList!!.setSelected(this)
    }

    override fun getNarration() = Text.empty()
}
