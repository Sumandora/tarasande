package net.tarasandedevelopment.tarasande.events

import net.minecraft.client.gui.Element
import net.minecraft.client.gui.screen.Screen
import net.minecraft.item.Item
import net.tarasandedevelopment.event.Event
import java.util.*

class EventTick(val state: State) : Event(false) {
    enum class State {
        PRE, POST
    }
}

class EventItemCooldown(val item: Item, var cooldown: Float) : Event(false)
class EventHandleBlockBreaking(var parameter: Boolean) : Event(false)
class EventUpdateTargetedEntity(val state: State) : Event(false) {
    enum class State {
        PRE, POST
    }
}

class EventSkipIdlePacket : Event(false)
class EventIsSaddled(var saddled: Boolean) : Event(false)
class EventInvalidGameMode(val uuid: UUID) : Event(false)
class EventScreenInput(var doneInput: Boolean) : Event(true)
class EventChangeScreen : Event {
    var dirty = false
    var newScreen: Screen?
        set(value) {
            field = value
            dirty = true
        }

    constructor(newScreen: Screen?) : super(true) {
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

class EventShowsDeathScreen(var showsDeathScreen: Boolean) : Event(false)
class EventTimeTravel(var time: Long) : Event(false)