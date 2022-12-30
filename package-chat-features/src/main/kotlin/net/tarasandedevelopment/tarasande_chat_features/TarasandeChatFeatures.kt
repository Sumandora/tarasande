package net.tarasandedevelopment.tarasande_chat_features

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.event.EventSuccessfulLoad
import net.tarasandedevelopment.tarasande_chat_features.module.*
import su.mandora.event.EventDispatcher

const val CATEGORY_CHAT = "Chat"

class TarasandeChatFeatures : ClientModInitializer {

    companion object {
        val tarasandeProtocolHackLoaded = FabricLoader.getInstance().isModLoaded("tarasande-protocol-hack")
    }

    override fun onInitializeClient() {
        EventDispatcher.add(EventSuccessfulLoad::class.java) {
            TarasandeMain.managerModule().add(
                    ModuleNoChatContext(),
                    ModulePublicKeyKicker(),
                    ModulePrivateMsgDetector(),
                    ModuleSpammer(),
                    ModuleAllowAllCharacters()
            )
        }
    }
}
