package net.tarasandedevelopment.tarasande.util.string

import net.minecraft.client.resource.language.LanguageDefinition
import net.minecraft.client.resource.language.LanguageManager
import net.minecraft.client.resource.language.TranslationStorage
import net.minecraft.text.Text
import net.tarasandedevelopment.tarasande.mc
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

object StringUtil {

    private val languageCache = HashMap<LanguageDefinition, TranslationStorage>()

    fun uncoverTranslation(key: String, languageDefinition: LanguageDefinition = LanguageManager.ENGLISH_US): String {
        return languageCache.computeIfAbsent(languageDefinition) { TranslationStorage.load(mc.resourceManager, listOf(languageDefinition.name), languageDefinition.rightToLeft) }.get(key)
    }

    fun formatEnumTypes(name: String) = (name.substring(0, 1) + name.substring(1).lowercase()).replace('_', ' ')

    fun round(input: Double, places: Int) = String.format(Locale.ROOT, "%." + places + "f", input)

    fun formatBytes(value: Long, count: Int): String {
        return if (value < 1024L)
            "$value B"
        else if (value < 1024L * 1024L)
            round(value / 1024.0, count) + " Kb"
        else if (value < 1024L * 1024L * 1024L)
            round(value / 1024.0 / 1024.0, count) + " Mb"
        else if (value < 1024L * 1024L * 1024L * 1024L)
            round(value / 1024.0 / 1024.0 / 1024.0, count) + " Gb"
        else
            round(value / 1024.0 / 1024.0 / 1024.0 / 1024.0, count) + " Tb"
    }

    fun formatTime(input: Long): String {
        @Suppress("NAME_SHADOWING")
        var input = input

        val days = TimeUnit.MILLISECONDS.toDays(input)
        input -= TimeUnit.DAYS.toMillis(days)
        val hours = TimeUnit.MILLISECONDS.toHours(input)
        input -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(input)
        input -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(input)

        return StringBuilder().apply {
            if (days > 0) {
                append(days).append(" days")
            }
            if (hours > 0) {
                if (isNotEmpty()) append(", ")
                append(hours).append(" hours")
            }
            if (minutes > 0) {
                if (isNotEmpty()) append(", ")
                append(minutes).append(" minutes")
            }
            if (seconds > 0) {
                if (isNotEmpty()) append(", ")
                append(seconds).append(" seconds")
            }
        }.toString()
    }

    fun extractContent(text: Text): String {
        var str = ""
        text.visit {
            str += it
            Optional.of(it)
        }
        return str
    }

    private val colorCodePattern = Pattern.compile("(?i)\u00a7[0-9A-F]")

    fun stripColors(string: String): String {
        return colorCodePattern.matcher(string).replaceAll("")
    }

    fun camelCaseToTitleCase(string: String): String {
        var newString = ""
        var newWord = true
        for ((index, c) in string.withIndex()) {
            if(c.isUpperCase())
                newWord = true
            newString +=
                when {
                    index == 0 -> c.uppercase()
                    newWord -> " " + c.uppercase()
                    else -> c.lowercase()
                }
            newWord = false
        }
        return newString
    }

}
