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
import su.mandora.tarasande.util.player.PlayerUtil
import su.mandora.tarasande.value.ValueBind
import su.mandora.tarasande.value.ValueBoolean
import su.mandora.tarasande.value.ValueNumber
import java.util.function.Consumer
import kotlin.math.ceil
import kotlin.math.max

class ModuleTickBaseManipulation : Module("Tick base manipulation", "Shifts minecraft's tick base", ModuleCategory.MISC) {

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

    private var prevTime = 0L
    private var lastUpdate = 0L

    private var prevShifted = 0L
    var shifted = 0L

    private var didHit = false

    override fun onEnable() {
        shifted = 0L
        prevShifted = 0L
    }

    val eventConsumer = Consumer<Event> { event ->
        when (event) {
            is EventUpdate -> {
                if (event.state == EventUpdate.State.POST) // doing it in post means, that we skip as soon as we get it, otherwise we get a one tick delay
                    if (shifted >= prevShifted && skipCooldown.value)
                        shifted = max(0L, shifted - ceil((0.9 - MinecraftClient.getInstance().player?.getAttackCooldownProgress(0.5F)!!).coerceAtLeast(0.0) * MinecraftClient.getInstance().player?.attackCooldownProgressPerTick!! * ((mc as IMinecraftClient).renderTickCounter as IRenderTickCounter).tickTime).toLong())
            }
            is EventAttackEntity -> {
                if (event.state != EventAttackEntity.State.PRE) return@Consumer
                if (event.entity is LivingEntity) {
                    didHit = true
                    if (rapidFire.value) {
                        shifted = max(0L, shifted - (if (rapidInstantUncharge.value) shifted else (10 * ((mc as IMinecraftClient).renderTickCounter as IRenderTickCounter).tickTime)).toLong())
                    }
                }
            }
            is EventKeyBindingIsPressed -> {
                if (defensive.value) {
                    if (shifted < prevShifted && didHit) {
                        if (PlayerUtil.movementKeys.contains(event.keyBinding)) {
                            event.pressed = !event.pressed
                        }
                    }
                }
            }
            is EventTimeTravel -> {
                if (mc.player != null) {
                    didHit = false
                    prevShifted = shifted
                    if (chargeKey.isPressed()) {
                        shifted += event.time - prevTime

                        if (resyncPositions.value) {
                            val iRenderTickCounter = (mc as IMinecraftClient).renderTickCounter as IRenderTickCounter
                            if (event.time - lastUpdate > iRenderTickCounter.tickTime) {
                                for (i in 0..((event.time - lastUpdate) / iRenderTickCounter.tickTime).toInt()) {
                                    for (entity in mc.world?.entities!!) {
                                        if (entity != mc.player) {
                                            //                                        entity as ILivingEntity
                                            //                                        entity.setPosition(entity.serverX, entity.serverY, entity.serverZ)
                                            //                                        entity.yaw += MathHelper.wrapDegrees(entity.serverYaw.toFloat() - entity.getYaw())
                                            //                                        entity.pitch = entity.serverPitch.toFloat()
                                            entity.tick()
                                        }
                                    }
                                }
                                lastUpdate = event.time
                            }
                        }
                    }
                    prevTime = event.time
                    if (unchargeKey.isPressed())
                        shifted = if (instantUncharge.value) 0L else max(0L, (shifted - unchargeSpeed.value).toLong())
                } else {
                    shifted = 0L
                }
                event.time -= shifted
            }
        }
    }

}