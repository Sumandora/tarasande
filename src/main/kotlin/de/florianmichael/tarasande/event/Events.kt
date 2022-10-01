package de.florianmichael.tarasande.event

import net.minecraft.client.gui.Element
import net.minecraft.client.gui.screen.Screen
import net.minecraft.util.hit.HitResult
import su.mandora.tarasande.base.event.Event

class EventChangeScreen : Event {
    var dirty = false
    var newScreen: Screen?
        set(value) {
            field = value
            dirty = true
        }

    constructor(newScreen: Screen?) : super(false) {
        this.newScreen = newScreen
        dirty = false
    }
}

class EventChildren(val screen: Screen) : Event(false) {
    private val children = ArrayList<Element>()

    fun add(element: Element) {
        children.add(element)
    }

    fun get() = children
}

class EventChatAcknowledge(var acknowledged: Boolean) : Event(false)
class EventCommandBlockUsage(var allowed: Boolean) : Event(false)
class EventBindingEnchantment(var present: Boolean) : Event(false)
class EventEntityStatusGUI(val type: Type, var state: Boolean) : Event(false) {

    enum class Type {
        ICON, PARTICLES
    }
}

class EventEntityRaycast(val hitResult: HitResult?) : Event(true)