package su.mandora.tarasande.module.movement

import com.google.gson.JsonArray
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.*
import net.minecraft.util.math.Matrix4f
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d
import org.apache.commons.lang3.ArrayUtils
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.*
import su.mandora.tarasande.util.math.rotation.Rotation
import su.mandora.tarasande.util.math.rotation.RotationUtil
import su.mandora.tarasande.value.ValueBind
import java.awt.Color
import java.util.function.Consumer

class ModuleMovementRecorder : Module("Movement recorder", "Records your movement for later playback", ModuleCategory.MOVEMENT) {

    private val recordAndPlaybackButton = ValueBind(this, "Record/Playback button", ValueBind.Type.KEY, GLFW.GLFW_KEY_UNKNOWN, true)
    private val deleteButton = ValueBind(this, "Delete button", ValueBind.Type.KEY, GLFW.GLFW_KEY_UNKNOWN, true)

    private var recording = false
    private val records = ArrayList<Record>()
    private var currentRecording: Record? = null
    private var lastVelocity: Vec3d? = null

    private var playedBack: Record? = null
    private var playbackState: PlaybackState? = null
    private var executingIndex = 0
    private var lastRotation: Rotation? = null

    private fun drawPath(bufferBuilder: BufferBuilder, matrix: Matrix4f, record: Record, alpha: Float) {
        bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR)
        for (tick in record.ticks) {
            val vec = tick.pos
            bufferBuilder.vertex(matrix, vec.x.toFloat(), vec.y.toFloat(), vec.z.toFloat()).color(1f, 1f, 1f, alpha).next()
        }
        bufferBuilder.end()
        BufferRenderer.draw(bufferBuilder)
    }

    override fun onDisable() {
        recording = false
        records.clear()
        currentRecording = null
        lastVelocity = null
        playedBack = null
        playbackState = null
        executingIndex = 0
        lastRotation = null
    }

    val eventConsumer = Consumer<Event> { event ->
        when (event) {
            is EventUpdate -> {
                when (event.state) {
                    EventUpdate.State.PRE -> {
                        if (playbackState == PlaybackState.EXECUTING) {
                            val tick = playedBack?.ticks?.get(executingIndex)!!

                            mc.player?.isSprinting = tick.sprinting

                            mc.player?.isSneaking = tick.sneaking
                            mc.player?.input?.sneaking = tick.sneaking
                        }
                    }
                    EventUpdate.State.PRE_PACKET -> {
                        for (i in 0 until recordAndPlaybackButton.wasPressed()) {
                            var matchingRecord: Record? = null
                            if (!recording) {
                                for (record in records) {
                                    if (record.ticks.first().pos.squaredDistanceTo(mc.player?.pos) <= 0.5 * 0.5) {
                                        matchingRecord = record
                                        break
                                    }
                                }
                            }
                            if (matchingRecord != null) {
                                playedBack = matchingRecord
                                executingIndex = 0
                                lastRotation = Rotation(mc.player!!)
                                playbackState = PlaybackState.PREPARE
                            } else {
                                recording = !recording
                                currentRecording = if (recording) {
                                    Record()
                                } else {
                                    if (!deleteButton.isPressed() && currentRecording != null && currentRecording?.ticks?.isNotEmpty()!! && currentRecording?.ticks?.any { it.movement.horizontalLengthSquared() > 0.0 }!!)
                                        records.add(currentRecording?.copy()!!)
                                    null
                                }
                            }
                        }

                        if (recording) {
                            if (lastVelocity != null)
                                currentRecording?.ticks?.add(TickMovement(Rotation(mc.player!!), lastVelocity!!, mc.player?.pos!!, mc.player?.isSprinting!!, mc.player?.isSneaking!!, mc.player?.input?.movementInput!!))
                        } else if (playbackState != null) {
                            when (playbackState!!) {
                                PlaybackState.PREPARE -> {
                                    val pos = playedBack?.ticks?.first()?.pos!!
                                    if (mc.player?.pos?.squaredDistanceTo(pos)!! <= 0.1 * 0.1) {
                                        playbackState = PlaybackState.EXECUTING
                                    }
                                }
                                PlaybackState.EXECUTING -> {
                                    lastRotation = Rotation(mc.player!!)
                                    executingIndex++
                                    if (executingIndex >= playedBack?.ticks?.size!! || playedBack?.ticks?.get(executingIndex)?.pos?.squaredDistanceTo(mc.player?.pos)!! > 4.0 * 4.0) {
                                        playedBack = null
                                        playbackState = null
                                        executingIndex = 0
                                        lastRotation = null
                                    }
                                }
                            }
                        }
                    }
                    else -> {}
                }
            }
            is EventInput -> {
                if (playbackState == PlaybackState.EXECUTING) {
                    val tick = playedBack?.ticks?.get(executingIndex)!!

                    event.movementSideways = tick.input.x
                    event.movementForward = tick.input.y
                }
            }
            is EventKeyBindingIsPressed -> {
                if ((event.keyBinding == mc.options.sneakKey || event.keyBinding == mc.options.forwardKey) && playbackState == PlaybackState.PREPARE) {
                    event.pressed = true
                }
            }
            is EventMovement -> {
                if (event.entity != mc.player) return@Consumer

                if (playbackState == PlaybackState.EXECUTING) {
                    event.velocity = playedBack?.ticks?.get(executingIndex)?.movement!!
                }
                lastVelocity = event.velocity
            }
            is EventPollEvents -> {
                if (playbackState != null && playedBack != null) {
                    when (playbackState!!) {
                        PlaybackState.PREPARE -> {
                            val pos = playedBack?.ticks?.first()?.pos!!
                            event.rotation = RotationUtil.getRotations(mc.player?.eyePos!!, pos.add(0.0, mc.player?.standingEyeHeight?.toDouble()!!, 0.0))
                        }
                        PlaybackState.EXECUTING -> {
                            event.rotation = lastRotation?.smoothedTurn(playedBack?.ticks?.get(executingIndex)?.rotation!!, mc.tickDelta.toDouble())!!
                        }
                    }
                    event.rotation.correctSensitivity()
                    mc.player?.yaw = event.rotation.yaw
                    mc.player?.pitch = event.rotation.pitch
                    event.minRotateToOriginSpeed = 1.0
                    event.maxRotateToOriginSpeed = 1.0
                }
            }
            is EventGoalMovement -> {
                if (playbackState == PlaybackState.PREPARE) {
                    val pos = playedBack?.ticks?.first()?.pos!!
                    event.yaw = RotationUtil.getYaw(pos.subtract(mc.player?.pos!!)).toFloat()
                }
            }
            is EventRender2D -> {
                val str = if (recording) "Recording" else if (playbackState != null) playbackState?.name?.let { it.first() + it.substring(1).lowercase() } else ""
                mc.textRenderer?.drawWithShadow(event.matrices, str, mc.window?.scaledWidth!! / 2.0f - mc.textRenderer.getWidth(str) / 2.0f, mc.window?.scaledHeight!! / 2.0f - mc.textRenderer.fontHeight, Color.white.rgb)
            }
            is EventRender3D -> {
                GL11.glEnable(GL11.GL_BLEND)
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
                GL11.glDisable(GL11.GL_CULL_FACE)
                GL11.glEnable(GL11.GL_LINE_SMOOTH)
                GL11.glDisable(GL11.GL_DEPTH_TEST)
                GL11.glDepthMask(false)
                event.matrices.push()
                val vec3d = MinecraftClient.getInstance().gameRenderer.camera.pos
                event.matrices.translate(-vec3d!!.x, -vec3d.y, -vec3d.z)
                val bufferBuilder = Tessellator.getInstance().buffer
                RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
                val matrix = event.matrices.peek()?.positionMatrix!!
                if (playbackState != null && playedBack != null) {
                    drawPath(bufferBuilder, matrix, playedBack!!, 1.0f)
                } else {
                    for (record in if (currentRecording != null) ArrayUtils.add(records.toTypedArray(), currentRecording!!) else records.toTypedArray()) {
                        drawPath(bufferBuilder, matrix, record, 1f - (mc.player?.pos?.squaredDistanceTo(record.ticks.first().pos)!! / (16.0 * 16.0)).toFloat().coerceAtMost(1.0f))
                    }
                }
                event.matrices.pop()
                GL11.glDisable(GL11.GL_BLEND)
                GL11.glDisable(GL11.GL_LINE_SMOOTH)
                GL11.glEnable(GL11.GL_DEPTH_TEST)
                GL11.glDepthMask(true)
            }
        }
    }

}

enum class PlaybackState { PREPARE, EXECUTING }

class Record() {
    val ticks = ArrayList<TickMovement>()

    constructor(ticks: ArrayList<TickMovement>) : this() {
        this.ticks.addAll(ticks)
    }

    fun toJson(): JsonArray {
        val jsonArray = JsonArray()
        for (tick in ticks) {
            jsonArray.add(TarasandeMain.get().gson.toJson(tick))
        }
        return jsonArray
    }

    fun fromJson(jsonArray: JsonArray) {
        for (jsonElement in jsonArray) {
            ticks.add(TarasandeMain.get().gson.fromJson(jsonElement, TickMovement::class.java))
        }
    }

    fun copy() = Record(ticks)
}

class TickMovement(val rotation: Rotation, val movement: Vec3d, val pos: Vec3d, val sprinting: Boolean, val sneaking: Boolean, val input: Vec2f)