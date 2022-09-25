package su.mandora.tarasande.module.misc

import net.minecraft.client.MinecraftClient
import net.minecraft.entity.LivingEntity
import org.lwjgl.glfw.GLFW
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventAttackEntity
import su.mandora.tarasande.event.EventKeyBindingIsPressed
import su.mandora.tarasande.event.EventTimeTravel
import su.mandora.tarasande.event.EventUpdate
import su.mandora.tarasande.mixin.accessor.IMinecraftClient
import su.mandora.tarasande.mixin.accessor.IRenderTickCounter
import su.mandora.tarasande.util.math.TimeUtil
import su.mandora.tarasande.util.player.PlayerUtil
import su.mandora.tarasande.util.render.RenderUtil
import su.mandora.tarasande.value.ValueBind
import su.mandora.tarasande.value.ValueBoolean
import su.mandora.tarasande.value.ValueNumber
import java.util.function.Consumer
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

class ModuleTickBaseManipulation : Module("Tick base manipulation", "Shifts the tick base", ModuleCategory.MISC) {

    private val chargeKey = ValueBind(this, "Charge key", ValueBind.Type.KEY, GLFW.GLFW_KEY_UNKNOWN)
    private val unchargeKey = ValueBind(this, "Uncharge key", ValueBind.Type.KEY, GLFW.GLFW_KEY_UNKNOWN)
    private val resyncPositions = ValueBoolean(this, "Resync positions", true)
    private val instantUncharge = ValueBoolean(this, "Instant uncharge", true)
    private val unchargeSpeed = object : ValueNumber(this, "Uncharge speed", 0.0, 1000.0, 1000.0, 1.0) {
        override fun isEnabled() = !instantUncharge.value
    }
    private val rapidFire = ValueBoolean(this, "Rapid fire", false)
    private val rapidInstantUncharge = object : ValueBoolean(this, "Rapid instant uncharge", false) {
        override fun isEnabled() = rapidFire.value
    }
    private val defensive = ValueBoolean(this, "Defensive", false)
    private val skipCooldown = ValueBoolean(this, "Skip cooldown", true)
    private val autoCharge = ValueBoolean(this, "Auto charge", true)
    private val minimum = object : ValueNumber(this, "Minimum", 0.0, 500.0, 2000.0, 100.0) {
        override fun isEnabled() = autoCharge.value
    }
    private val delay = object : ValueNumber(this, "Delay", 0.0, 600.0, 2000.0, 100.0) {
        override fun isEnabled() = autoCharge.value
    }
    private val future = object : ValueBoolean(this, "Future", false) {
        override fun onChange() {
            if(shifted < 0L)
                shifted = 0L
        }
    }
    private val negativeUncharge = object : ValueBoolean(this, "Negative uncharge", false) {
        override fun isEnabled() = future.value
    }
    private val futureHop = object : ValueBind(this, "Future hop", Type.KEY, GLFW.GLFW_KEY_UNKNOWN) {
        override fun isEnabled() = future.value
    }
    private val hopLength = object : ValueNumber(this, "Hop length", 0.0, 500.0, 2000.0, 1.0) {
        override fun isEnabled() = future.value
    }
    private val resyncNegativity = object : ValueBoolean(this, "Resync negativity", false) {
        override fun isEnabled() = future.value
    }

    private var prevTime = 0L

    private var prevShifted = 0L
    var shifted = 0L

    private var didHit = false

    private val autoChargeDelay = TimeUtil()
    private var prevUnchargePressed = false

    override fun onEnable() {
        shifted = 0L
        prevShifted = 0L
    }

    val eventConsumer = Consumer<Event> { event ->
        when (event) {
            is EventUpdate -> {
                when (event.state) {
                    EventUpdate.State.PRE -> {
                        if (shifted > prevShifted) event.cancelled = true
                    }
                    EventUpdate.State.POST -> { // doing it in post means, that we skip as soon as we get it, otherwise we get a one tick delay
                        if (shifted >= prevShifted && skipCooldown.value)
                            shifted = max(min(0L, shifted), shifted - ceil((0.9 - MinecraftClient.getInstance().player?.getAttackCooldownProgress(0.5F)!!).coerceAtLeast(0.0) * MinecraftClient.getInstance().player?.attackCooldownProgressPerTick!! * ((mc as IMinecraftClient).tarasande_getRenderTickCounter() as IRenderTickCounter).tarasande_getTickTime()).toLong())
                    }
                    else -> {}
                }
            }

            is EventAttackEntity -> {
                if (event.state != EventAttackEntity.State.PRE) return@Consumer
                if (event.entity is LivingEntity) {
                    didHit = true
                    if (rapidFire.value) {
                        shifted = max(min(0L, shifted), shifted - (if (rapidInstantUncharge.value) shifted else (10 * ((mc as IMinecraftClient).tarasande_getRenderTickCounter() as IRenderTickCounter).tarasande_getTickTime())).toLong())
                    }
                }
            }

            is EventKeyBindingIsPressed -> {
                if (defensive.value) {
                    if (shifted < prevShifted) {
                        if (didHit && PlayerUtil.movementKeys.contains(event.keyBinding)) {
                            event.pressed = !event.pressed
                        }
                    } else {
                        didHit = false
                    }
                }
            }

            is EventTimeTravel -> {
                if (mc.player != null) {
                    prevShifted = shifted

                    if (!unchargeKey.isPressed()) {
                        if (prevUnchargePressed)
                            autoChargeDelay.reset()

                        if (chargeKey.isPressed())
                            shifted += event.time - prevTime
                        else if (autoCharge.value && minimum.value > shifted && autoChargeDelay.hasReached(delay.value.toLong()))
                            shifted += min(event.time - prevTime, (minimum.value - shifted).toLong())
                        else if(future.value && resyncNegativity.value && shifted < 0L)
                            shifted += min(event.time - prevTime, -shifted)
                    }
                    if (resyncPositions.value && prevShifted < shifted) {
                        val iRenderTickCounter = (mc as IMinecraftClient).tarasande_getRenderTickCounter() as IRenderTickCounter
                        for (i in 0..floor((event.time - iRenderTickCounter.tarasande_getPrevTimeMillis()) / iRenderTickCounter.tarasande_getTickTime()).toInt())
                            mc.world?.tickEntities()
                    }
                    if (unchargeKey.isPressed()) {
                        if(shifted > 0L) {
                            shifted = if (instantUncharge.value) 0L else max(0L, (shifted - unchargeSpeed.value * (RenderUtil.deltaTime / 100.0)).toLong())
                        } else if(future.value && negativeUncharge.value) {
                            shifted -= event.time - prevTime
                        }
                    }

                    if(future.value)
                        for(i in 0 until futureHop.wasPressed())
                            shifted -= hopLength.value.toLong()

                    prevUnchargePressed = unchargeKey.isPressed()
                } else {
                    shifted = 0L
                }
                prevTime = event.time
                event.time -= shifted
            }
        }
    }

}