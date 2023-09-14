package su.mandora.tarasande.system.feature.modulesystem.impl.misc

import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.minecraft.util.math.Vec3d
import org.lwjgl.glfw.GLFW
import su.mandora.tarasande.event.impl.*
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.*
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.system.screen.informationsystem.Information
import su.mandora.tarasande.system.screen.informationsystem.ManagerInformation
import su.mandora.tarasande.util.DEFAULT_REACH
import su.mandora.tarasande.util.DEFAULT_TPS
import su.mandora.tarasande.util.extension.minecraft.setMovementForward
import su.mandora.tarasande.util.extension.minecraft.setMovementSideways
import su.mandora.tarasande.util.math.MathUtil
import su.mandora.tarasande.util.math.time.TickCounter
import su.mandora.tarasande.util.math.time.TimeUtil
import su.mandora.tarasande.util.maxReach
import su.mandora.tarasande.util.player.PlayerUtil
import su.mandora.tarasande.util.player.prediction.PredictionEngine
import su.mandora.tarasande.util.render.RenderUtil
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min
import kotlin.math.round

class ModuleTickBaseManipulation : Module("Tick base manipulation", "Shifts the tick base your game runs in", ModuleCategory.MISC) {

    private val chargeKey = ValueBind(this, "Charge key", ValueBind.Type.KEY, GLFW.GLFW_KEY_UNKNOWN)
    private val unchargeKey = ValueBind(this, "Uncharge key", ValueBind.Type.KEY, GLFW.GLFW_KEY_UNKNOWN)
    private val resyncPositions = ValueBoolean(this, "Re-sync positions", true)
    private val instantUncharge = ValueBoolean(this, "Instant uncharge", true)
    private val unchargeSpeed = ValueNumber(this, "Uncharge speed", 0.0, 1000.0, 1000.0, 1.0, isEnabled = { !instantUncharge.value })
    private val playStyle = ValueMode(this, "Play style", true, "Offensive", "Defensive", "Counter")
    private val reach = ValueNumber(this, "Reach", 0.1, DEFAULT_REACH, maxReach, 0.1, isEnabled = { playStyle.isSelected(0) })
    private val skipCooldown = ValueBoolean(this, "Skip cooldown", true)
    private val autoCharge = ValueBoolean(this, "Auto charge", true)
    private val minimum = ValueNumber(this, "Minimum", 0.0, 500.0, 2000.0, 10.0, isEnabled = { autoCharge.value })
    private val delay = ValueNumber(this, "Delay", 0.0, 600.0, 2000.0, 10.0, isEnabled = { autoCharge.value })
    private val future = object : ValueBoolean(this, "Future", false) {
        override fun onChange(oldValue: Boolean?, newValue: Boolean) {
            if (shifted < 0L)
                shifted = 0L
        }
    }
    private val negativeUncharge = ValueBoolean(this, "Negative uncharge", false, isEnabled = { future.value })
    private val futureHop = ValueBind(this, "Future hop", ValueBind.Type.KEY, GLFW.GLFW_KEY_UNKNOWN, isEnabled = { future.value })
    private val hopLength = ValueNumber(this, "Hop length", 0.0, 500.0, 2000.0, 10.0, isEnabled = { future.value })
    private val resyncNegativity = ValueBoolean(this, "Resync negativity", false, isEnabled = { future.value })
    private val actualPositionColor = ValueColor(this, "Actual position color", 0.0, 1.0, 1.0, 1.0)
    private val chargeOnIdle = ValueBoolean(this, "Charge on idle", false)

    private var prevTime = 0L

    private var prevShifted = 0L
    var shifted = 0L

    private var didHit = false

    private val autoChargeDelay = TimeUtil()
    private var prevUnchargePressed = false

    private var willUncharge = false

    private val entityResyncer = TickCounter((1000.0 / DEFAULT_TPS).toLong())

    private var predictedPlayer: ClientPlayerEntity? = null

    private var waitingForIdlePacket = false

    init {
        ManagerInformation.add(object : Information("Tick base manipulation", "Time shifted") {
            override fun getMessage(): String? {
                if (!enabled.value) return null
                if (shifted == 0L) return null
                return shifted.toString() + " (" + round(shifted / mc.renderTickCounter.tickTime).toInt() + ")"
            }
        })
    }

    private fun shouldUncharge(futurePos: Vec3d): Boolean {
        val validEntities = mc.world?.entities?.filterIsInstance<PlayerEntity>()?.filter { PlayerUtil.isAttackable(it) }!!
        if (validEntities.isEmpty())
            return false

        if (validEntities.any { mc.player?.eyePos?.squaredDistanceTo(MathUtil.closestPointToBox(mc.player?.eyePos!!, it.boundingBox.expand(it.targetingMargin.toDouble())))!! <= reach.value * reach.value })
            return false

        return validEntities.any { futurePos.squaredDistanceTo(MathUtil.closestPointToBox(futurePos, it.boundingBox.expand(it.targetingMargin.toDouble()))) <= reach.value * reach.value }
    }

