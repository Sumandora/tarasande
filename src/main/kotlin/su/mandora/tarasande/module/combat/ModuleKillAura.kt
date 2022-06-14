package su.mandora.tarasande.module.combat

import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.passive.PassiveEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.item.ShieldItem
import net.minecraft.item.SwordItem
import net.minecraft.util.Hand
import net.minecraft.util.UseAction
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.Box
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.*
import su.mandora.tarasande.mixin.accessor.IClientPlayerEntity
import su.mandora.tarasande.mixin.accessor.IKeyBinding
import su.mandora.tarasande.mixin.accessor.ILivingEntity
import su.mandora.tarasande.mixin.accessor.IMinecraftClient
import su.mandora.tarasande.util.math.MathUtil
import su.mandora.tarasande.util.math.rotation.Rotation
import su.mandora.tarasande.util.math.rotation.RotationUtil
import su.mandora.tarasande.util.player.PlayerUtil
import su.mandora.tarasande.util.player.clickspeed.ClickSpeedUtil
import su.mandora.tarasande.util.render.RenderUtil
import su.mandora.tarasande.value.ValueBoolean
import su.mandora.tarasande.value.ValueMode
import su.mandora.tarasande.value.ValueNumber
import su.mandora.tarasande.value.ValueNumberRange
import java.util.concurrent.ThreadLocalRandom
import java.util.function.Consumer
import kotlin.math.min
import kotlin.math.sqrt

class ModuleKillAura : Module("Kill aura", "Automatically attacks near players", ModuleCategory.COMBAT) {

    private val mode = ValueMode(this, "Mode", false, "Single", "Multi")
    private val priority = ValueMode(this, "Priority", false, "Distance", "Health", "Hurt Time", "FOV")
    private val fov = ValueNumber(this, "FOV", 0.0, 255.0, 255.0, 1.0)
    private val reach = ValueNumberRange(this, "Reach", 0.1, 3.0, 4.0, 6.0, 0.1)
    private val clickSpeedUtil = ClickSpeedUtil(this, { true }) // for setting order
    private val rayTrace = ValueBoolean(this, "Ray trace", false)
    private val simulateMouseDelay = object : ValueBoolean(this, "Simulate mouse delay", false) {
        override fun isEnabled() = rayTrace.value && !mode.isSelected(1)
    }
    private val swingInAir = ValueBoolean(this, "Swing in air", true)
    private val aimSpeed = ValueNumberRange(this, "Aim speed", 0.0, 1.0, 1.0, 1.0, 0.1)
    private val dontAttackWhenBlocking = ValueBoolean(this, "Don't attack when blocking", false)
    private val simulateShieldBlock = object : ValueBoolean(this, "Simulate shield block", false) {
        override fun isEnabled() = dontAttackWhenBlocking.value
    }
    private val throughWalls = ValueBoolean(this, "Through walls", false)
    private val attackCooldown = ValueBoolean(this, "Attack cooldown", false)
    private val blockMode = object : ValueMode(this, "Auto block", false, "Disabled", "Permanent", "Legit") {
        override fun onChange() {
            blocking = false
        }
    }
    private val needUnblock = object : ValueBoolean(this, "Need unblock", true) {
        override fun isEnabled() = blockMode.isSelected(1)
    }
    private val blockCheckMode = object : ValueMode(this, "Auto block check", false, "Shield", "Sword") {
        override fun isEnabled() = !blockMode.isSelected(0)
    }
    private val blockOutOfReach = object : ValueBoolean(this, "Block out of reach", true) {
        override fun isEnabled() = !blockMode.isSelected(0)
    }
    private val guaranteeHit = ValueBoolean(this, "Guarantee hit", false)
    private val rotations = ValueMode(this, "Rotations", true, "Around walls", "Randomized")
    private val precision = object : ValueNumber(this, "Precision", 0.0, 0.01, 1.0, 0.01) {
        override fun isEnabled() = rotations.anySelected()
    }
    private val lockView = ValueBoolean(this, "Lock view", false)
    private val flex = ValueBoolean(this, "Flex", false)
    private val flexTurn = object : ValueNumber(this, "Flex turn", 0.0, 90.0, 180.0, 1.0) {
        override fun isEnabled() = flex.value
    }
    private val flexHurtTime = object : ValueNumber(this, "Flex hurt time", 0.0, 0.5, 1.0, 0.1) {
        override fun isEnabled() = flex.value
    }
    private val syncPosition = ValueBoolean(this, "Sync position", false)
    private val waitForCritical = ValueBoolean(this, "Wait for critical", false)
    private val dontWaitWhenEnemyHasShield = object : ValueBoolean(this, "Don't wait when enemy has shield", true) {
        override fun isEnabled() = waitForCritical.value
    }
    private val criticalSprint = object : ValueBoolean(this, "Critical sprint", false) {
        override fun isEnabled() = waitForCritical.value
    }
    private val forceCritical = object : ValueBoolean(this, "Force critical", true) {
        override fun isEnabled() = waitForCritical.value && criticalSprint.value
    }

