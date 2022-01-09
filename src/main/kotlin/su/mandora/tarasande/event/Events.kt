package su.mandora.tarasande.event

import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.Entity
import net.minecraft.network.Packet
import net.minecraft.util.math.Vec3d
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.util.math.rotation.Rotation

class EventChat(val chatMessage: String) : Event(true)
class EventKey(val key: Int) : Event(true)
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
class EventRender3D(val matrices: MatrixStack) : Event(false)

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
	var dirty: Boolean
	var minRotateToOriginSpeed: Double
	var maxRotateToOriginSpeed: Double

	constructor(rotation: Rotation) : super(false) {
		this.rotation = rotation
		this.dirty = false
		this.minRotateToOriginSpeed = 1.0
		this.maxRotateToOriginSpeed = 1.0
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
class EventJump(var yaw: Float) : Event(false)
class EventGamma(var gamma: Double) : Event(false)
class EventMovement(val entity: Entity, var velocity: Vec3d) : Event(false)
class EventSlowdown(var usingItem: Boolean) : Event(false)
class EventSlowdownAmount(var slowdownAmount: Float) : Event(false)
class EventIsEntityAttackable(val entity: Entity?, var attackable: Boolean) : Event(false)

class EventVanillaFlight : Event {
	var dirty: Boolean = false
	var flying: Boolean
		set(value) {
			dirty = true
			field = value
		}
	var flightSpeed: Float

	constructor(flying: Boolean, flightSpeed: Float) : super(false) {
		this.flying = flying
		this.flightSpeed = flightSpeed
	}

}

class EventMouse(val button: Int) : Event(true)
class EventMouseDelta(var deltaX: Double, var deltaY: Double) : Event(false)
class EventTimeTravel(var time: Long) : Event(false)