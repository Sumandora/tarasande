package net.tarasandedevelopment.tarasande.event

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.StringReader
import io.netty.buffer.ByteBuf
import net.minecraft.block.BlockState
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.input.Input
import net.minecraft.client.network.PlayerListEntry
import net.minecraft.client.network.ServerInfo
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.render.Camera
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.command.CommandSource
import net.minecraft.entity.Entity
import net.minecraft.item.Item
import net.minecraft.network.Packet
import net.minecraft.particle.ParticleEffect
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Matrix4f
import net.minecraft.util.math.Vec3d
import net.minecraft.util.shape.VoxelShape
import net.tarasandedevelopment.tarasande.base.Manager
import net.tarasandedevelopment.tarasande.base.event.Event
import net.tarasandedevelopment.tarasande.screen.cheatmenu.panel.Panel
import net.tarasandedevelopment.tarasande.util.math.rotation.Rotation
import java.awt.Color
import java.net.InetSocketAddress
import java.util.*

class EventChat(val chatMessage: String) : Event(true)
class EventKey(val key: Int, val action: Int) : Event(true)

class EventUpdate(val state: State) : Event(state == State.PRE) {
    enum class State {
        PRE, PRE_PACKET, POST
    }
}

class EventTick(val state: State) : Event(false) {
    enum class State {
        PRE, POST
    }
}

class EventResolutionUpdate(val prevWidth: Double, val prevHeight: Double, val width: Double, val height: Double) : Event(false)
class EventRender2D(val matrices: MatrixStack) : Event(false)
class EventScreenRender(val matrices: MatrixStack, val screen: Screen, val mouseX: Int, val mouseY: Int) : Event(false)
class EventRender3D(val matrices: MatrixStack, val positionMatrix: Matrix4f) : Event(false)

class EventPacket(val type: Type, val packet: Packet<*>?) : Event(true) {
    enum class Type {
        SEND, RECEIVE
    }
}

class EventPollEvents : Event {
    var dirty = false
        private set
    var rotation: Rotation
        set(value) {
            dirty = true
            field = value
        }
    var minRotateToOriginSpeed = 1.0
    var maxRotateToOriginSpeed = 1.0
    val fake: Boolean

    constructor(rotation: Rotation, fake: Boolean) : super(false) {
        this.rotation = rotation
        this.dirty = false
        this.fake = fake
    }
}

class EventVelocityYaw(var yaw: Float) : Event(false)
class EventKeyBindingIsPressed(val keyBinding: KeyBinding, var pressed: Boolean) : Event(false)

class EventVelocity(var velocityX: Double, var velocityY: Double, var velocityZ: Double, val packet: Packet) : Event(true) {
    enum class Packet {
        VELOCITY, EXPLOSION
    }
}

class EventInput(val input: Input, var movementForward: Float, var movementSideways: Float, var slowDown: Boolean, val slowdownAmount: Float) : Event(true)

class EventJump(var yaw: Float, val state: State) : Event(state == State.PRE) {
    enum class State {
        PRE, POST
    }
}

class EventGamma(val x: Int, val y: Int, var color: Int) : Event(false)
class EventMovement(val entity: Entity, var velocity: Vec3d) : Event(false)
class EventIsEntityAttackable(val entity: Entity, var attackable: Boolean) : Event(false)


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

class EventAttack : Event(false) {
    var dirty = false
        set(value) {
            if (field && !value)
                error(javaClass.name + " is already dirty")
            field = value
        }
}

class EventHandleBlockBreaking(var parameter: Boolean) : Event(false)
class EventEntityColor(val entity: Entity, var color: Color?) : Event(false)
class EventHasForwardMovement(var hasForwardMovement: Boolean) : Event(false)
class EventSwing(var hand: Hand) : Event(true)
class EventColorCorrection(var red: Int, var green: Int, var blue: Int) : Event(false)
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
class EventPlayerListName(val playerListEntry: PlayerListEntry, var displayName: Text) : Event(false)
class EventRotationSet(val yaw: Float, val pitch: Float) : Event(false)

class EventUpdateTargetedEntity(val state: State) : Event(false) {
    enum class State {
        PRE, POST
    }
}

class EventRenderBlockModel(val state: BlockState, val pos: BlockPos) : Event(true)

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
class EventBoundingBoxOverride(val entity: Entity, var boundingBox: Box) : Event(false)

class EventPacketTransform(val type: Type, val buf: ByteBuf?) : Event(false) {
    enum class Type {
        DECODE, ENCODE
    }
}

class EventCollisionShape(val pos: BlockPos, var collisionShape: VoxelShape) : Event(false)
class EventTextVisit(var string: String) : Event(false)

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

class EventLoadManager(val manager: Manager<*>) : Event(false)
class EventConnectServer(val address: InetSocketAddress) : Event(false)
class EventSkipIdlePacket : Event(false)
class EventDisconnect : Event(false)
class EventIsSaddled(var saddled: Boolean) : Event(false)
class EventInvalidGameMode(val uuid: UUID) : Event(false)
class EventRespawn(var showDeathScreen: Boolean) : Event(false)
class EventScreenInput(var doneInput: Boolean) : Event(true)
class EventRenderMultiplayerEntry(val matrices: MatrixStack, val x: Int, val y: Int, val entryWidth: Int, val entryHeight: Int, val mouseX: Int, val mouseY: Int, val server: ServerInfo) : Event(false)
class EventChunkOcclusion : Event(true)
class EventParticle(val effect: ParticleEffect) : Event(true)
class EventPanels(val panels: MutableList<Class<out Panel>>) : Event(false)

class EventInputSuggestions(val reader: StringReader) : Event(false) {
    var dispatcher: CommandDispatcher<CommandSource>? = null
    var commandSource: CommandSource? = null
}

class EventFog(val state: State, val values: FloatArray) : Event(false) {
    enum class State {
        FOG_START, FOG_END, FOG_COLOR
    }
}