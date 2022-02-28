package su.mandora.tarasande.util.string

import net.minecraft.client.MinecraftClient
import net.minecraft.client.resource.language.LanguageDefinition
import net.minecraft.client.resource.language.TranslationStorage
import su.mandora.tarasande.mixin.accessor.ILanguageManager
import java.util.*

object StringUtil {

    fun uncoverTranslation(key: String, languageDefinition: LanguageDefinition = (MinecraftClient.getInstance().languageManager as ILanguageManager).englishUS) = TranslationStorage.load(MinecraftClient.getInstance().resourceManager, Collections.singletonList(languageDefinition)).get(key)

}