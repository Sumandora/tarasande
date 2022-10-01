package de.florianmichael.tarasande.event

import net.minecraft.client.gui.Element
import net.minecraft.client.gui.screen.Screen
import su.mandora.tarasande.base.event.Event

class EventChangeScreen(var newScreen: Screen?) : Event(true)
class EventChildren(val screen: Screen) : Event(false) {
    private val children = ArrayList<Element>()

    fun add(element: Element) {
        children.add(element)
    }

    fun get() = children
}
class EventChatAcknowledge : Event(true)
class EventCommandBlockUsage : Event(true)
class EventBindingEnchantment : Event(true)
class EventEntityStatusGUI(val type: Type, var state: Boolean) : Event(false) {

    enum class Type {
        ICON, PARTICLES
    }
}
