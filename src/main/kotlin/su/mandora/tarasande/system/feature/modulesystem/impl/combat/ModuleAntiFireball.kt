package su.mandora.tarasande.system.feature.modulesystem.impl.combat

import net.minecraft.entity.projectile.FireballEntity
import su.mandora.tarasande.event.impl.EventAttack
import su.mandora.tarasande.event.impl.EventRotation
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.util.DEFAULT_REACH
import su.mandora.tarasande.util.extension.javaruntime.clearAndGC
import su.mandora.tarasande.util.extension.minecraft.math.minus
import su.mandora.tarasande.util.extension.minecraft.prevPos
import su.mandora.tarasande.util.math.MathUtil
import su.mandora.tarasande.util.math.TimeUtil
import su.mandora.tarasande.util.math.rotation.RotationUtil
import su.mandora.tarasande.util.maxReach
import su.mandora.tarasande.util.player.PlayerUtil

class ModuleAntiFireball : Module("Anti fireball", "Hits fireballs to reflect them", ModuleCategory.COMBAT) {

    private val reach = ValueNumber(this, "Reach", 0.1, DEFAULT_REACH, maxReach, 0.1)
    private val delay = ValueNumber(this, "Delay", 0.0, 200.0, 1000.0, 50.0)
    private val rotate = ValueMode(this, "Rotate", false, "At fireball", "At enemy", "Disable")
    private val throughWalls = ValueBoolean(this, "Through walls", false)

    private val targets = ArrayList<FireballEntity>()

    private val timeUtil = TimeUtil()

    override fun onDisable() {
        targets.clearAndGC()
    }

    init {
        registerEvent(EventRotation::class.java) { event ->
            for (entity in mc.world?.entities?.filterIsInstance<FireballEntity>() ?: return@registerEvent) {
                val aimPoint = MathUtil.getBestAimPoint(entity.boundingBox.expand(entity.targetingMargin.toDouble()))
                if (aimPoint.squaredDistanceTo(mc.player?.eyePos!!) > reach.value * reach.value)
                    continue
                if ((entity.prevPos() - mc.player?.eyePos!!).horizontalLength() <= (entity.pos - mc.player?.eyePos!!).horizontalLength())
                    continue

                if (!targets.contains(entity)) {
                    targets.add(entity)
                }

                if (!throughWalls.value && !PlayerUtil.canVectorBeSeen(mc.player?.eyePos!!, aimPoint))
                    continue

                when {
                    rotate.isSelected(0) -> event.rotation = RotationUtil.getRotations(mc.player!!.eyePos, aimPoint).correctSensitivity()
                    rotate.isSelected(1) -> event.rotation = RotationUtil.getRotations(mc.player!!.eyePos, (mc.world!!.entities.filter { PlayerUtil.isAttackable(it) && PlayerUtil.canVectorBeSeen(mc.player?.eyePos!!, it.eyePos) }.minByOrNull { mc.player!!.distanceTo(it) } ?: return@registerEvent).boundingBox.center).correctSensitivity()
                }
            }
        }
        registerEvent(EventAttack::class.java, 999) { event ->
            if (event.dirty)
                return@registerEvent
            val iterator = targets.iterator()

            while (iterator.hasNext()) {
                val next = iterator.next()
                if (timeUtil.hasReached(delay.value.toLong())) {
                    PlayerUtil.attack(next)
                    timeUtil.reset()
                    iterator.remove()
                }
            }
        }
    }

}