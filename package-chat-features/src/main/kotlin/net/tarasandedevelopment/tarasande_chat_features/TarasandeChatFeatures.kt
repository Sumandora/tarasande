package net.tarasandedevelopment.tarasande_chat_features

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.event.EventSuccessfulLoad
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.meta.abstracted.ValueButtonOwnerValues
import net.tarasandedevelopment.tarasande_chat_features.clientvalue.ChatValues
import net.tarasandedevelopment.tarasande_chat_features.module.ModuleNoChatContext
import net.tarasandedevelopment.tarasande_chat_features.module.ModulePrivateMsgDetector
import net.tarasandedevelopment.tarasande_chat_features.module.ModulePublicKeyKicker
import net.tarasandedevelopment.tarasande_chat_features.module.ModuleSpammer
import su.mandora.event.EventDispatcher

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
                    ModuleSpammer()
            )

            ValueButtonOwnerValues(TarasandeMain.clientValues(), "Chat Values", ChatValues)
        }
    }
}
