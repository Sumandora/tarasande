package su.mandora.tarasande.event.impl

import net.minecraft.client.input.Input
import su.mandora.tarasande.event.Event
import su.mandora.tarasande.util.math.rotation.Rotation

class EventPollEvents : Event(false)

class EventRotation : Event {
    var dirty = false
        private set
    var rotation = Rotation(0F, 0F)
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