    override fun onEnable() {
        shifted = 0L
        prevShifted = 0L
        entityResyncer.reset()
    }

    private fun shouldPredict() = (shifted > 0L || (future.value && shifted == 0L)) && (playStyle.isSelected(0) || actualPositionColor.alpha!! > 0.0)

    init {
        registerEvent(EventUpdate::class.java) { event ->
            when (event.state) {
                EventUpdate.State.PRE -> {
                    waitingForIdlePacket = true
                    predictedPlayer =
                        if (shouldPredict())
                            PredictionEngine.predictState(round((if (shifted == 0L) hopLength.value else shifted).toLong() / mc.renderTickCounter.tickTime).toInt()).first
                        else
                            null

                    willUncharge = playStyle.isSelected(0) && predictedPlayer != null && shouldUncharge(predictedPlayer!!.eyePos)

                    if (shifted > prevShifted) event.cancelled = true
                }

                EventUpdate.State.POST -> { // doing it in post means, that we skip as soon as we get it, otherwise we get a one tick delay
                    if (waitingForIdlePacket) {
                        // We just skipped the idle packet
                        if (chargeOnIdle.value)
                            shifted += mc.renderTickCounter.tickTime.toLong()
                    }
                    if (shifted >= prevShifted && skipCooldown.value)
                        shifted = max(min(0L, shifted), shifted - ceil((0.9 - mc.player?.getAttackCooldownProgress(0.5F)!!).coerceAtLeast(0.0) * mc.player?.attackCooldownProgressPerTick!! * mc.renderTickCounter.tickTime).toLong())
                }

                else -> {}
            }
        }

        registerEvent(EventPacket::class.java) { event ->
            if (event.type == EventPacket.Type.SEND && event.packet is PlayerMoveC2SPacket) {
                waitingForIdlePacket = false
            }
        }

        registerEvent(EventInput::class.java) { event ->
            if(event.input == mc.player?.input)
                if (playStyle.isSelected(1))
                    if (shifted < prevShifted) {
                        if (didHit) {
                            event.input.setMovementForward(-event.input.movementForward)
                            event.input.setMovementSideways(-event.input.movementSideways)
                        }
                    } else {
                        didHit = false
                    }
        }

        registerEvent(EventAttackEntity::class.java) {
            didHit = true
        }

        registerEvent(EventEntityHurt::class.java) { event ->
            if (event.entity == mc.player && playStyle.isSelected(2))
                shifted = 0L
        }

        registerEvent(EventTimeTravel::class.java) { event ->
            if (mc.player != null) {
                prevShifted = shifted

                if (!unchargeKey.isPressed() && !willUncharge) {
                    if (prevUnchargePressed)
                        autoChargeDelay.reset()

                    if (chargeKey.isPressed())
                        shifted += event.time - prevTime
                    else if (autoCharge.value && minimum.value > shifted && autoChargeDelay.hasReached(delay.value.toLong()))
                        shifted += min(event.time - prevTime, (minimum.value - shifted).toLong())
                    else if (future.value && resyncNegativity.value && shifted < 0L)
                        shifted += min(event.time - prevTime, -shifted)
                }
                if (resyncPositions.value && prevShifted < shifted) {
                    repeat(entityResyncer.getTicks().toInt()) {
                        mc.world?.tickEntities()
                    }
                } else {
                    entityResyncer.reset()
                }
                if (unchargeKey.isPressed()) {
                    if (shifted > 0L) {
                        shifted = if (instantUncharge.value) 0L else max(0L, (shifted - unchargeSpeed.value * (RenderUtil.deltaTime / 100.0)).toLong())
                    } else if (future.value && negativeUncharge.value) {
                        shifted -= event.time - prevTime
                    }
                } else if (willUncharge) {
                    shifted = if (shifted == 0L && future.value)
                        -hopLength.value.toLong()
                    else
                        0L
                }

                if (future.value)
                    repeat(futureHop.wasPressed()) {
                        shifted -= hopLength.value.toLong()
                    }

                prevUnchargePressed = unchargeKey.isPressed()
            } else {
                shifted = 0L
            }
            prevTime = event.time
            event.time -= shifted
        }

        registerEvent(EventRender3D::class.java) { event ->
            if(event.state != EventRender3D.State.POST) return@registerEvent

            RenderUtil.blockOutline(event.matrices, predictedPlayer?.boundingBox ?: return@registerEvent, actualPositionColor.getColor().rgb)
        }
    }
}
