package net.tarasandedevelopment.tarasande.event

import net.minecraft.client.input.Input
import net.tarasandedevelopment.tarasande.util.math.rotation.Rotation
import su.mandora.event.Event

class EventPollEvents : Event(false)

class EventRotation : Event {
    var dirty = false
        private set
    var rotation: Rotation
        set(value) {
            field = value
            dirty = true
        }

    constructor(rotation: Rotation) : super(false) {
        this.rotation = rotation
        this.dirty = false
    }
}

class EventVelocityYaw(var yaw: Float) : Event(false)
class EventInput(val input: Input, var movementForward: Float, var movementSideways: Float, var slowDown: Boolean, val slowdownAmount: Float) : Event(true)
class EventMouseDelta(var deltaX: Double, var deltaY: Double) : Event(false)
class EventRotationSet(val yaw: Float, val pitch: Float) : Event(false)