    private val targets = ArrayList<Pair<Entity, Vec3d>>()
    private val comparator: Comparator<Pair<Entity, Vec3d>> = Comparator.comparing {
        when {
            priority.isSelected(0) -> {
                mc.player?.eyePos?.squaredDistanceTo(MathUtil.closestPointToBox(mc.player?.eyePos!!, it.first.boundingBox.expand(it.first.targetingMargin.toDouble())))!!
            }
            priority.isSelected(1) -> {
                if (it.first is LivingEntity)
                    (it.first as LivingEntity).health
                else
                    0
            }
            priority.isSelected(2) -> {
                if (it.first is LivingEntity)
                    (it.first as LivingEntity).hurtTime
                else
                    0
            }
            priority.isSelected(3) -> {
                RotationUtil.getRotations(mc.player?.eyePos!!, getBestAimPoint(it.first.boundingBox)).fov(RotationUtil.fakeRotation ?: Rotation(mc.player!!))
            }
            else -> 0.0
        }.toDouble()
    }

    private var blocking = false
    private var waitForHit = false
    private var clicked = false
    private var performedTick = false

    override fun onEnable() {
        clickSpeedUtil.reset()
    }

    override fun onDisable() {
        clicked = false
        blocking = false
        targets.clear()
    }

    private fun hasShield(entity: Entity): Boolean {
        if (entity is PlayerEntity) {
            return entity.inventory.mainHandStack.item == Items.SHIELD || entity.inventory.offHand[0].item == Items.SHIELD
        }
        return false
    }

    private fun allAttacked(block: (LivingEntity) -> Boolean): Boolean {
        return (mode.isSelected(0) && targets.first().first.let { it is LivingEntity && block(it) }) || (mode.isSelected(1) && targets.all { it.first is LivingEntity && block((it.first as LivingEntity)) })
    }

