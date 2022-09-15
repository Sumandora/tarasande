package su.mandora.tarasande.util.player

import net.minecraft.client.MinecraftClient
import net.minecraft.client.input.KeyboardInput
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.mob.Monster
import net.minecraft.entity.passive.AnimalEntity
import net.minecraft.entity.passive.TameableEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.util.Hand
import net.minecraft.util.UseAction
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.RaycastContext
import net.minecraft.world.RaycastContext.FluidHandling
import net.minecraft.world.RaycastContext.ShapeType
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.event.EventInput
import su.mandora.tarasande.event.EventIsEntityAttackable
import su.mandora.tarasande.mixin.accessor.IGameRenderer
import su.mandora.tarasande.mixin.accessor.IMinecraftClient
import su.mandora.tarasande.util.math.rotation.Rotation
import su.mandora.tarasande.util.math.rotation.RotationUtil

object PlayerUtil {

    val movementKeys = listOf(MinecraftClient.getInstance().options.forwardKey, MinecraftClient.getInstance().options.leftKey, MinecraftClient.getInstance().options.backKey, MinecraftClient.getInstance().options.rightKey)
    val input = KeyboardInput(MinecraftClient.getInstance().options)

    init {
        TarasandeMain.get().managerEvent?.add { event ->
            if (event is EventInput) {
                if (event.input == MinecraftClient.getInstance().player?.input) {
                    input.tick(event.slowDown, event.slowdownAmount)
                }
            }
        }
    }

    fun isAttackable(entity: Entity?): Boolean {
        var attackable = true
        if (entity == null) attackable = false
        else if (entity !is LivingEntity) attackable = false
        else if (entity == MinecraftClient.getInstance().player) attackable = false
        else if (!TarasandeMain.get().clientValues?.targets?.isSelected(0)!! && entity is PlayerEntity) attackable = false
        else if (TarasandeMain.get().clientValues?.dontAttackTamedEntities?.value!! && entity is TameableEntity && entity.ownerUuid == MinecraftClient.getInstance().player?.uuid) attackable = false
        else if (!TarasandeMain.get().clientValues?.targets?.isSelected(1)!! && entity is AnimalEntity) attackable = false
        else if (!TarasandeMain.get().clientValues?.targets?.isSelected(2)!! && ((entity is MobEntity || entity is Monster) && entity !is AnimalEntity)) attackable = false
        else if (!TarasandeMain.get().clientValues?.targets?.isSelected(3)!! && (entity !is PlayerEntity && entity !is AnimalEntity && entity !is MobEntity)) attackable = false

        val eventIsEntityAttackable = EventIsEntityAttackable(entity, attackable)
        TarasandeMain.get().managerEvent?.call(eventIsEntityAttackable)
        return eventIsEntityAttackable.attackable
    }

    fun getTargetedEntity(reach: Double, rotation: Rotation, allowThroughWalls: Boolean = true): HitResult? {
        val gameRenderer = MinecraftClient.getInstance().gameRenderer
        val accessor = (gameRenderer as IGameRenderer)
        val renderTickCounter = (MinecraftClient.getInstance() as IMinecraftClient).tarasande_getRenderTickCounter()

        val prevAllowThroughWalls = accessor.tarasande_isAllowThroughWalls()
        val prevReach = accessor.tarasande_getReach()
        val prevReachExtension = accessor.tarasande_isDisableReachExtension()

        accessor.tarasande_setAllowThroughWalls(allowThroughWalls)
        accessor.tarasande_setReach(reach)
        accessor.tarasande_setDisableReachExtension(true)

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

        val prevTickDelta = renderTickCounter.tickDelta
        renderTickCounter.tickDelta = 1.0f

        gameRenderer.updateTargetedEntity(1.0f)
        val hitResult = MinecraftClient.getInstance().crosshairTarget

        renderTickCounter.tickDelta = prevTickDelta

        RotationUtil.fakeRotation = prevFakeRotation

        MinecraftClient.getInstance().player?.yaw = prevYaw
        MinecraftClient.getInstance().player?.pitch = prevPitch

        MinecraftClient.getInstance().cameraEntity = prevCameraEntity

        MinecraftClient.getInstance().crosshairTarget = prevCrosshairTarget
        MinecraftClient.getInstance().targetedEntity = prevTargetedEntity

        accessor.tarasande_setReach(prevReach)
        accessor.tarasande_setAllowThroughWalls(prevAllowThroughWalls)
        accessor.tarasande_setDisableReachExtension(prevReachExtension)

        return hitResult
    }

    fun rayCast(start: Vec3d, end: Vec3d) = MinecraftClient.getInstance().world?.raycast(RaycastContext(start, end, ShapeType.OUTLINE, FluidHandling.NONE, MinecraftClient.getInstance().player!!))

    fun canVectorBeSeen(start: Vec3d, end: Vec3d): Boolean {
        val hitResult = rayCast(start, end)
        return hitResult != null && hitResult.type != HitResult.Type.BLOCK
    }

    fun getMoveDirection(): Double {
        return RotationUtil.getYaw(input.movementInput) + (if (input.movementInput.lengthSquared() != 0.0f) 0.0 else 90.0) + MinecraftClient.getInstance().player?.yaw!!
    }

    fun isOnEdge(extrapolation: Double) = MinecraftClient.getInstance().world?.isSpaceEmpty(MinecraftClient.getInstance().player, MinecraftClient.getInstance().player?.boundingBox?.offset(MinecraftClient.getInstance().player?.velocity?.x!! * extrapolation, -MinecraftClient.getInstance().player?.stepHeight?.toDouble()!!, MinecraftClient.getInstance().player?.velocity?.z!! * extrapolation))!!

    fun getUsedHand(): Hand? {
        for (hand in Hand.values()) {
            val stack = MinecraftClient.getInstance().player?.getStackInHand(hand)
            if (stack != null && stack.item != Items.AIR) {
                if (stack.useAction == UseAction.NONE) continue

                return hand
            }
        }
        return null
    }
}