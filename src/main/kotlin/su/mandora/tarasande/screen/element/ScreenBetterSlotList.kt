package su.mandora.tarasande.screen.element

import com.google.common.annotations.Beta
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.gui.widget.EntryListWidget
import net.minecraft.client.util.math.MatrixStack
import su.mandora.tarasande.util.math.TimeUtil
import su.mandora.tarasande.util.render.RenderUtil
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

    @Beta
    fun renderTitle(matrices: MatrixStack?, title: String) {
        matrices?.push()
        matrices?.scale(2F, 2F, 2F)
        RenderUtil.textCenter(matrices, title, width / 4F, 10F - textRenderer.fontHeight / 4)
        matrices?.pop()
    }

    override fun init() {
        super.init()
        var lastSelected: ScreenBetterSlotListEntry? = null
        if (this.slotList != null)
            lastSelected = this.slotList?.selectedOrNull

        this.addDrawableChild(ScreenBetterSlotListWidget(client!!, this.listProvider, width, height, top, height - bottom - top, entryHeight).also {
            this.slotList = it

            if (lastSelected != null)
                this.slotList!!.setSelected(lastSelected)
            else
                this.slotList!!.setSelected(this.listProvider.get()[0])
        })
    }
}

class ScreenBetterSlotListWidget(minecraft: MinecraftClient, private val listProvider: ListProvider, width: Int, height: Int, top: Int, bottom: Int, entryHeight: Int)
    : EntryListWidget<ScreenBetterSlotListEntry>(minecraft, width, height, top, bottom, entryHeight) {

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

    override fun appendNarrations(builder: NarrationMessageBuilder?) {
    }
}

open class ScreenBetterSlotListEntry : EntryListWidget.Entry<ScreenBetterSlotListEntry>() {
    var parentList: EntryListWidget<ScreenBetterSlotListEntry>? = null
    private var lastClick = TimeUtil()

    open fun isSelected(): Boolean {
        return false
    }
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
        if (this.isSelected())
            this.parentList?.setSelected(this)
        matrices.pop()
    }
}