    val eventConsumer = Consumer<Event> { event ->
        when (event) {
            is EventPollEvents -> {
                targets.clear()
                val currentRot = if (RotationUtil.fakeRotation != null) Rotation(RotationUtil.fakeRotation!!) else Rotation(mc.player!!)
                for (entity in mc.world?.entities!!) {
                    if (!PlayerUtil.isAttackable(entity))
                        continue
                    val entity = entity as LivingEntity

                    var boundingBox = entity.boundingBox.expand(entity.targetingMargin.toDouble())
                    if (syncPosition.value) {
                        val accessor = entity as ILivingEntity
                        boundingBox = boundingBox.offset(accessor.tarasande_getServerX() - entity.x, accessor.tarasande_getServerY() - entity.y, accessor.tarasande_getServerZ() - entity.z)
                    }
                    val bestAimPoint = getBestAimPoint(boundingBox)
                    if (bestAimPoint.squaredDistanceTo(mc.player?.eyePos!!) > reach.maxValue * reach.maxValue)
                        continue
                    if (RotationUtil.getRotations(mc.player?.eyePos!!, bestAimPoint).fov(currentRot) > fov.value)
                        continue
                    val aimPoint = if (boundingBox.contains(mc.player?.eyePos) && mc.player?.input?.movementInput?.lengthSquared() == 1.0f) {
                        mc.player?.eyePos?.add(currentRot.forwardVector(0.01))!!
                    } else {
                        // aim point calculation maybe slower, only run it if the range check is actually able to succeed under best conditions
                        getAimPoint(boundingBox, entity) ?: continue
                    }
                    // in case the eyepos is inside the boundingbox the next 2 checks will always succeed, but keeping them might prevent some retarded situation which is going to be added with an update
                    if (aimPoint.squaredDistanceTo(mc.player?.eyePos!!) > reach.maxValue * reach.maxValue)
                        continue
                    if (!throughWalls.value && !PlayerUtil.canVectorBeSeen(mc.player?.eyePos!!, aimPoint))
                        continue

                    targets.add(Pair(entity, aimPoint))
                }
                if (targets.isEmpty()) {
                    if (blocking)
                        blocking = false
                    return@Consumer
                }

                targets.sortWith(comparator)

                targets.sortBy { it.first is LivingEntity && (it.first as LivingEntity).isDead }
                targets.sortBy { it.second.squaredDistanceTo(mc.player?.eyePos!!) > reach.minValue * reach.minValue }

                val target = targets[0]

                val targetRot = RotationUtil.getRotations(mc.player?.eyePos!!, target.second)
                val smoothedRot = currentRot.smoothedTurn(
                    targetRot,
                    if (aimSpeed.minValue == 1.0 && aimSpeed.maxValue == 1.0)
                        1.0
                    else
                        MathHelper.clamp(
                            (
                                    if (aimSpeed.minValue == aimSpeed.maxValue)
                                        aimSpeed.minValue
                                    else
                                        ThreadLocalRandom.current().nextDouble(aimSpeed.minValue, aimSpeed.maxValue)
                                    ) * RenderUtil.deltaTime * 0.05,
                            0.0,
                            1.0
                        )
                )
                var finalRot = smoothedRot

                var canFlex = true
                for (entity in targets) {
                    if (entity.first !is LivingEntity) {
                        canFlex = false
                        break
                    }
                    val livingEntity = entity.first as LivingEntity
                    if (livingEntity.maxHurtTime == 0 || livingEntity.hurtTime < livingEntity.maxHurtTime * flexHurtTime.value) {
                        canFlex = false
                        break
                    }
                }
                if (!flex.value || !canFlex) {
                    val hitResult = PlayerUtil.getTargetedEntity(reach.minValue, smoothedRot)
                    if (guaranteeHit.value && target.second.squaredDistanceTo(mc.player?.eyePos!!) <= reach.minValue * reach.minValue && (hitResult == null || hitResult !is EntityHitResult || hitResult.entity == null))
                        finalRot = targetRot
                } else {
                    finalRot.yaw += ThreadLocalRandom.current().nextDouble(-flexTurn.value, flexTurn.value).toFloat()
                    finalRot.pitch = ThreadLocalRandom.current().nextDouble(-flexTurn.value / 2, flexTurn.value / 2).toFloat()
                }

                event.rotation = finalRot.correctSensitivity()

                if (lockView.value) {
                    mc.player?.yaw = finalRot.yaw
                    mc.player?.pitch = finalRot.pitch
                }

                event.minRotateToOriginSpeed = aimSpeed.minValue
                event.maxRotateToOriginSpeed = aimSpeed.maxValue
            }
            is EventTick -> {
                if (event.state == EventTick.State.PRE) {
                    if (performedTick)
                        clickSpeedUtil.reset()
                    performedTick = true
                }
            }
            is EventAttack -> {
                performedTick = false
                clicked = false

                if (targets.isEmpty() || event.dirty) {
                    clickSpeedUtil.reset()
                    waitForHit = false
                    return@Consumer
                }

                var clicks = clickSpeedUtil.getClicks()

                if (waitForCritical.value)
                    if (!dontWaitWhenEnemyHasShield.value || allAttacked { !hasShield(it) })
                        if (!mc.player?.isClimbing!! && !mc.player?.isTouchingWater!! && !mc.player?.hasStatusEffect(StatusEffects.BLINDNESS)!! && !mc.player?.hasVehicle()!!)
                            if (!mc.player?.isOnGround!! && (mc.player?.fallDistance == 0.0f || (criticalSprint.value && !mc.player?.isSprinting!!)))
                                clicks = 0

                if (dontAttackWhenBlocking.value && allAttacked { it.isBlocking })
                    if (!simulateShieldBlock.value || allAttacked { it.blockedByShield(DamageSource.player(mc.player)) })
                        clicks = 0

                if (mc.player?.isUsingItem!! && clicks > 0 && !blockMode.isSelected(0)) {
                    var hasTarget = false
                    for (entry in targets) {
                        if (entry.second.squaredDistanceTo(mc.player?.eyePos!!) <= reach.minValue * reach.minValue) {
                            hasTarget = true
                            break
                        }
                    }
                    if (hasTarget) {
                        blocking = false
                        waitForHit = true
                        when {
                            blockMode.isSelected(1) && needUnblock.value -> {
                                mc.interactionManager?.stopUsingItem(mc.player)
                            }
                            blockMode.isSelected(2) -> {
                                return@Consumer
                            }
                        }
                    }
                }
                if (!mc.player?.isUsingItem!! || (blockMode.isSelected(1) && !needUnblock.value)) {
                    for (click in 1..clicks) {
                        var attacked = false
                        for (entry in targets) {
                            var target = entry.first
                            val aimPoint = entry.second

                            if (dontAttackWhenBlocking.value && target is LivingEntity && target.isBlocking)
                                if (!simulateShieldBlock.value || target.blockedByShield(DamageSource.player(mc.player)))
                                    continue

                            if (rayTrace.value) {
                                if (RotationUtil.fakeRotation == null) {
                                    continue
                                } else {
                                    val hitResult = PlayerUtil.getTargetedEntity(reach.minValue, if (!mode.isSelected(1)) (if (simulateMouseDelay.value) Rotation((mc.player as IClientPlayerEntity).tarasande_getLastYaw(), (mc.player as IClientPlayerEntity).tarasande_getLastPitch()) else RotationUtil.fakeRotation!!) else RotationUtil.getRotations(mc.player?.eyePos!!, aimPoint))
                                    if (hitResult == null || hitResult !is EntityHitResult || hitResult.entity == null) {
                                        continue
                                    } else {
                                        target = hitResult.entity
                                    }
                                }
                            } else if (aimPoint.squaredDistanceTo(mc.player?.eyePos!!) > reach.minValue * reach.minValue) {
                                continue
                            }
                            attack(target)
                            event.dirty = true
                            waitForHit = false
                            attacked = true

                            if (!mode.isSelected(1))
                                break
                        }
                        if (!attacked)
                            if (swingInAir.value) {
                                attack(null)
                                event.dirty = true
                            }
                    }
                }
                if (targets.isNotEmpty() && (mode.isSelected(1) || targets[0].first !is PassiveEntity) && (!waitForHit) && !mc.player?.isUsingItem!! && !blockMode.isSelected(0)) {
                    var canBlock = true
                    if (blockCheckMode.isSelected(0)) {
                        val stack = mc.player?.getStackInHand(Hand.OFF_HAND)
                        if (stack?.item !is ShieldItem || mc.player?.itemCooldownManager?.isCoolingDown(stack.item)!! || mc.player?.getStackInHand(Hand.MAIN_HAND)?.useAction != UseAction.NONE)
                            canBlock = false
                    }
                    if (blockCheckMode.isSelected(1) && (mc.player?.getStackInHand(Hand.MAIN_HAND)?.item !is SwordItem || mc.player?.getStackInHand(Hand.OFF_HAND)?.useAction != UseAction.NONE))
                        canBlock = false

                    if (canBlock) {
                        var hasTarget = false
                        for (entry in targets) {
                            if (blockOutOfReach.value || entry.second.squaredDistanceTo(mc.player?.eyePos!!) <= reach.minValue * reach.minValue) {
                                hasTarget = true
                                break
                            }
                        }
                        if (hasTarget) {
                            blocking = true
                            (mc.options.useKey as IKeyBinding).tarasande_setTimesPressed(1)
                        }
                    }
                }
            }
            is EventKeyBindingIsPressed -> {
                if (event.keyBinding == mc.options.useKey) {
                    if (blocking && targets.isNotEmpty()) {
                        event.pressed = true
                    }
                }
                if (PlayerUtil.movementKeys.contains(event.keyBinding) && targets.isNotEmpty()) {
                    if (waitForCritical.value && criticalSprint.value && forceCritical.value)
                        if (!dontWaitWhenEnemyHasShield.value || ((mode.isSelected(0) && !hasShield(targets.first().first) || (mode.isSelected(1) && targets.none { hasShield(it.first) }))))
                            if (!mc.player?.isClimbing!! && !mc.player?.isTouchingWater!! && !mc.player?.hasStatusEffect(StatusEffects.BLINDNESS)!! && !mc.player?.hasVehicle()!!)
                                if (!mc.player?.isOnGround!! && mc.player?.fallDistance!! >= 0.0f)
                                    if (mc.player?.isSprinting!!)
                                        event.pressed = false
                }
            }
            is EventHandleBlockBreaking -> {
                event.parameter = event.parameter || clicked
            }
        }
    }

