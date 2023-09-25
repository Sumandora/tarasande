package su.mandora.tarasande.system.feature.modulesystem.impl.combat

import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.math.Direction
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import su.mandora.tarasande.event.impl.EventMovement
import su.mandora.tarasande.event.impl.EventUpdate
import su.mandora.tarasande.feature.rotation.api.RotationUtil
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.system.feature.modulesystem.impl.movement.ModuleFlight
import su.mandora.tarasande.util.extension.minecraft.isEntityHitResult
import su.mandora.tarasande.util.extension.minecraft.math.BlockPos
import su.mandora.tarasande.util.extension.minecraft.math.minus
import su.mandora.tarasande.util.extension.minecraft.math.times
import su.mandora.tarasande.util.maxReach
import su.mandora.tarasande.util.player.PlayerUtil
import kotlin.math.cos
import kotlin.math.sin

class ModuleTargetStrafe : Module("Target strafe", "Circle-strafes around targets", ModuleCategory.COMBAT) {

    private val radius = ValueNumber(this, "Radius", 0.0, 1.0, maxReach, 0.1)
    private val maximumFallDistance = ValueNumber(this, "Maximum fall distance", 0.0, 5.0, 15.0, 0.1)

    private var invert = false

    private val moduleFlight by lazy { ManagerModule.get(ModuleFlight::class.java) }
    private val moduleKillAura by lazy { ManagerModule.get(ModuleKillAura::class.java) }

    private fun calculateNextPosition(selfSpeed: Double, curPos: Vec3d, center: Vec3d): Vec3d {
        var angleOffset = selfSpeed / radius.value
        if (invert)
            angleOffset *= -1
        val angle = Math.toRadians(RotationUtil.getYaw(curPos - center)) + angleOffset

        return Vec3d(
            center.x - radius.value * sin(angle),
            center.y,
            center.z + radius.value * cos(angle)
        )
    }

    init {
        registerEvent(EventUpdate::class.java) { event ->
            if (event.state == EventUpdate.State.PRE)
                if (mc.player?.horizontalCollision == true)
                // I don't think we need a check whether this module is controlling movement... this just makes it more random, not a bad thing in this context
                    invert = !invert
        }

        registerEvent(EventMovement::class.java, 2000) { event ->
            if (event.entity != mc.player)
                return@registerEvent

            if (!PlayerUtil.isPlayerMoving())
                return@registerEvent

            val enemy =
                if (moduleKillAura.enabled.value && moduleKillAura.targets.isNotEmpty())
                    moduleKillAura.targets[0].first
                else if (mc.crosshairTarget.isEntityHitResult()) {
                    val entity = (mc.crosshairTarget as EntityHitResult).entity
                    if (!PlayerUtil.isAttackable(entity))
                        null
                    else
                        entity
                } else
                    null

            if (enemy == null)
                return@registerEvent

            val selfSpeed = if (event.dirty) event.velocity.horizontalLength() else PlayerUtil.calcBaseSpeed()

            val curPos = mc.player?.pos!!
            val center = enemy.pos

            var newPos = calculateNextPosition(selfSpeed, curPos, center)

            val flying = moduleFlight.enabled.value
            val freeFlying = moduleFlight.let { it.enabled.value && (it.mode.isSelected(0) || it.mode.isSelected(1)) }

            if (!flying && PlayerUtil.predictFallDistance(BlockPos(newPos)).let { it == null || it > maximumFallDistance.value }) {
                invert = !invert
                newPos = calculateNextPosition(selfSpeed, curPos, center)
            }

            val rotation = RotationUtil.getRotations(curPos, newPos).withPitch(0F)
            var forward = rotation.forwardVector() * selfSpeed

            forward = forward.withAxis(Direction.Axis.Y,
                if (freeFlying)
                    MathHelper.clamp(center.y - curPos.y, -moduleFlight.flightSpeed.value, moduleFlight.flightSpeed.value)
                else
                    event.velocity.y
            )

            event.velocity = forward
        }
    }
}
