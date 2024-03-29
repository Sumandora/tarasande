package su.mandora.tarasande.event.impl

import net.minecraft.client.option.KeyBinding
import su.mandora.tarasande.event.Event

class EventKey(val key: Int, val action: Int) : Event(true)
class EventMouse(val button: Int, val action: Int) : Event(true)
class EventKeyBindingIsPressed : Event {
    var dirty = false
    val keyBinding: KeyBinding
    var pressed = false
        set(value) {
            field = value
            dirty = true
        }

    constructor(keyBinding: KeyBinding, pressed: Boolean) : super(false) {
        this.keyBinding = keyBinding
        this.pressed = pressed
        dirty = false
    }
}