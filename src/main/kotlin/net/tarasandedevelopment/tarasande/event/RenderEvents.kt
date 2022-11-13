package net.tarasandedevelopment.tarasande.event

import net.minecraft.block.BlockState
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.network.PlayerListEntry
import net.minecraft.client.network.ServerInfo
import net.minecraft.client.render.Camera
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.particle.ParticleEffect
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Matrix4f
import su.mandora.event.Event

class EventResolutionUpdate(val prevWidth: Double, val prevHeight: Double, val width: Double, val height: Double) : Event(false)
class EventRender2D(val matrices: MatrixStack) : Event(false)
class EventScreenRender(val matrices: MatrixStack, val screen: Screen, val mouseX: Int, val mouseY: Int) : Event(false)
class EventRender3D(val matrices: MatrixStack, val positionMatrix: Matrix4f) : Event(false)
class EventGamma(val x: Int, val y: Int, var color: Int) : Event(false)
class EventColorCorrection(var red: Int, var green: Int, var blue: Int) : Event(false)
class EventCameraOverride(val camera: Camera) : Event(false)
class EventRenderBlockModel(val state: BlockState, val pos: BlockPos) : Event(true)
class EventTextVisit(var string: String) : Event(false)
class EventRenderMultiplayerEntry(val matrices: MatrixStack, val x: Int, val y: Int, val entryWidth: Int, val entryHeight: Int, val mouseX: Int, val mouseY: Int, val server: ServerInfo) : Event(false)
class EventFog(val state: State, val values: FloatArray) : Event(false) {
    enum class State {
        FOG_START, FOG_END, FOG_COLOR
    }
}

class EventMovementFovMultiplier(var movementFovMultiplier: Float) : Event(false)
class EventChunkOcclusion : Event(true)
class EventParticle(val effect: ParticleEffect) : Event(true)
class EventPlayerListName(val playerListEntry: PlayerListEntry, var displayName: Text) : Event(false)