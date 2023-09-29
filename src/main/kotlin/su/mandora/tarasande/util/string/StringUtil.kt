package su.mandora.tarasande.util.string

import net.minecraft.client.resource.language.LanguageDefinition
import net.minecraft.client.resource.language.LanguageManager
import net.minecraft.client.resource.language.TranslationStorage
import su.mandora.tarasande.mc
import su.mandora.tarasande.util.DEFAULT_LANGUAGE_CODE
import java.util.regex.Pattern

object StringUtil {

    private val languageCache = HashMap<LanguageDefinition, TranslationStorage>()

    fun uncoverTranslation(key: String, languageDefinition: LanguageDefinition = LanguageManager.ENGLISH_US, languageCode: String = DEFAULT_LANGUAGE_CODE): String {
        return languageCache.computeIfAbsent(languageDefinition) { TranslationStorage.load(mc.resourceManager, listOf(languageCode), languageDefinition.rightToLeft) }.get(key)
    }

    fun round(input: Double, places: Int) = ("%." + places + "f").format(input)

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

    val colorCodePattern: Pattern = Pattern.compile("(?i)\u00a7[0-9A-F]")

    fun stripColors(string: String): String {
        return colorCodePattern.matcher(string).replaceAll("")
    }

    fun camelCaseToTitleCase(string: String): String {
        val newString = StringBuilder()
        for ((index, c) in string.withIndex()) {
            when {
                index == 0 -> newString.append(c.uppercase())
                c.isUpperCase() -> newString.append(" ").append(c)
                else -> newString.append(c)
            }
        }
        return newString.toString()
    }

}
