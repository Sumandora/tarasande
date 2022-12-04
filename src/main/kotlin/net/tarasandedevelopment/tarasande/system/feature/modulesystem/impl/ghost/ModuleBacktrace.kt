package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.ghost

import net.minecraft.entity.Entity
import net.minecraft.util.math.Box
import net.minecraft.util.shape.VoxelShapes
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.event.EventAttackEntity
import net.tarasandedevelopment.tarasande.event.EventBoundingBoxOverride
import net.tarasandedevelopment.tarasande.event.EventRender3D
import net.tarasandedevelopment.tarasande.event.EventUpdate
import net.tarasandedevelopment.tarasande.injection.accessor.IGameRenderer
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueColor
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.misc.ModuleBlink
import net.tarasandedevelopment.tarasande.util.extension.plus
import net.tarasandedevelopment.tarasande.util.math.MathUtil
import net.tarasandedevelopment.tarasande.util.math.rotation.Rotation
import net.tarasandedevelopment.tarasande.util.math.rotation.RotationUtil
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil
import net.tarasandedevelopment.tarasande.util.render.RenderUtil

class ModuleBacktrace : Module("Backtrace", "Allows you to trace back enemy hit boxes", ModuleCategory.GHOST) {

    private val ticks = ValueNumber(this, "Ticks", 1.0, 5.0, 20.0, 1.0)
    private val defaultColor = ValueColor(this, "Default color", 0.0f, 1.0f, 1.0f, 1.0f)
    private val selectedColor = ValueColor(this, "Selected color", 0.0f, 1.0f, 1.0f, 1.0f)
    private val blinkResync = ValueBoolean(this, "Blink resync", true)
    private val removeInvalidRecords = ValueBoolean(this, "Remove invalid records", true)

    private val boundingBoxes = HashMap<Entity, ArrayList<Box>>()

    private fun computeSelectedBox(entity: Entity): Box? {
        val playerRotation = RotationUtil.fakeRotation ?: Rotation(mc.player!!)
        val playerEye = mc.player?.eyePos
        val rotationVec = mc.player?.eyePos!! + playerRotation.forwardVector((mc.gameRenderer as IGameRenderer).tarasande_getReach())
        return boundingBoxes[entity]?.filter { it.raycast(playerEye, rotationVec).isPresent }?.minByOrNull { playerEye?.squaredDistanceTo(MathUtil.closestPointToBox(playerEye, it))!! }
    }

    init {
        registerEvent(EventBoundingBoxOverride::class.java) { event ->
            event.boundingBox = computeSelectedBox(event.entity) ?: return@registerEvent
        }

        registerEvent(EventUpdate::class.java) { event ->
            if (event.state == EventUpdate.State.PRE) {
                boundingBoxes.entries.removeIf { mc.world?.entities?.contains(it.key) != true }
                for (entity in mc.world?.entities!!)
                    if (PlayerUtil.isAttackable(entity))
                        boundingBoxes.computeIfAbsent(entity) { ArrayList() }.also {
                            if (entity.boundingBox != null)
                                it.add(entity.boundingBox)
                            while ((it.size - 1) > ticks.value)
                                it.removeAt(0)
                        }
            }
        }

        registerEvent(EventRender3D::class.java) { event ->
            boundingBoxes.forEach {
                it.value.forEach { box ->
                    RenderUtil.blockOutline(event.matrices, VoxelShapes.cuboid(box), if(computeSelectedBox(it.key) == box) selectedColor.getColor().rgb else defaultColor.getColor().rgb)
                }
            }
        }

        registerEvent(EventAttackEntity::class.java) { event ->
            val box = computeSelectedBox(event.entity) ?: return@registerEvent
            val list = boundingBoxes[event.entity] ?: return@registerEvent

            val moduleBlink = TarasandeMain.managerModule().get(ModuleBlink::class.java)
            if(blinkResync.value && moduleBlink.enabled) {
                val index = list.size - list.indexOf(box)
                val time = (index - 1) * mc.renderTickCounter.tickTime
                moduleBlink.onDisable(false, cancelled = false, timeOffset = time.toLong())
            }

            if(removeInvalidRecords.value) {
                list.removeIf { list.indexOf(it) <= list.indexOf(box) }
            }
        }
    }

}