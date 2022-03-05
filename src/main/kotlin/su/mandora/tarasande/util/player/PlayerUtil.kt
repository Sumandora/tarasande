package su.mandora.tarasande.util.player

import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.mob.Monster
import net.minecraft.entity.passive.AnimalEntity
import net.minecraft.entity.passive.TameableEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.RaycastContext
import net.minecraft.world.RaycastContext.FluidHandling
import net.minecraft.world.RaycastContext.ShapeType
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.event.EventIsEntityAttackable
import su.mandora.tarasande.mixin.accessor.IGameRenderer
import su.mandora.tarasande.mixin.accessor.ILivingEntity
import su.mandora.tarasande.mixin.accessor.IWorld
import su.mandora.tarasande.util.math.rotation.Rotation
import su.mandora.tarasande.util.math.rotation.RotationUtil

object PlayerUtil {

    fun isAttackable(entity: Entity?): Boolean {
        var attackable = true
        if (entity == null)
            attackable = false
        else if (entity !is LivingEntity)
            attackable = false
        else if (!TarasandeMain.get().clientValues?.targets?.isSelected(0)!! && entity is PlayerEntity)
            attackable = false
        else if (TarasandeMain.get().clientValues?.dontAttackTamedEntities?.value!! && entity is TameableEntity && entity.ownerUuid == MinecraftClient.getInstance().player?.uuid)
            attackable = false
        else if (!TarasandeMain.get().clientValues?.targets?.isSelected(1)!! && entity is AnimalEntity)
            attackable = false
        else if (!TarasandeMain.get().clientValues?.targets?.isSelected(2)!! && ((entity is MobEntity || entity is Monster) && entity !is AnimalEntity))
            attackable = false
        else if (!TarasandeMain.get().clientValues?.targets?.isSelected(3)!! && (entity !is PlayerEntity && entity !is AnimalEntity && entity !is MobEntity))
            attackable = false
        else if (entity == MinecraftClient.getInstance().player)
            attackable = false
        else if (entity.isDead)
            attackable = false

        val eventIsEntityAttackable = EventIsEntityAttackable(entity, attackable)
        TarasandeMain.get().managerEvent?.call(eventIsEntityAttackable)
        return eventIsEntityAttackable.attackable
    }

    fun getTargetedEntity(reach: Double, rotation: Rotation): HitResult? {
        val gameRenderer = MinecraftClient.getInstance().gameRenderer
        val accessor = (gameRenderer as IGameRenderer)

        val prevAllowThroughWalls = accessor.isAllowThroughWalls
        val prevReach = accessor.reach
        val prevReachExtension = accessor.isDisableReachExtension

        accessor.isAllowThroughWalls = true
        accessor.reach = reach
        accessor.isDisableReachExtension = true

        val prevCrosshairTarget = MinecraftClient.getInstance().crosshairTarget
        val prevTargetedEntity = MinecraftClient.getInstance().targetedEntity

        val prevCameraEntity = MinecraftClient.getInstance().cameraEntity
        MinecraftClient.getInstance().cameraEntity = MinecraftClient.getInstance().player // not using setter to avoid shader unloading, this is purely math, no rendering

        val prevYaw = MinecraftClient.getInstance().player?.yaw!!
        val prevPitch = MinecraftClient.getInstance().player?.pitch!!

        MinecraftClient.getInstance().player?.yaw = rotation.yaw
        MinecraftClient.getInstance().player?.pitch = rotation.pitch

        val prevFakeRotation = RotationUtil.fakeRotation
        RotationUtil.fakeRotation = null // prevent rotationvec override by mixin

        gameRenderer.updateTargetedEntity(1.0f)
        val hitResult = MinecraftClient.getInstance().crosshairTarget

        RotationUtil.fakeRotation = prevFakeRotation

        MinecraftClient.getInstance().player?.yaw = prevYaw
        MinecraftClient.getInstance().player?.pitch = prevPitch

        MinecraftClient.getInstance().cameraEntity = prevCameraEntity

        MinecraftClient.getInstance().crosshairTarget = prevCrosshairTarget
        MinecraftClient.getInstance().targetedEntity = prevTargetedEntity

        accessor.reach = prevReach
        accessor.isAllowThroughWalls = prevAllowThroughWalls
        accessor.isDisableReachExtension = prevReachExtension

        return hitResult
    }

    fun simulateAttack(target: LivingEntity): Float {
        val player = MinecraftClient.getInstance().player!!
        val iLivingEntity = player as ILivingEntity

        val prevIsClient = MinecraftClient.getInstance().world?.isClient()!!
        (MinecraftClient.getInstance().world as IWorld).setIsClient(false)

        val prevVelocity = player.velocity

        val prevSprinting = player.isSprinting

        val prevTicksSinceSprintingChanged = player.ticksSinceSprintingChanged

        val prevOnFire = target.isOnFire

        val prevExhaustion = player.hungerManager.exhaustion

        val prevHurtTime = target.hurtTime

        val prevLastAttackedTicks = iLivingEntity.lastAttackedTicks

        val prevSelfHealth = player.health

        val prevHealth = target.health

        player.attack(target)
        val healthLoss = prevHealth - target.health

        target.health = prevHealth

        player.health = prevSelfHealth

        iLivingEntity.lastAttackedTicks = prevLastAttackedTicks

        target.hurtTime = prevHurtTime

        player.hungerManager.exhaustion = prevExhaustion

        target.isOnFire = prevOnFire

        player.ticksSinceSprintingChanged = prevTicksSinceSprintingChanged

        player.isSprinting = prevSprinting

        player.velocity = prevVelocity

        (MinecraftClient.getInstance().world as IWorld).setIsClient(prevIsClient)

        return healthLoss
    }

    fun rayCast(start: Vec3d, end: Vec3d) = MinecraftClient.getInstance().world?.raycast(RaycastContext(start, end, ShapeType.OUTLINE, FluidHandling.NONE, MinecraftClient.getInstance().player!!))

    fun canVectorBeSeen(start: Vec3d, end: Vec3d): Boolean {
        val hitResult = rayCast(start, end)
        return hitResult != null && hitResult.type != HitResult.Type.BLOCK
    }

    fun getMoveDirection() =
        Math.toRadians(
            RotationUtil.getYaw(
                if (MinecraftClient.getInstance().options.leftKey.isPressed && MinecraftClient.getInstance().options.rightKey.isPressed) 0.0 else if (MinecraftClient.getInstance().options.leftKey.isPressed) 1.0 else if (MinecraftClient.getInstance().options.rightKey.isPressed) -1.0 else 0.0,
                if (MinecraftClient.getInstance().options.forwardKey.isPressed && MinecraftClient.getInstance().options.backKey.isPressed) 0.0 else if (MinecraftClient.getInstance().options.forwardKey.isPressed) 1.0 else if (MinecraftClient.getInstance().options.backKey.isPressed) -1.0 else 0.0
            ) + 90 + MinecraftClient.getInstance().player?.yaw!!
        )
}