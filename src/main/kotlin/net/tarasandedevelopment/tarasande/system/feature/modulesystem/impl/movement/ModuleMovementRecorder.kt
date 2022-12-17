package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.movement

import net.minecraft.client.MinecraftClient
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d
import net.tarasandedevelopment.tarasande.event.*
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBind
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory
import net.tarasandedevelopment.tarasande.util.extension.javaruntime.withAlpha
import net.tarasandedevelopment.tarasande.util.extension.minecraft.minus
import net.tarasandedevelopment.tarasande.util.math.rotation.Rotation
import net.tarasandedevelopment.tarasande.util.math.rotation.RotationUtil
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import net.tarasandedevelopment.tarasande.util.render.font.FontWrapper
import org.apache.commons.lang3.ArrayUtils
import org.lwjgl.glfw.GLFW
import java.awt.Color

class ModuleMovementRecorder : Module("Movement recorder", "Records your movement for later playback", ModuleCategory.MOVEMENT) {

    private val recordAndPlaybackButton = ValueBind(this, "Record/Playback button", ValueBind.Type.KEY, GLFW.GLFW_KEY_UNKNOWN)
    private val deleteButton = ValueBind(this, "Delete button", ValueBind.Type.KEY, GLFW.GLFW_KEY_UNKNOWN)

    private var recording = false
    private val records = ArrayList<Record>()
    private var currentRecording: Record? = null
    private var lastVelocity: Vec3d? = null

    private var playedBack: Record? = null
    private var playbackState: PlaybackState? = null
    private var executingIndex = 0
    private var lastRotation: Rotation? = null

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

    init {
        registerEvent(EventUpdate::class.java) { event ->
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
                                if (!deleteButton.isPressed() && currentRecording != null && currentRecording?.ticks?.isNotEmpty()!! && currentRecording?.ticks?.any { it.movement.horizontalLengthSquared() > 0.0 }!!) records.add(currentRecording?.copy()!!)
                                null
                            }
                        }
                    }

                    if (recording) {
                        if (lastVelocity != null) currentRecording?.ticks?.add(TickMovement(Rotation(mc.player!!), lastVelocity!!, mc.player?.pos!!, mc.player?.isSprinting!!, mc.player?.isSneaking!!, mc.player?.input?.movementInput!!))
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

        registerEvent(EventInput::class.java) { event ->
            if (event.input == MinecraftClient.getInstance().player?.input)
                if (playbackState == PlaybackState.EXECUTING) {
                    val tick = playedBack?.ticks?.get(executingIndex)!!

                    event.movementSideways = tick.input.x
                    event.movementForward = tick.input.y
                }
        }

        registerEvent(EventKeyBindingIsPressed::class.java) { event ->
            if ((event.keyBinding == mc.options.sneakKey || event.keyBinding == mc.options.forwardKey) && playbackState == PlaybackState.PREPARE) {
                event.pressed = true
            }
        }

        registerEvent(EventMovement::class.java) { event ->
            if (playbackState == PlaybackState.EXECUTING) {
                event.velocity = playedBack?.ticks?.get(executingIndex)?.movement!!
            }
            lastVelocity = event.velocity
        }

        registerEvent(EventPollEvents::class.java) { event ->
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

        registerEvent(EventGoalMovement::class.java) { event ->
            if (playbackState == PlaybackState.PREPARE) {
                val pos = playedBack?.ticks?.first()?.pos!!
                event.yaw = RotationUtil.getYaw(pos - mc.player?.pos!!).toFloat()
            }
        }

        registerEvent(EventRender2D::class.java) { event ->
            val str = if (recording) "Recording" else playbackState?.name?.let {
                it.first() + it.substring(1).lowercase()
            } ?: return@registerEvent
            FontWrapper.textShadow(event.matrices, str, mc.window?.scaledWidth!! / 2.0F - FontWrapper.getWidth(str) / 2.0F, mc.window?.scaledHeight!! / 2.0F - FontWrapper.fontHeight(), -1)
        }

        registerEvent(EventRender3D::class.java) { event ->
            if (playbackState != null && playedBack != null) {
                RenderUtil.renderPath(event.matrices, playedBack!!.ticks.map { it.pos }, Color.white.rgb)
            } else {
                for (record in if (currentRecording != null) ArrayUtils.add(records.toTypedArray(), currentRecording!!) else records.toTypedArray()) {
                    RenderUtil.renderPath(event.matrices, record.ticks.map { it.pos }, Color.white.withAlpha(255 - (mc.player?.pos?.squaredDistanceTo(record.ticks.first().pos)!! / (16.0 * 16.0) * 255).toInt().coerceAtMost(255)).rgb)
                }
            }
        }
    }

    enum class PlaybackState { PREPARE, EXECUTING }

    inner class Record() {
        val ticks = ArrayList<TickMovement>()

        constructor(ticks: ArrayList<TickMovement>) : this() {
            this.ticks.addAll(ticks)
        }

        fun copy() = Record(ticks)
    }

    inner class TickMovement(val rotation: Rotation, val movement: Vec3d, val pos: Vec3d, val sprinting: Boolean, val sneaking: Boolean, val input: Vec2f)

}