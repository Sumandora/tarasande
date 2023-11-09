package su.mandora.tarasande.system.feature.modulesystem.impl.ghost

import net.minecraft.entity.Entity
import net.minecraft.util.math.Box
import su.mandora.tarasande.event.impl.EventAttackEntity
import su.mandora.tarasande.event.impl.EventBoundingBoxOverride
import su.mandora.tarasande.event.impl.EventRender3D
import su.mandora.tarasande.event.impl.EventTick
import su.mandora.tarasande.feature.rotation.Rotations
import su.mandora.tarasande.feature.rotation.api.Rotation
import su.mandora.tarasande.injection.accessor.IGameRenderer
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueColor
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.system.feature.modulesystem.impl.misc.ModuleBlink
import su.mandora.tarasande.util.DEFAULT_TPS
import su.mandora.tarasande.util.extension.minecraft.math.plus
import su.mandora.tarasande.util.extension.minecraft.math.times
import su.mandora.tarasande.util.math.MathUtil
import su.mandora.tarasande.util.player.PlayerUtil
import su.mandora.tarasande.util.render.RenderUtil

class ModuleBacktrace : Module("Backtrace", "Allows you to trace back enemy hit boxes", ModuleCategory.GHOST) {

    private val ticks = ValueNumber(this, "Ticks", 1.0, DEFAULT_TPS / 4.0, DEFAULT_TPS.toDouble(), 1.0)
    private val defaultColor = ValueColor(this, "Default color", 0.0, 1.0, 1.0, 1.0)
    private val selectedColor = ValueColor(this, "Selected color", 0.0, 1.0, 1.0, 1.0)
    private val blinkResync = ValueBoolean(this, "Blink re-sync", true)
    private val removeInvalidRecords = ValueBoolean(this, "Remove invalid records", true)

    private val boundingBoxes = HashMap<Entity, ArrayList<Box>>()

    private val moduleBlink by lazy { ManagerModule.get(ModuleBlink::class.java) }

    private fun computeSelectedBox(entity: Entity): Box? {
        val playerRotation = Rotations.fakeRotation ?: Rotation(mc.player!!)
        val playerEye = mc.player?.eyePos!!
        val rotationVec = playerEye + playerRotation.forwardVector() * (mc.gameRenderer as IGameRenderer).tarasande_getReach()
        return boundingBoxes[entity]?.filter { it.raycast(playerEye, rotationVec).isPresent }?.minByOrNull { playerEye.distanceTo(MathUtil.closestPointToBox(playerEye, it)) }
    }

    override fun onDisable() {
        boundingBoxes.clear()
    }

    init {
        registerEvent(EventBoundingBoxOverride::class.java) { event ->
            event.boundingBox = computeSelectedBox(event.entity) ?: return@registerEvent
        }

        registerEvent(EventTick::class.java) { event ->
            if (event.state == EventTick.State.PRE) {
                if (mc.world == null || mc.player == null) {
                    boundingBoxes.clear()
                    return@registerEvent
                }
                boundingBoxes.entries.removeIf { !mc.world!!.entities.contains(it.key) }
                for (entity in mc.world!!.entities)
                    if (PlayerUtil.isAttackable(entity))
                        boundingBoxes.computeIfAbsent(entity) { ArrayList() }.also {
                            if (entity.boundingBox != null)
                                it.add(entity.boundingBox)
                            while ((it.size - 1) > ticks.value)
                                it.removeAt(0)
                        }
                    else
                        boundingBoxes.remove(entity)
            }
        }

        registerEvent(EventRender3D::class.java) { event ->
            if(event.state != EventRender3D.State.POST) return@registerEvent

            boundingBoxes.forEach {
                it.value.forEach { box ->
                    RenderUtil.blockOutline(event.matrices, box, if (computeSelectedBox(it.key) == box) selectedColor.getColor().rgb else defaultColor.getColor().rgb)
                }
            }
        }

        registerEvent(EventAttackEntity::class.java) { event ->
            val box = computeSelectedBox(event.entity) ?: return@registerEvent
            val list = boundingBoxes[event.entity] ?: return@registerEvent

            if (blinkResync.value && moduleBlink.enabled.value) {
                val index = list.size - list.indexOf(box)
                val time = (index - 1) * mc.renderTickCounter.tickTime
                moduleBlink.onDisable(false, cancelled = false, timeOffset = time.toLong())
            }

            if (removeInvalidRecords.value) {
                list.removeIf { list.indexOf(it) <= list.indexOf(box) }
            }
        }
    }

}
