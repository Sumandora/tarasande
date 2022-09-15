package su.mandora.tarasande.module.movement

import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.event.Priority
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventMovement
import su.mandora.tarasande.mixin.accessor.IVec3d
import su.mandora.tarasande.module.combat.ModuleKillAura
import su.mandora.tarasande.util.math.rotation.RotationUtil
import su.mandora.tarasande.util.player.PlayerUtil
import su.mandora.tarasande.value.ValueNumber
import java.util.function.Consumer
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

class ModuleTargetStrafe : Module("Target strafe", "Strafes around a target in a circle", ModuleCategory.MOVEMENT) {

    private val radius = ValueNumber(this, "Radius", 0.0, 1.0, 6.0, 0.1)

    @Priority(2000)
    val eventConsumer = Consumer<Event> { event ->
        if (event is EventMovement) {
            if (event.entity != mc.player)
                return@Consumer
            if (PlayerUtil.input.movementInput?.lengthSquared() == 0.0f)
                return@Consumer

            val moduleKillAura = TarasandeMain.get().managerModule?.get(ModuleKillAura::class.java)!!
            val enemy = if (moduleKillAura.enabled && moduleKillAura.targets.isNotEmpty()) moduleKillAura.targets[0].first else if (mc.crosshairTarget?.type == HitResult.Type.ENTITY && mc.crosshairTarget is EntityHitResult) (mc.crosshairTarget as EntityHitResult).entity else null

            if (enemy == null)
                return@Consumer

            val curPos = mc.player?.pos!!
            val center = enemy.pos
            val selfSpeed = max(event.velocity.horizontalLength(), TarasandeMain.get().managerModule?.get(ModuleSpeed::class.java)?.calcSpeed()!!)

            val angleOffset = Math.toDegrees(selfSpeed / radius.value)
            var angle = Math.toRadians(RotationUtil.getYaw(curPos.subtract(center)) + angleOffset)

            val newPos = Vec3d(
                center.x - radius.value * sin(angle),
                center.y,
                center.z + radius.value * cos(angle)
            )

            val rotation = RotationUtil.getRotations(curPos, newPos)
            val forward = rotation.forwardVector(selfSpeed)
            if (!TarasandeMain.get().managerModule?.get(ModuleFlight::class.java)?.let { it.allowVertical.isEnabled() && it.allowVertical.value }!!)
                (forward as IVec3d).tarasande_setY(event.velocity.y)
            else {
                val absY = abs(event.velocity.y)
                (forward as IVec3d).tarasande_setY(MathHelper.clamp(forward.y, -absY, absY))
            }

            event.velocity = forward
        }
    }
}