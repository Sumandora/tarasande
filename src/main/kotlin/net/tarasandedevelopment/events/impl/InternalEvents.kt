package net.tarasandedevelopment.events.impl

import net.minecraft.entity.Entity
import net.minecraft.text.Text
import net.tarasandedevelopment.events.Event
import java.awt.Color

class EventIsEntityAttackable(val entity: Entity, var attackable: Boolean) : Event(false)
class EventTagName(var entity: Entity, var displayName: Text) : Event(false)
class EventGoalMovement : Event {
    var dirty = false
    var yaw: Float
        set(value) {
            dirty = true
            field = value
        }

    constructor(yaw: Float) : super(false) {
        this.yaw = yaw
        this.dirty = false
    }
}

class EventEntityColor(val entity: Entity, var color: Color?) : Event(false)