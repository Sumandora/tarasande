package net.tarasandedevelopment.tarasande.util.chat

import net.minecraft.client.MinecraftClient
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.text.TextColor
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.module.Module

object CustomChat {

    fun print(module: Module, message: Text) {
        val end = buildPrefix()

        end.append(module.name)
        end.append(": ")
        end.append(message)

        MinecraftClient.getInstance().inGameHud.chatHud.addMessage(message)
    }

    private fun buildPrefix(): MutableText {
        return Text.literal(TarasandeMain.get().name).styled { it.withColor(TextColor.fromRgb(TarasandeMain.get().clientValues.accentColor.getColor().rgb)) }.append(" ")
    }
}