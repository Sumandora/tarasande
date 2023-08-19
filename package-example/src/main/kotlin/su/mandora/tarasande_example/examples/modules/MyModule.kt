package su.mandora.tarasande_example.examples.modules

import su.mandora.tarasande.event.impl.EventTick
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.util.player.chat.CustomChat

class MyModule : Module("My module", "My dearest module", "My category") {
    init {
        registerEvent(EventTick::class.java) {
            CustomChat.printChatMessage("I made my first module!")
        }
    }
}