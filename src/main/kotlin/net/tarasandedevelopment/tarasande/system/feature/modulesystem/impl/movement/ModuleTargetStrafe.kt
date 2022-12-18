package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.movement

import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.event.EventMovement
import net.tarasandedevelopment.tarasande.event.EventUpdate
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.combat.ModuleKillAura
import net.tarasandedevelopment.tarasande.util.extension.minecraft.minus
import net.tarasandedevelopment.tarasande.util.math.rotation.RotationUtil
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil
import kotlin.math.cos
import kotlin.math.sin

class ModuleTargetStrafe : Module("Target strafe", "Strafes around a target in a circle", ModuleCategory.MOVEMENT) {

    private val radius = ValueNumber(this, "Radius", 0.0, 1.0, 6.0, 0.1)
    private val maximumFallDistance = ValueNumber(this, "Maximum fall distance", 0.0, 5.0, 15.0, 0.1)

    private var invert = false

    private fun calculateNextPosition(selfSpeed: Double, curPos: Vec3d, center: Vec3d): Vec3d {
        var angleOffset = selfSpeed / radius.value // I have no clue, why this is wrong... TODO
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

            if (PlayerUtil.input.movementInput?.lengthSquared() == 0.0F)
                return@registerEvent

            val moduleKillAura = TarasandeMain.managerModule().get(ModuleKillAura::class.java)
            val enemy =
                if (moduleKillAura.enabled && moduleKillAura.targets.isNotEmpty())
                    moduleKillAura.targets[0].first
                else if (mc.crosshairTarget?.type == HitResult.Type.ENTITY && mc.crosshairTarget is EntityHitResult) {
                    (mc.crosshairTarget as EntityHitResult).entity
                } else
                    null

            if (enemy == null)
                return@registerEvent

            val selfSpeed = event.velocity.horizontalLength()
            val curPos = mc.player?.pos!!
            val center = enemy.pos

            var newPos = calculateNextPosition(selfSpeed, curPos, center)

            if (PlayerUtil.predictFallDistance(BlockPos(newPos)).let { it == null || it > maximumFallDistance.value }) {
                invert = !invert
                newPos = calculateNextPosition(selfSpeed, curPos, center)
            }

            val rotation = RotationUtil.getRotations(curPos, newPos)
            val forward = rotation.forwardVector(selfSpeed)
            val moduleFlight = TarasandeMain.managerModule().get(ModuleFlight::class.java)

            if (moduleFlight.let { !it.enabled || !(it.mode.isSelected(0) || it.mode.isSelected(1)) })
                forward.y = event.velocity.y
            else
                forward.y = MathHelper.clamp(forward.y, -moduleFlight.flightSpeed.value, moduleFlight.flightSpeed.value)

            event.velocity = forward
        }
    }
}
