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
