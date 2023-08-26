package su.mandora.tarasande.util.extension.minecraft

import net.minecraft.text.Text
import java.util.*

fun Text.extractContent(): String {
    val stringBuilder = StringBuilder()
    visit {
        stringBuilder.append(it)
        Optional.empty<Text>()
    }
    return stringBuilder.toString()
}