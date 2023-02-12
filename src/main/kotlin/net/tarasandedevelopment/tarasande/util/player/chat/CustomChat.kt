package net.tarasandedevelopment.tarasande.util.player.chat

import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.text.TextColor
import net.tarasandedevelopment.tarasande.TARASANDE_NAME
import net.tarasandedevelopment.tarasande.feature.tarasandevalue.TarasandeValues
import net.tarasandedevelopment.tarasande.mc

object CustomChat {
    fun printChatMessage(message: String) = printChatMessage(Text.literal(message))

    fun printChatMessage(message: MutableText) {
        val end = buildPrefix()
        end.append(message.styled { it.withColor(TextColor.fromRgb(-1)) })

        mc.inGameHud.chatHud.addMessage(end)
    }

    private fun buildPrefix(): MutableText {
        return Text.literal(TARASANDE_NAME).styled { it.withColor(TextColor.fromRgb(TarasandeValues.accentColor.getColor().rgb)) }.append(" ")
    }
}