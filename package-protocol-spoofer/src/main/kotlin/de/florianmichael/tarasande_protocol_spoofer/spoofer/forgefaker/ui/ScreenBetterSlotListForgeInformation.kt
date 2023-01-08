package de.florianmichael.tarasande_protocol_spoofer.spoofer.forgefaker.ui

import de.florianmichael.tarasande_protocol_spoofer.spoofer.forgefaker.payload.IForgePayload
import de.florianmichael.tarasande_protocol_spoofer.spoofer.forgefaker.payload.legacy.ModStruct
import de.florianmichael.tarasande_protocol_spoofer.spoofer.forgefaker.payload.modern.ChannelStruct
import de.florianmichael.tarasande_protocol_spoofer.spoofer.forgefaker.payload.modern.ModernForgePayload
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack
import net.tarasandedevelopment.tarasande.screen.base.AlwaysSelectedEntryListWidgetScreenBetterSlotListWidget
import net.tarasandedevelopment.tarasande.screen.base.EntryScreenBetterSlotListEntry
import net.tarasandedevelopment.tarasande.screen.base.ScreenBetterSlotList
import net.tarasandedevelopment.tarasande.util.render.font.FontWrapper

class ScreenBetterSlotListForgeInformation(parent: Screen, private val titleName: String, val type: Type, val struct: IForgePayload) : ScreenBetterSlotList(46, -10, -1, FontWrapper.fontHeight() * 2 + 5, parent) {

    enum class Type {
        MOD_LIST,
        CHANNEL_LIST
    }

    init {
        this.provideElements(object : AlwaysSelectedEntryListWidgetScreenBetterSlotListWidget.ListProvider {
            override fun get(): List<EntryScreenBetterSlotListEntryForgeList> {
                val list = when (type) {
                    Type.MOD_LIST -> struct.installedMods().map { m -> EntryScreenBetterSlotListEntryForgeListMods(m) }
                    Type.CHANNEL_LIST -> (struct as ModernForgePayload).channels.map { m -> EntryScreenBetterSlotListEntryForgeListChannels(m) }
                }
                list.forEach {
                    val width = (FontWrapper.getWidth(it.display()) * 2) + 5

                    if (entryWidth <= width) {
                        entryWidth = width
                    }
                }
                return list
            }
        })
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(matrices, mouseX, mouseY, delta)
        this.renderTitle(matrices, this.titleName)
    }

    abstract class EntryScreenBetterSlotListEntryForgeList : EntryScreenBetterSlotListEntry() {
        abstract fun display(): String

        override fun renderEntry(matrices: MatrixStack, index: Int, entryWidth: Int, entryHeight: Int, mouseX: Int, mouseY: Int, hovered: Boolean) {
            FontWrapper.text(matrices, this.display(), entryWidth.toFloat() / 4F, 1F, scale = 2F, centered = true)
        }
    }

    class EntryScreenBetterSlotListEntryForgeListMods(private val modStruct: ModStruct) : EntryScreenBetterSlotListEntryForgeList() {
        override fun display() = this.modStruct.modId + " (" + this.modStruct.modVersion + ")"
    }

    class EntryScreenBetterSlotListEntryForgeListChannels(private val channelStruct: ChannelStruct) : EntryScreenBetterSlotListEntryForgeList() {
        override fun display() = this.channelStruct.name + " (" + this.channelStruct.version + ")"
    }
}
