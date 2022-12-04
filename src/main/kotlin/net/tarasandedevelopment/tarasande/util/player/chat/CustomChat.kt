package net.tarasandedevelopment.tarasande.util.player.chat

import net.minecraft.client.MinecraftClient
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.text.TextColor
import net.tarasandedevelopment.tarasande.TarasandeMain

object CustomChat {
    fun printChatMessage(message: MutableText) {
        val end = buildPrefix()
        end.append(message.styled { it.withColor(TextColor.fromRgb(-1)) })

        MinecraftClient.getInstance().inGameHud.chatHud.addMessage(end)
    }

    private fun buildPrefix(): MutableText {
        return Text.literal(TarasandeMain.get().name).styled { it.withColor(TextColor.fromRgb(TarasandeMain.clientValues().accentColor.getColor().rgb)) }.append(" ")
    }
}