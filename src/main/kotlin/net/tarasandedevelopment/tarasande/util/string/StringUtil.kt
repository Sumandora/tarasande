package net.tarasandedevelopment.tarasande.util.string

import net.minecraft.client.MinecraftClient
import net.minecraft.client.resource.language.LanguageDefinition
import net.minecraft.client.resource.language.TranslationStorage
import net.tarasandedevelopment.tarasande.mixin.accessor.ILanguageManager
import java.util.*
import java.util.concurrent.TimeUnit

object StringUtil {

    private val languageCache = HashMap<LanguageDefinition, TranslationStorage>()

    fun uncoverTranslation(key: String, languageDefinition: LanguageDefinition = (MinecraftClient.getInstance().languageManager as ILanguageManager).tarasande_getEnglishUS()): String {
        return languageCache.computeIfAbsent(languageDefinition) { TranslationStorage.load(MinecraftClient.getInstance().resourceManager, Collections.singletonList(languageDefinition)) }.get(key)
    }

    fun formatEnumTypes(name: String) = (name.substring(0, 1) + name.substring(1).lowercase()).replace('_', ' ')

    fun round(input: Double, places: Int) = String.format("%." + places + "f", input)

    fun formatBytes(value: Long, count: Int): String {
        var value = value
        value *= 8L
        val bytes = value.toDouble()
        return if (value < 1024L)
            "$value B"
        else if (value < 1024L * 1024L)
            round(bytes / 1024.0, count) + " Kb"
        else if (value < 1024L * 1024L * 1024L)
            round(bytes / 1024.0 / 1024.0, count) + " Mb"
        else if (value < 1024L * 1024L * 1024L * 1024L)
            round(bytes / 1024.0 / 1024.0 / 1024.0, count) + " Gb"
        else
            round(bytes / 1024.0 / 1024.0 / 1024.0 / 1024.0, count) + " Tb"
    }

    fun formatTime(input: Long): String {
        var input = input

        val days = TimeUnit.MILLISECONDS.toDays(input)
        input -= TimeUnit.DAYS.toMillis(days)
        val hours = TimeUnit.MILLISECONDS.toHours(input)
        input -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(input)
        input -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(input)

        val builder = StringBuilder()

        if (seconds > 0) builder.append(seconds).append(" seconds")
        val thisSplit = ", "
        if (minutes > 0) {
            if (seconds != 0L) builder.append(thisSplit)
            builder.append(minutes).append(" minutes")
        }
        if (hours > 0) {
            builder.append(thisSplit)
            builder.append(hours).append(" hours")
        }
        if (days > 0) {
            builder.append(thisSplit)
            builder.append(days).append(" days")
        }
        return builder.toString()
    }
}
