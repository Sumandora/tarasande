package su.mandora.tarasande.event.impl

import net.minecraft.block.BlockState
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen
import net.minecraft.client.network.PlayerListEntry
import net.minecraft.client.network.ServerInfo
import net.minecraft.client.render.Camera
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.particle.ParticleEffect
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import org.joml.Matrix4f
import su.mandora.tarasande.event.Event

class EventResolutionUpdate(val prevWidth: Double, val prevHeight: Double, val width: Double, val height: Double) : Event(false)
class EventRender2D(val context: DrawContext) : Event(false)

class EventScreenRender(val context: DrawContext, val screen: Screen, val state: State) : Event(false) {
    enum class State {
        PRE, POST
    }
}

class EventRender3D(val matrices: MatrixStack, val positionMatrix: Matrix4f, val state: State) : Event(false) {
    enum class State {
        PRE, POST
    }
}

class EventGamma(val x: Int, val y: Int, var color: Int) : Event(false)
class EventColorCorrection(var red: Int, var green: Int, var blue: Int) : Event(false)
class EventCameraOverride(val camera: Camera) : Event(false)
class EventRenderBlockModel(val state: BlockState, val pos: BlockPos) : Event(true)
class EventTextVisit(var string: String) : Event(false)
class EventRenderMultiplayerEntry(val context: DrawContext, val x: Int, val y: Int, val entryWidth: Int, val entryHeight: Int, val mouseX: Int, val mouseY: Int, val server: ServerInfo, val multiplayerScreen: MultiplayerScreen) : Event(false)

class EventFogStart(var distance: Float) : Event(false)
class EventFogEnd(var distance: Float) : Event(false)
class EventFogColor(var color: FloatArray) : Event(false)

class EventFovMultiplier(var movementFovMultiplier: Float) : Event(false)
class EventChunkOcclusion : Event(true)
class EventParticle(val effect: ParticleEffect) : Event(true)
class EventPlayerListName(val playerListEntry: PlayerListEntry, var displayName: Text) : Event(false)
