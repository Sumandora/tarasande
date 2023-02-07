package net.tarasandedevelopment.tarasande.util.screen

import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.gui.screen.narration.NarrationPart
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.tarasandedevelopment.tarasande.feature.clientvalue.impl.AccessibilityValues
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.grabbersystem.ManagerGrabber
import net.tarasandedevelopment.tarasande.system.base.grabbersystem.impl.GrabberScrollbarWidth
import net.tarasandedevelopment.tarasande.util.extension.minecraft.ButtonWidget
import net.tarasandedevelopment.tarasande.util.math.TimeUtil
import net.tarasandedevelopment.tarasande.util.render.font.FontWrapper

private val scrollbarWidth by lazy { ManagerGrabber.getConstant(GrabberScrollbarWidth::class.java) as Int }

private typealias ListProvider = () -> List<EntryScreenBetterSlotList>
private typealias SlotListWidget = AlwaysSelectedEntryListWidgetScreenBetterSlotList // Yarn names are way too long for my taste
private typealias SlotListEntry = EntryScreenBetterSlotList

open class ScreenBetterSlotList(val title: String, prevScreen: Screen?, private val top: Int, private val bottom: Int) : ScreenBetter(title, prevScreen) {

    private var listProvider: ListProvider? = null
    protected var slotList: SlotListWidget? = null
    var selected: Int = 0

    fun provideElements(provider: ListProvider) {
        this.listProvider = provider
    }

    override fun init() {
        if (prevScreen != null) {
            this.addDrawableChild(ButtonWidget(3, height - 20 - 3, 20, 20, Text.literal("<-")) {
                mc.setScreen(prevScreen)
            })
        }

        val entries = ArrayList<SlotListEntry>()
        for (entry in listProvider!!()) {
            entries.add(entry)
        }
        val entryWidth = entries.maxOf { it.width }
        val entryHeight = entries.maxOf { it.height }
        this.addDrawableChild(SlotListWidget(this, width, height, top, height - bottom - top, entryWidth, entryHeight).also {
            slotList = it
        })

        reload()
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(matrices, mouseX, mouseY, delta)
        FontWrapper.textShadow(matrices, title.string, width / 2F, top / 2 - (FontWrapper.fontHeight() / 2F), scale = 2F, centered = true)
    }

    fun reload() {
        if(slotList == null || listProvider == null)
            return
        val slotList = slotList!!

        slotList.clearEntries() // Force garbage collection

        val entries = ArrayList<SlotListEntry>()
        for (entry in listProvider!!()) {
            entries.add(entry)
        }
        val entryWidth = entries.maxOf { it.width }
        val entryHeight = entries.maxOf { it.height }

        entries.forEach { entry ->
            entry.parentList = slotList
            slotList.addEntry(entry)
        }

        slotList.entryWidth = entryWidth
        slotList.itemHeight = entryHeight
        if(slotList.scrollAmount > slotList.maxScroll)
            slotList.scrollAmount = slotList.maxScroll.toDouble()
    }
}

class AlwaysSelectedEntryListWidgetScreenBetterSlotList(val parent: ScreenBetterSlotList, width: Int, height: Int, top: Int, bottom: Int, var entryWidth: Int, entryHeight: Int)
    : AlwaysSelectedEntryListWidget<SlotListEntry>(mc, width, height, top, bottom, entryHeight) {

    override fun getRowWidth() = entryWidth

    override fun getScrollbarPositionX() = this.width / 2 + (this.entryWidth / 2) + scrollbarWidth

    override fun appendNarrations(builder: NarrationMessageBuilder?) {
        builder?.put(NarrationPart.TITLE, parent.title + " List")
    }
}

abstract class EntryScreenBetterSlotList(val width: Int, val height: Int, private val selectable: Boolean = true) : AlwaysSelectedEntryListWidget.Entry<SlotListEntry>() {
    lateinit var parentList: SlotListWidget
    private val clickTimer = TimeUtil()
    private var index: Int = 0

    open fun isSelected(): Boolean {
        if (!selectable) {
            return false
        }
        return this.parentList.parent.selected == index
    }

    open fun renderEntry(matrices: MatrixStack, index: Int, entryWidth: Int, entryHeight: Int, mouseX: Int, mouseY: Int, hovered: Boolean) {
    }

    open fun onDoubleClickEntry(mouseX: Double, mouseY: Double, mouseButton: Int) {
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (selectable) {
            this.parentList.apply {
                parent.selected = index
                parentList.setSelected(this@EntryScreenBetterSlotList)
            }
        }

        if (!clickTimer.hasReached(AccessibilityValues.doubleClickDelay.value.toLong())) {
            onDoubleClickEntry(mouseX, mouseY, button)
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
            this.parentList.setSelected(this)
    }
}