    private fun attack(entity: Entity?) {
        clicked = true
        val original = mc.crosshairTarget
        if (entity != null) {
            mc.crosshairTarget = EntityHitResult(entity)
            if (!attackCooldown.value) {
                (mc as IMinecraftClient).tarasande_setAttackCooldown(0)
            }
        } else {
            mc.crosshairTarget = object : HitResult(null) {
                override fun getType() = Type.MISS
            }
        }
        (mc as IMinecraftClient).tarasande_invokeDoAttack()
        mc.crosshairTarget = original
    }

    private fun getBestAimPoint(box: Box): Vec3d {
        return MathUtil.closestPointToBox(mc.player?.eyePos!!, box)
    }

    private fun getAimPoint(box: Box, entity: LivingEntity): Vec3d? {
        var best: Vec3d? = getBestAimPoint(box)

        if (rotations.isSelected(0)) {
            if (!PlayerUtil.canVectorBeSeen(mc.player?.eyePos!!, best!!)) {
                best = null
                var distanceToVec = Double.POSITIVE_INFINITY
                var x = 0.0
                while (x <= 1.0) {
                    var y = 0.0
                    while (y <= 1.0) {
                        var z = 0.0
                        while (z <= 1.0) {
                            val vector = Vec3d(
                                box.minX + (box.maxX - box.minX) * x,
                                box.minY + (box.maxY - box.minY) * y,
                                box.minZ + (box.maxZ - box.minZ) * z
                            )
                            if (PlayerUtil.canVectorBeSeen(mc.player?.eyePos!!, vector)) {
                                val distSquared = mc.player?.eyePos?.squaredDistanceTo(vector)!!
                                if (distSquared < distanceToVec) {
                                    best = vector
                                    distanceToVec = distSquared
                                }
                            }

                            z += precision.value
                        }
                        y += precision.value
                    }
                    x += precision.value
                }
                if (best == null || distanceToVec > reach.maxValue * reach.maxValue)
                    return null
            }
        }

        if (rotations.isSelected(1)) {
            var aimPoint = best?.add(0.0, 0.0, 0.0)!! /* copy */

            // Humans always try to get to the middle
            val center = box.center
            val dist = MathUtil.getBias(mc.player?.eyePos?.squaredDistanceTo(aimPoint)!! / (reach.maxValue * reach.maxValue), 0.45) // I have no idea why this works and looks like it does, but it's good and why remove it then
            aimPoint = aimPoint.add(
                (center.x - aimPoint.x) * min((1 - dist), 1.0),
                (center.y - aimPoint.y) * (1 - dist) * 0.5 /* Humans dislike aiming up and down */,
                (center.z - aimPoint.z) * min((1 - dist), 1.0)
            )

            // Humans can't hold their hands still
            val actualVelocity = Vec3d(mc.player?.prevX!! - mc.player?.x!!, mc.player?.prevY!! - mc.player?.y!!, mc.player?.prevZ!! - mc.player?.z!!)
            val diff = actualVelocity.subtract(entity.prevX - entity.x, entity.prevY - entity.y, entity.prevZ - entity.z)?.multiply(-1.0)!!
//            if (diff.lengthSquared() > 0.0) { // either the target or the player has to move otherwise changing the rotation doesn't make sense
//                aimPoint = aimPoint.add(
//                    if (diff.x != 0.0) sin(System.currentTimeMillis() * 0.001) * 0.15 else 0.0,
//                    if (diff.y != 0.0) cos(System.currentTimeMillis() * 0.001) * 0.15 else 0.0,
//                    if (diff.z != 0.0) sin(System.currentTimeMillis() * 0.001) * 0.15 else 0.0
//                )
//            }

            // Human aim is slow
            aimPoint = aimPoint.subtract(diff.multiply(0.5))

            // Don't aim through walls
            while (!PlayerUtil.canVectorBeSeen(mc.player?.eyePos!!, aimPoint) && rotations.isSelected(0)) {
                aimPoint = Vec3d(
                    MathUtil.bringCloser(aimPoint.x, best.x, precision.value),
                    MathUtil.bringCloser(aimPoint.y, best.y, precision.value),
                    MathUtil.bringCloser(aimPoint.z, best.z, precision.value)
                )
            }

            var distToBest = mc.player?.eyePos?.squaredDistanceTo(best)!!
            if (distToBest <= reach.minValue * reach.minValue) {
                while (mc.player?.eyePos?.squaredDistanceTo(aimPoint)!! > reach.minValue * reach.minValue)
                    aimPoint = Vec3d(
                        MathUtil.bringCloser(aimPoint.x, best.x, precision.value),
                        MathUtil.bringCloser(aimPoint.y, best.y, precision.value),
                        MathUtil.bringCloser(aimPoint.z, best.z, precision.value)
                    )
                val hitResult = PlayerUtil.getTargetedEntity(reach.minValue, RotationUtil.getRotations(mc.player?.eyePos!!, aimPoint))
                if (hitResult == null || hitResult !is EntityHitResult || hitResult.entity == null) {
                    aimPoint = best
                }
            } else {
                distToBest = (sqrt(distToBest) - reach.minValue) / (reach.maxValue - reach.minValue)
                aimPoint = aimPoint.add(
                    (best.x - aimPoint.x) * (1 - distToBest),
                    (best.y - aimPoint.y) * (1 - distToBest),
                    (best.z - aimPoint.z) * (1 - distToBest),
                )
            }

            best = aimPoint
        }

        return best
    }

}