package net.tarasandedevelopment.tarasande_chat_features

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.tarasandedevelopment.tarasande.event.EventSuccessfulLoad
import net.tarasandedevelopment.tarasande.feature.clientvalue.ClientValues
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.meta.abstracted.ValueButtonOwnerValues
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ManagerModule
import net.tarasandedevelopment.tarasande_chat_features.clientvalue.ChatValues
import net.tarasandedevelopment.tarasande_chat_features.feature.commandsystem.ManagerCommand
import net.tarasandedevelopment.tarasande_chat_features.feature.module.ModuleNoChatContext
import net.tarasandedevelopment.tarasande_chat_features.feature.module.ModulePrivateMsgDetector
import net.tarasandedevelopment.tarasande_chat_features.feature.module.ModulePublicKeyKicker
import net.tarasandedevelopment.tarasande_chat_features.feature.module.ModuleSpammer
import su.mandora.event.EventDispatcher

class TarasandeChatFeatures : ClientModInitializer {

    companion object {
        val tarasandeProtocolHackLoaded = FabricLoader.getInstance().isModLoaded("tarasande-protocol-hack")
    }

    override fun onInitializeClient() {
        EventDispatcher.add(EventSuccessfulLoad::class.java) {
            ManagerModule.add(
                    ModuleNoChatContext(),
                    ModulePublicKeyKicker(),
                    ModulePrivateMsgDetector(),
                    ModuleSpammer()
            )

            ManagerCommand

            ValueButtonOwnerValues(ClientValues, "Chat values", ChatValues)
        }
    }
}
