package net.tarasandedevelopment.tarasande.module.movement

import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.event.Event
import net.tarasandedevelopment.tarasande.base.event.Priority
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventMovement
import net.tarasandedevelopment.tarasande.event.EventUpdate
import net.tarasandedevelopment.tarasande.mixin.accessor.IVec3d
import net.tarasandedevelopment.tarasande.module.combat.ModuleKillAura
import net.tarasandedevelopment.tarasande.util.extension.minus
import net.tarasandedevelopment.tarasande.util.math.rotation.RotationUtil
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil
import net.tarasandedevelopment.tarasande.value.ValueNumber
import java.util.function.Consumer
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

class ModuleTargetStrafe : Module("Target strafe", "Strafes around a target in a circle", ModuleCategory.MOVEMENT) {

    private val radius = ValueNumber(this, "Radius", 0.0, 1.0, 6.0, 0.1)

    private var invert = false

    @Priority(2000)
    val eventConsumer = Consumer<Event> { event ->
        if (event is EventUpdate) {
            if (event.state == EventUpdate.State.PRE)
                if (mc.player?.horizontalCollision == true)
                    invert = !invert
        } else if (event is EventMovement) {
            if (event.entity != mc.player)
                return@Consumer
            if (PlayerUtil.input.movementInput?.lengthSquared() == 0.0f)
                return@Consumer

            val moduleKillAura = TarasandeMain.get().managerModule.get(ModuleKillAura::class.java)
            val enemy =
                if (moduleKillAura.enabled && moduleKillAura.targets.isNotEmpty())
                    moduleKillAura.targets[0].first
                else if (mc.crosshairTarget?.type == HitResult.Type.ENTITY && mc.crosshairTarget is EntityHitResult) {
                    val ent = (mc.crosshairTarget as EntityHitResult).entity
                    if (PlayerUtil.isAttackable(ent))
                        ent
                    else
                        null
                } else
                    null

            if (enemy == null)
                return@Consumer

            val curPos = mc.player?.pos!!
            val center = enemy.pos
            val selfSpeed = max(event.velocity.horizontalLength(), PlayerUtil.calcBaseSpeed(PlayerUtil.walkSpeed))

            var angleOffset = Math.toDegrees(selfSpeed / radius.value)
            if (invert)
                angleOffset *= -1
            val angle = Math.toRadians(RotationUtil.getYaw(curPos - center) + angleOffset)

            val newPos = Vec3d(
                center.x - radius.value * sin(angle),
                center.y,
                center.z + radius.value * cos(angle)
            )

            val rotation = RotationUtil.getRotations(curPos, newPos)
            val forward = rotation.forwardVector(selfSpeed)
            val moduleFlight = TarasandeMain.get().managerModule.get(ModuleFlight::class.java)
            if (moduleFlight.let { !it.enabled || !(it.mode.isSelected(0) || it.mode.isSelected(1)) })
                (forward as IVec3d).tarasande_setY(event.velocity.y)
            else {
                (forward as IVec3d).tarasande_setY(MathHelper.clamp(forward.y, -moduleFlight.flightSpeed.value, moduleFlight.flightSpeed.value))
            }

            event.velocity = forward
        }
    }
}