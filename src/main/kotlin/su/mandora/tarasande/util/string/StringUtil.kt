package su.mandora.tarasande.util.string

import net.minecraft.client.MinecraftClient
import net.minecraft.client.resource.language.LanguageDefinition
import net.minecraft.client.resource.language.TranslationStorage
import su.mandora.tarasande.mixin.accessor.ILanguageManager
import java.util.*

object StringUtil {

    private val languageCache = HashMap<LanguageDefinition, TranslationStorage>()

    fun uncoverTranslation(key: String, languageDefinition: LanguageDefinition = (MinecraftClient.getInstance().languageManager as ILanguageManager).tarasande_getEnglishUS()): String {
        return languageCache.computeIfAbsent(languageDefinition) { TranslationStorage.load(MinecraftClient.getInstance().resourceManager, Collections.singletonList(languageDefinition)) }.get(key)
    }

    fun formatEnumTypes(name: String) = (name.substring(0, 1) + name.substring(1).lowercase()).replace('_', ' ')

}