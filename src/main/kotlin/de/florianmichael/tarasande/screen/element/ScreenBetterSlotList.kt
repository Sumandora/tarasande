package de.florianmichael.tarasande.screen.element

import com.google.common.annotations.Beta
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import su.mandora.tarasande.util.math.TimeUtil
import su.mandora.tarasande.util.render.screen.ScreenBetter

open class ScreenBetterSlotList(parent: Screen, private val top: Int, private val bottom: Int, private val entryHeight: Int, private var listProvider: ScreenBetterSlotListWidget.ListProvider) : ScreenBetter(parent) {

    private var slotList: ScreenBetterSlotListWidget? = null

    constructor(parent: Screen, top: Int, entryHeight: Int, listProvider: ScreenBetterSlotListWidget.ListProvider) : this(parent, top, -10, entryHeight, listProvider)

    @Beta
    fun replaceList(provider: ScreenBetterSlotListWidget.ListProvider) {
        this.listProvider = provider

        if (this.slotList != null)
            this.slotList?.reload()
    }

    override fun init() {
        super.init()

        this.addDrawableChild(ScreenBetterSlotListWidget(client!!, this.listProvider, width, height, top, height - bottom, entryHeight).also { this.slotList = it })
    }
}

class ScreenBetterSlotListWidget(minecraft: MinecraftClient, private val listProvider: ListProvider, width: Int, height: Int, top: Int, bottom: Int, entryHeight: Int)
    : AlwaysSelectedEntryListWidget<ScreenBetterSlotListEntry>(minecraft, width, height, top, bottom, entryHeight) {

    init {
        this.reload()
    }

    fun reload() {
        this.clearEntries()

        for (entry in this.listProvider.get()) {
            entry.parentList = this
            this.addEntry(entry)
        }
    }

    interface ListProvider {
        fun get(): List<ScreenBetterSlotListEntry>
    }
}

open class ScreenBetterSlotListEntry : AlwaysSelectedEntryListWidget.Entry<ScreenBetterSlotListEntry>() {
    var parentList: AlwaysSelectedEntryListWidget<ScreenBetterSlotListEntry>? = null
    private var lastClick = TimeUtil()

    open fun renderEntry(matrices: MatrixStack, index: Int, entryWidth: Int, entryHeight: Int, mouseX: Int, mouseY: Int, hovered: Boolean) {
    }

    open fun onClickEntry(mouseX: Double, mouseY: Double, mouseButton: Int): Boolean {
        return true
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (this.lastClick.hasReached(300L)) {
            val clicked = this.onClickEntry(mouseX, mouseY, button)
            this.parentList?.setSelected(this)
            this.lastClick.reset()
            return clicked
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun render(matrices: MatrixStack?, index: Int, y: Int, x: Int, entryWidth: Int, entryHeight: Int, mouseX: Int, mouseY: Int, hovered: Boolean, tickDelta: Float) {
        matrices?.push()
        matrices?.translate(x.toDouble(), y.toDouble(), 0.0)
        this.renderEntry(matrices!!, index, entryWidth / 2, entryHeight, mouseX, mouseY, hovered)
        matrices.pop()
    }

    override fun getNarration(): MutableText = Text.empty()
}
