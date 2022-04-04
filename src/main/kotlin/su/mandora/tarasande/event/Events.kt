package su.mandora.tarasande.event

import net.minecraft.client.option.KeyBinding
import net.minecraft.client.render.Camera
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.Entity
import net.minecraft.item.Item
import net.minecraft.network.Packet
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.math.Matrix4f
import net.minecraft.util.math.Vec3d
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.util.math.rotation.Rotation
import java.awt.Color

class EventChat(val chatMessage: String) : Event(true)
class EventKey(val key: Int, val action: Int) : Event(true)
class EventUpdate(val state: State) : Event(false) {
    enum class State {
        PRE, PRE_PACKET, POST
    }
}

class EventTick(val state: State) : Event(false) {
    enum class State {
        PRE, POST
    }
}

class EventResolutionUpdate(val width: Float, val height: Float) : Event(false)
class EventRender2D(val matrices: MatrixStack) : Event(false)
class EventScreenRender(val matrices: MatrixStack) : Event(false)
class EventRender3D(val matrices: MatrixStack, val positionMatrix: Matrix4f) : Event(false)

class EventRenderEntity(val entity: Entity, val state: State) : Event(false) {
    enum class State {
        PRE, POST
    }
}

class EventPacket(val type: Type, val packet: Packet<*>?) : Event(true) {
    enum class Type {
        SEND, RECEIVE
    }
}

class EventPollEvents : Event {
    var rotation: Rotation
        set(value) {
            dirty = true
            field = value
        }
    var dirty = false
    var minRotateToOriginSpeed = 1.0
    var maxRotateToOriginSpeed = 1.0

    constructor(rotation: Rotation) : super(false) {
        this.rotation = rotation
        this.dirty = false
    }
}

class EventVelocityYaw(var yaw: Float) : Event(false)
class EventKeyBindingIsPressed(val keyBinding: KeyBinding, var pressed: Boolean) : Event(false)

class EventVelocity(var velocityX: Double, var velocityY: Double, var velocityZ: Double, val packet: Packet) : Event(true) {
    enum class Packet {
        VELOCITY, EXPLOSION
    }
}

class EventInput(var movementForward: Float, var movementSideways: Float) : Event(false)
class EventJump(var yaw: Float, val state: State) : Event(false) {
    enum class State {
        PRE, POST
    }
}
class EventGamma(var gamma: Double) : Event(false)
class EventMovement(val entity: Entity, var velocity: Vec3d) : Event(false)
class EventSlowdown(var usingItem: Boolean) : Event(false)
class EventSlowdownAmount(var slowdownAmount: Float) : Event(false)
class EventIsEntityAttackable(val entity: Entity?, var attackable: Boolean) : Event(false)

class EventVanillaFlight : Event {
    var dirty = false
    var flying: Boolean
        set(value) {
            dirty = true
            field = value
        }
    var flightSpeed: Float

    constructor(flying: Boolean, flightSpeed: Float) : super(false) {
        this.flying = flying
        this.flightSpeed = flightSpeed
        this.dirty = false
    }

}

class EventMouse(val button: Int, val action: Int) : Event(true)
class EventMouseDelta(var deltaX: Double, var deltaY: Double) : Event(false)
class EventTimeTravel(var time: Long) : Event(false)
class EventItemCooldown(val item: Item, var cooldown: Float) : Event(false)

class EventAttackEntity(val entity: Entity, val state: State) : Event(false) {
    enum class State {
        PRE, POST
    }
}

class EventMovementFovMultiplier(var movementFovMultiplier: Float) : Event(false)
class EventKeepSprint(var sprinting: Boolean) : Event(false)
class EventAttack : Event(false)
class EventHandleBlockBreaking(var parameter: Boolean) : Event(false)
class EventEntityColor(val entity: Entity, var color: Color?) : Event(false)
class EventHasForwardMovement(var hasForwardMovement: Boolean) : Event(false)
class EventSwing(var hand: Hand) : Event(true)
class EventColorCorrection(var red: Int, var green: Int, var blue: Int) : Event(false)
class EventResetEquipProgress : Event(true)
class EventIsWalking(var walking: Boolean) : Event(false)
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

class EventCameraOverride(val camera: Camera) : Event(false)