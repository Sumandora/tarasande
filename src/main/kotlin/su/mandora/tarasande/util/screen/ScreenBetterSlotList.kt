package su.mandora.tarasande.util.screen

import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.gui.screen.narration.NarrationPart
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget
import net.minecraft.text.Text
import su.mandora.tarasande.feature.screen.ScreenBetter
import su.mandora.tarasande.feature.tarasandevalue.impl.AccessibilityValues
import su.mandora.tarasande.mc
import su.mandora.tarasande.util.SCROLLBAR_WIDTH
import su.mandora.tarasande.util.extension.minecraft.render.widget.ButtonWidget
import su.mandora.tarasande.util.math.time.TimeUtil
import su.mandora.tarasande.util.render.font.FontWrapper

private typealias ListProvider = () -> List<EntryScreenBetterSlotList>
private typealias SlotListWidget = AlwaysSelectedEntryListWidgetScreenBetterSlotList // Yarn names are way too long for my taste
private typealias SlotListEntry = EntryScreenBetterSlotList

open class ScreenBetterSlotList(val title: String, prevScreen: Screen?, private val top: Int, private val bottom: Int) : ScreenBetter(title, prevScreen) {

    private lateinit var listProvider: ListProvider
    protected var slotList: SlotListWidget? = null
    var selected = 0

    fun provideElements(provider: ListProvider) {
        this.listProvider = provider
    }

    override fun init() {
        val entries = ArrayList<SlotListEntry>()
        for (entry in listProvider()) {
            entries.add(entry)
        }
        val entryWidth = entries.maxOfOrNull { it.width } ?: 1
        val entryHeight = entries.maxOfOrNull { it.height } ?: 1
        this.addDrawableChild(SlotListWidget(this, width, height, top, height - bottom - top, entryWidth, entryHeight).also {
            slotList = it
        })

        if (prevScreen != null) {
            this.addDrawableChild(ButtonWidget(3, height - 20 - 3, 20, 20, Text.literal("<-")) {
                mc.setScreen(prevScreen)
            })
        }

        reload()
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(context, mouseX, mouseY, delta)
        FontWrapper.textShadow(context, (this as Screen).title.string, width / 2F, top / 2 - (FontWrapper.fontHeight() / 2F), scale = 2F, centered = true)
    }

    fun reload() {
        if (slotList == null)
            return
        val slotList = slotList!!

        slotList.clearEntries() // Force garbage collection

        val entries = ArrayList<SlotListEntry>()
        for (entry in listProvider()) {
            entries.add(entry)
        }
        if (entries.isNotEmpty()) {
            val entryWidth = entries.maxOf { it.width }
            val entryHeight = entries.maxOf { it.height }

            entries.forEach { entry ->
                entry.parentList = slotList
                slotList.addEntry(entry)
            }

            slotList.entryWidth = entryWidth
            slotList.itemHeight = entryHeight
        } else {
            slotList.entryWidth = 1
            slotList.itemHeight = 1
        }

        if (slotList.scrollAmount > slotList.maxScroll)
            slotList.scrollAmount = slotList.maxScroll.toDouble()
    }
}

class AlwaysSelectedEntryListWidgetScreenBetterSlotList(val parent: ScreenBetterSlotList, width: Int, height: Int, top: Int, bottom: Int, var entryWidth: Int, entryHeight: Int)
    : AlwaysSelectedEntryListWidget<SlotListEntry>(mc, width, height, top, bottom, entryHeight) {

    override fun getRowWidth() = entryWidth

    override fun getScrollbarPositionX() = this.width / 2 + (this.entryWidth / 2) + SCROLLBAR_WIDTH

    override fun appendNarrations(builder: NarrationMessageBuilder?) {
        builder?.put(NarrationPart.TITLE, parent.title + " List")
    }
}

abstract class EntryScreenBetterSlotList(val width: Int, val height: Int, private val selectable: Boolean = true) : AlwaysSelectedEntryListWidget.Entry<SlotListEntry>() {
    lateinit var parentList: SlotListWidget
    private val clickTimer = TimeUtil()
    private var index = 0

    open fun isSelected(): Boolean {
        if (!selectable) {
            return false
        }
        return this.parentList.parent.selected == index
    }

    open fun renderEntry(context: DrawContext, index: Int, entryWidth: Int, entryHeight: Int, mouseX: Int, mouseY: Int, hovered: Boolean) {
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

    override fun render(context: DrawContext, index: Int, y: Int, x: Int, entryWidth: Int, entryHeight: Int, mouseX: Int, mouseY: Int, hovered: Boolean, tickDelta: Float) {
        this.index = index

        context.matrices.push()
        context.matrices.translate(x.toDouble(), y.toDouble(), 0.0)
        this.renderEntry(context, index, entryWidth, entryHeight, mouseX, mouseY, hovered)
        context.matrices.pop()

        if (this.isSelected())
            this.parentList.setSelected(this)
    }
}
