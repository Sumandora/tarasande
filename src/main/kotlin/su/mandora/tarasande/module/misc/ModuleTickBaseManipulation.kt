package su.mandora.tarasande.module.misc

import net.minecraft.entity.LivingEntity
import org.lwjgl.glfw.GLFW
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventAttack
import su.mandora.tarasande.event.EventAttackEntity
import su.mandora.tarasande.event.EventTimeTravel
import su.mandora.tarasande.event.EventUpdate
import su.mandora.tarasande.mixin.accessor.IMinecraftClient
import su.mandora.tarasande.mixin.accessor.IRenderTickCounter
import su.mandora.tarasande.util.math.rotation.RotationUtil
import su.mandora.tarasande.util.render.RenderUtil
import su.mandora.tarasande.value.ValueBoolean
import su.mandora.tarasande.value.ValueKeyBind
import su.mandora.tarasande.value.ValueNumber
import java.util.function.Consumer
import kotlin.math.max
import kotlin.math.round
import kotlin.math.roundToInt

class ModuleTickBaseManipulation : Module("Tick base manipulation", "Shifts minecraft's tick base", ModuleCategory.MISC) {

    private val chargeKey = ValueKeyBind(this, "Charge key", GLFW.GLFW_KEY_UNKNOWN)
    private val unchargeKey = ValueKeyBind(this, "Uncharge key", GLFW.GLFW_KEY_UNKNOWN)
    private val resyncPositions = ValueBoolean(this, "Resync positions", true)
    private val instantUncharge = ValueBoolean(this, "Instant uncharge", true)
    private val unchargeSpeed = object : ValueNumber(this, "Uncharge speed", 0.0, 1000.0, 1000.0, 1.0) {
        override fun isVisible() = !instantUncharge.value
    }
    private val rapidFire = ValueBoolean(this, "Rapid fire", false)

    private var prevTime = 0L
    private var lastUpdate = 0L

    var prevShifted = 0L
    var shifted = 0L

    override fun onEnable() {
        shifted = 0L
        prevShifted = 0L
    }

    val eventConsumer = Consumer<Event> { event ->
        when (event) {
            is EventAttackEntity -> {
                if(event.entity is LivingEntity && rapidFire.value) {
                    shifted = if(instantUncharge.value)
                        0L
                    else
                        max(0L, (shifted - (event.entity.hurtTime * ((mc as IMinecraftClient).renderTickCounter as IRenderTickCounter).tickTime)).toLong())
                }
            }
            is EventAttack -> { // Aura sync
                if(shifted < prevShifted)
                    for(i in 0..(1000.0 / RenderUtil.deltaTime).roundToInt())
                        RotationUtil.updateFakeRotation()
                prevShifted = shifted
            }
            is EventTimeTravel -> {
                if (mc.player != null) {
                    if (chargeKey.isPressed()) {
                        shifted += event.time - prevTime

                        if (resyncPositions.value) {
                            val iRenderTickCounter = (mc as IMinecraftClient).renderTickCounter as IRenderTickCounter
                            if (event.time - lastUpdate > iRenderTickCounter.tickTime) {
                                for(i in 0..((event.time - lastUpdate) / iRenderTickCounter.tickTime).toInt()) {
                                    for (entity in mc.world?.entities!!) {
                                        if (entity is LivingEntity && entity != mc.player) {
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
                        shifted = if(instantUncharge.value) 0L else max(0L, (shifted - unchargeSpeed.value).toLong())
                } else {
                    shifted = 0L
                }
                event.time -= shifted
            }
        }
    }

}