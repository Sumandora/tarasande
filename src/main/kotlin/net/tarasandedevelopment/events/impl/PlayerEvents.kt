package net.tarasandedevelopment.events.impl

import net.minecraft.block.BlockState
import net.minecraft.entity.Entity
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import net.minecraft.util.shape.VoxelShape
import net.tarasandedevelopment.events.Event

class EventUpdate(val state: State) : Event(state == State.PRE) {
    enum class State {
        PRE, PRE_PACKET, POST
    }
}

class EventVelocity(var velocityX: Double, var velocityY: Double, var velocityZ: Double, val packet: Packet) : Event(true) {
    enum class Packet {
        VELOCITY, EXPLOSION
    }
}

class EventJump(var yaw: Float, val state: State) : Event(state == State.PRE) {
    enum class State {
        PRE, POST
    }
}

class EventMovement(val entity: Entity, var velocity: Vec3d) : Event(false)
class EventAttackEntity(val entity: Entity, val state: State) : Event(false) {
    enum class State {
        PRE, POST
    }
}

class EventKeepSprint(var sprinting: Boolean) : Event(false)
class EventAttack : Event(false) {
    var dirty = false
        set(value) {
            if (field && !value)
                error(javaClass.name + " is already dirty")
            field = value
        }
}

class EventStep : Event {
    var stepHeight: Float
        set(value) {
            if (state == State.POST)
                error("stepHeight can't be modified during POST")
            field = value
        }
    val state: State

    constructor(stepHeight: Float, state: State) : super(false) {
        this.stepHeight = stepHeight
        this.state = state
    }

    enum class State {
        PRE, POST
    }
}

class EventBlockCollision(val state: BlockState, val pos: BlockPos, val entity: Entity) : Event(true)
class EventEntityFlag(val entity: Entity, val flag: Int, var enabled: Boolean) : Event(false)
class EventCollisionShape(val pos: BlockPos, var collisionShape: VoxelShape) : Event(false)
class EventBoundingBoxOverride(val entity: Entity, var boundingBox: Box) : Event(false)
class EventChat(val chatMessage: String) : Event(true)
class EventSwing(var hand: Hand) : Event(true)