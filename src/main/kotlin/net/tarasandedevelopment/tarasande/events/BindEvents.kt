package net.tarasandedevelopment.tarasande.events

import net.minecraft.client.option.KeyBinding
import net.tarasandedevelopment.event.Event

class EventKey(val key: Int, val action: Int) : Event(true)
class EventMouse(val button: Int, val action: Int) : Event(true)
class EventKeyBindingIsPressed(val keyBinding: KeyBinding, var pressed: Boolean) : Event(false)