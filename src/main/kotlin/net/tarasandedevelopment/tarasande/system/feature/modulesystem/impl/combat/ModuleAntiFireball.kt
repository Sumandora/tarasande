package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.combat

import net.minecraft.entity.projectile.FireballEntity
import net.minecraft.util.math.Vec3d
import net.tarasandedevelopment.tarasande.event.EventAttack
import net.tarasandedevelopment.tarasande.event.EventPollEvents
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory
import net.tarasandedevelopment.tarasande.util.extension.mc
import net.tarasandedevelopment.tarasande.util.extension.minecraft.minus
import net.tarasandedevelopment.tarasande.util.math.MathUtil
import net.tarasandedevelopment.tarasande.util.math.TimeUtil
import net.tarasandedevelopment.tarasande.util.math.rotation.RotationUtil
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil

class ModuleAntiFireball : Module("Anti fireball", "Hits fireballs to turn them", ModuleCategory.COMBAT) {

    private val reach = ValueNumber(this, "Reach", 0.1, 3.0, 6.0, 0.1)
    private val delay = ValueNumber(this, "Delay", 0.0, 200.0, 1000.0, 50.0)
    private val rotate = ValueBoolean(this, "Rotate", true)
    private val throughWalls = ValueBoolean(this, "Through walls", false)

    private val targets = ArrayList<FireballEntity>()

    private val timeUtil = TimeUtil()

    override fun onDisable() {
        targets.clear()
    }

    init {
        registerEvent(EventPollEvents::class.java) { event ->
            for (entity in mc.world?.entities?.filterIsInstance<FireballEntity>() ?: return@registerEvent) {
                val aimPoint = MathUtil.getBestAimPoint(entity.boundingBox.expand(entity.targetingMargin.toDouble()))
                if (aimPoint.squaredDistanceTo(mc.player?.eyePos!!) > reach.value * reach.value)
                    continue
                if ((Vec3d(entity.prevX, entity.prevY, entity.prevZ) - mc.player?.eyePos!!).horizontalLengthSquared() <= (entity.pos - mc.player?.eyePos!!).horizontalLengthSquared())
                    continue

                if (!targets.contains(entity)) {
                    targets.add(entity)
                }

                if (!throughWalls.value && !PlayerUtil.canVectorBeSeen(mc.player?.eyePos!!, aimPoint))
                    continue

                if (rotate.value) {
                    event.rotation = RotationUtil.getRotations(mc.player?.eyePos!!, aimPoint).correctSensitivity()
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