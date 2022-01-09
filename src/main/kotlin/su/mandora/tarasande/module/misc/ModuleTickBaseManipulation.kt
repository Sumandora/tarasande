package su.mandora.tarasande.module.misc

import net.minecraft.entity.LivingEntity
import net.minecraft.util.math.MathHelper
import org.lwjgl.glfw.GLFW
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventTimeTravel
import su.mandora.tarasande.mixin.accessor.ILivingEntity
import su.mandora.tarasande.mixin.accessor.IMinecraftClient
import su.mandora.tarasande.mixin.accessor.IRenderTickCounter
import su.mandora.tarasande.value.ValueBoolean
import su.mandora.tarasande.value.ValueKeyBind
import java.util.function.Consumer

class ModuleTickBaseManipulation : Module("Tick base manipulation", "Shifts minecraft's tick base", ModuleCategory.MISC) {

    private val chargeKey = ValueKeyBind(this, "Charge key", GLFW.GLFW_KEY_UNKNOWN)
    private val unchargeKey = ValueKeyBind(this, "Uncharge key", GLFW.GLFW_KEY_UNKNOWN)
    private val resyncPositions = ValueBoolean(this, "Resync positions", true)

    private var prevTime = 0L
    private var lastUpdate = 0L

    var shifted = 0L

    val eventConsumer = Consumer<Event> { event ->
        if (event is EventTimeTravel) {
            if (mc.player != null) {
                if (chargeKey.isPressed()) {
                    shifted += event.time - prevTime

                    if (resyncPositions.value) {
                        if (event.time - lastUpdate > ((mc as IMinecraftClient).renderTickCounter as IRenderTickCounter).tickTime) {
                            for(i in 0..((event.time - lastUpdate) / ((mc as IMinecraftClient).renderTickCounter as IRenderTickCounter).tickTime).toInt()) {
                                for (entity in mc.world?.entities!!) {
                                    if (entity is LivingEntity && entity != mc.player) {
//                                        entity as ILivingEntity
//                                        entity.setPosition(entity.serverX, entity.serverY, entity.serverZ)
//                                        entity.yaw += MathHelper.wrapDegrees(entity.serverYaw.toFloat() - entity.getYaw())
//                                        entity.pitch = entity.serverPitch.toFloat()
                                        entity.tickMovement()
                                    }
                                }
                            }
                            lastUpdate = event.time
                        }
                    }
                }
                prevTime = event.time
                if (unchargeKey.isPressed()) {
                    shifted = 0L
                }
            } else {
                shifted = 0L
            }
            event.time -= shifted
        }
    }

}