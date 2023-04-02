package su.mandora.tarasande_protocol_spoofer.tarasandevalues.forge.ui

import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import su.mandora.tarasande.util.render.font.FontWrapper
import su.mandora.tarasande.util.screen.EntryScreenBetterSlotList
import su.mandora.tarasande.util.screen.ScreenBetterSlotList
import su.mandora.tarasande_protocol_spoofer.tarasandevalues.forge.payload.IForgePayload
import su.mandora.tarasande_protocol_spoofer.tarasandevalues.forge.payload.legacy.ModStruct
import su.mandora.tarasande_protocol_spoofer.tarasandevalues.forge.payload.modern.ChannelStruct
import su.mandora.tarasande_protocol_spoofer.tarasandevalues.forge.payload.modern.ModernForgePayload

class ScreenBetterSlotListForgeInformation(title: String, parent: Screen, private val type: Type, private val struct: IForgePayload) : ScreenBetterSlotList(title, parent, 46, -46) {

    enum class Type {
        MOD_LIST,
        CHANNEL_LIST
    }

    init {
        this.provideElements {
            when (type) {
                Type.MOD_LIST -> struct.installedMods().map { m -> EntryScreenBetterSlotListStringMod(m) }
                Type.CHANNEL_LIST -> (struct as ModernForgePayload).channels.map { m -> EntryScreenBetterSlotListStringChannel(m) }
            }
        }
    }


    open class EntryScreenBetterSlotListString(private val text: String) : EntryScreenBetterSlotList((FontWrapper.getWidth(text) + 5) * 2, FontWrapper.fontHeight() * 2) {
        override fun getNarration(): Text {
            return Text.of(text)
        }

        override fun renderEntry(matrices: MatrixStack, index: Int, entryWidth: Int, entryHeight: Int, mouseX: Int, mouseY: Int, hovered: Boolean) {
            FontWrapper.text(matrices, text, entryWidth / 2F, entryHeight / 2F - FontWrapper.fontHeight() /* Scale cancels the division out */, scale = 2F, centered = true)
        }
    }

    class EntryScreenBetterSlotListStringMod(modStruct: ModStruct) : EntryScreenBetterSlotListString(modStruct.modId + " (" + modStruct.modVersion + ")")
    class EntryScreenBetterSlotListStringChannel(channelStruct: ChannelStruct) : EntryScreenBetterSlotListString(channelStruct.name + " (" + channelStruct.version + ")")
}
