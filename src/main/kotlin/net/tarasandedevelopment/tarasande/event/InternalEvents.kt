package net.tarasandedevelopment.tarasande.event

import net.minecraft.entity.Entity
import net.minecraft.text.Text
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import su.mandora.event.Event
import java.awt.Color

class EventIsEntityAttackable(val entity: Entity, var attackable: Boolean) : Event(false)
class EventTagName(var entity: Entity, var displayName: Text) : Event(false)
class EventGoalMovement : Event {
    var dirty = false
    var yaw: Float
        set(value) {
            field = value
            dirty = true
        }

    constructor(yaw: Float) : super(false) {
        this.yaw = yaw
        this.dirty = false
    }
}

class EventEntityColor(val entity: Entity, var color: Color?) : Event(false)
class EventSuccessfulLoad : Event(false)
class EventModuleStateSwitched(val module: Module, val oldState: Boolean, val newState: Boolean) : Event(false)