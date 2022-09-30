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
import net.minecraft.util.math.Vec3d
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.*
import su.mandora.tarasande.mixin.accessor.IClientPlayerEntity
import su.mandora.tarasande.mixin.accessor.IKeyBinding
import su.mandora.tarasande.mixin.accessor.IMinecraftClient
import su.mandora.tarasande.util.extension.minus
import su.mandora.tarasande.util.extension.plus
import su.mandora.tarasande.util.extension.times
import su.mandora.tarasande.util.math.MathUtil
import su.mandora.tarasande.util.math.rotation.Rotation
import su.mandora.tarasande.util.math.rotation.RotationUtil
import su.mandora.tarasande.util.player.PlayerUtil
import su.mandora.tarasande.util.player.clickspeed.ClickSpeedUtil
import su.mandora.tarasande.value.ValueBoolean
import su.mandora.tarasande.value.ValueMode
import su.mandora.tarasande.value.ValueNumber
import su.mandora.tarasande.value.ValueNumberRange
import java.util.concurrent.ThreadLocalRandom
import java.util.function.Consumer
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class ModuleKillAura : Module("Kill aura", "Automatically attacks near players", ModuleCategory.COMBAT) {

    private val mode = ValueMode(this, "Mode", false, "Single", "Multi")
    private val priority = ValueMode(this, "Priority", false, "Distance", "Health", "Hurt time", "FOV")
    private val fov = ValueNumber(this, "FOV", 0.0, 255.0, 255.0, 1.0)
    private val fakeRotationFov = ValueBoolean(this, "Fake rotation FOV", false)
    private val reach = ValueNumberRange(this, "Reach", 0.1, 3.0, 4.0, 6.0, 0.1)
    private val clickSpeedUtil = ClickSpeedUtil(this, { true }) // for setting order
    private val rayTrace = ValueBoolean(this, "Ray trace", false)
    private val simulateMouseDelay = object : ValueBoolean(this, "Simulate mouse delay", false) {
        override fun isEnabled() = rayTrace.value && !mode.isSelected(1)
    }
    private val swingInAir = ValueBoolean(this, "Swing in air", true)
    private val aimSpeed = ValueNumberRange(this, "Aim speed", 0.1, 1.0, 1.0, 1.0, 0.1)
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
    private val flexHurtTime = object : ValueNumber(this, "Flex hurt time", 0.1, 0.5, 0.9, 0.1) {
        override fun isEnabled() = flex.value
    }
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

    val targets = ArrayList<Pair<Entity, Vec3d>>()
    private val comparator: Comparator<Pair<Entity, Vec3d>> = Comparator.comparing {
        when {
            priority.isSelected(0) -> {
                mc.player?.eyePos?.squaredDistanceTo(MathUtil.getBestAimPoint(it.first.boundingBox.expand(it.first.targetingMargin.toDouble())))!!
            }

            priority.isSelected(1) -> {
                if (it.first is LivingEntity) (it.first as LivingEntity).health
                else 0
            }

            priority.isSelected(2) -> {
                if (it.first is LivingEntity) (it.first as LivingEntity).hurtTime
                else 0
            }

            priority.isSelected(3) -> {
                RotationUtil.getRotations(mc.player?.eyePos!!, MathUtil.getBestAimPoint(it.first.boundingBox)).fov(fovRotation())
            }

            else -> 0.0
        }.toDouble()
    }

    private var blocking = false
    private var waitForHit = false
    private var clicked = false
    private var performedTick = false
    private var lastFlex: Rotation? = null

    override fun onEnable() {
        clickSpeedUtil.reset()
    }

    override fun onDisable() {
        clicked = false
        blocking = false
        lastFlex = null
        targets.clear()
    }

    private fun fovRotation() = if (fakeRotationFov.value && RotationUtil.fakeRotation != null) RotationUtil.fakeRotation!! else Rotation(mc.player!!)

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
                    if (!PlayerUtil.isAttackable(entity)) continue
                    val entity = entity as LivingEntity

                    val boundingBox = entity.boundingBox.expand(entity.targetingMargin.toDouble())
                    val bestAimPoint = MathUtil.getBestAimPoint(boundingBox)
                    if (bestAimPoint.squaredDistanceTo(mc.player?.eyePos!!) > reach.maxValue * reach.maxValue) continue
                    if (RotationUtil.getRotations(mc.player?.eyePos!!, bestAimPoint).fov(fovRotation()) > fov.value) continue
                    val aimPoint = if (boundingBox.contains(mc.player?.eyePos) && mc.player?.input?.movementInput?.lengthSquared() != 0.0f) {
                        mc.player?.eyePos!! + currentRot.forwardVector(0.01)
                    } else {
                        // aim point calculation maybe slower, only run it if the range check is actually able to succeed under best conditions
                        getAimPoint(boundingBox, entity)
                    }
                    // in case the eyepos is inside the boundingbox the next 2 checks will always succeed, but keeping them might prevent some retarded situation which is going to be added with an update
                    if (aimPoint.squaredDistanceTo(mc.player?.eyePos!!) > reach.maxValue * reach.maxValue) continue
                    if (!throughWalls.value && !PlayerUtil.canVectorBeSeen(mc.player?.eyePos!!, aimPoint)) continue

                    targets.add(Pair(entity, aimPoint))
                }
                if (targets.isEmpty()) {
                    if (blocking)
                        blocking = false
                    lastFlex = null
                    return@Consumer
                }

                targets.sortWith(comparator)

                targets.sortBy { it.first is LivingEntity && (it.first as LivingEntity).isDead }
                targets.sortBy { it.second.squaredDistanceTo(mc.player?.eyePos!!) > reach.minValue * reach.minValue }

                val target = targets[0]

                val targetRot = RotationUtil.getRotations(mc.player?.eyePos!!, target.second)
                val smoothedRot = currentRot.smoothedTurn(targetRot, aimSpeed)
                var finalRot = smoothedRot

                val lowestHurttime = targets.filter { it.first is LivingEntity && (it.first as LivingEntity).maxHurtTime > 0 }.minOfOrNull { val livingEntity = it.first as LivingEntity; livingEntity.hurtTime / livingEntity.maxHurtTime.toFloat() }

                if (!flex.value || lowestHurttime == null || lowestHurttime < flexHurtTime.value) {
                    val hitResult = PlayerUtil.getTargetedEntity(reach.minValue, smoothedRot)
                    if (guaranteeHit.value && target.second.squaredDistanceTo(mc.player?.eyePos!!) <= reach.minValue * reach.minValue && (hitResult == null || hitResult !is EntityHitResult || hitResult.entity == null)) {
                        finalRot = targetRot
                    }
                } else {
                    val delta = (lowestHurttime - flexHurtTime.value) / (1.0 - flexHurtTime.value)
                    if (lastFlex == null) {
                        lastFlex = Rotation(ThreadLocalRandom.current().nextDouble(-flexTurn.value, flexTurn.value).toFloat(), ThreadLocalRandom.current().nextDouble(-flexTurn.value / 2, flexTurn.value / 2).toFloat())
                    }
                    finalRot = finalRot.smoothedTurn(Rotation(finalRot.yaw + lastFlex?.yaw!!, lastFlex?.pitch!!), delta)
                }

                event.rotation = finalRot.correctSensitivity()

                if (lockView.value) {
                    mc.player?.yaw = event.rotation.yaw
                    mc.player?.pitch = event.rotation.pitch
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

                if (waitForCritical.value) if (!dontWaitWhenEnemyHasShield.value || allAttacked { !hasShield(it) })
                    if (!mc.player?.isClimbing!! && !mc.player?.isTouchingWater!! && !mc.player?.hasStatusEffect(StatusEffects.BLINDNESS)!! && !mc.player?.hasVehicle()!!)
                        if (!mc.player?.isOnGround!! && mc.player?.velocity?.y!! != 0.0 && (mc.player?.fallDistance == 0.0f || (criticalSprint.value && !mc.player?.isSprinting!!)))
                            clicks = 0

                if (dontAttackWhenBlocking.value && allAttacked { it.isBlocking })
                    if (!simulateShieldBlock.value || allAttacked { it.blockedByShield(DamageSource.player(mc.player)) })
                        clicks = 0

                if (!blockMode.isSelected(0) && mc.player?.isUsingItem!! && clicks > 0) {
                    var hasTarget = false
                    for (entry in targets) {
                        if (entry.second.squaredDistanceTo(mc.player?.eyePos!!) <= reach.minValue * reach.minValue) {
                            hasTarget = true
                            break
                        }
                    }
                    if (hasTarget) {
                        if (!blockMode.isSelected(1) || needUnblock.value)
                            blocking = false
                        waitForHit = true
                        when {
                            blockMode.isSelected(1) && needUnblock.value -> {
                                mc.interactionManager?.stopUsingItem(mc.player)
                            }

                            blockMode.isSelected(2) -> {
                                clickSpeedUtil.reset() // we can't count this as a attack, can we?
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
                                    val hitResult = PlayerUtil.getTargetedEntity(
                                        reach.minValue,
                                        if (!mode.isSelected(1))
                                            if (simulateMouseDelay.value)
                                                Rotation((mc.player as IClientPlayerEntity).tarasande_getLastYaw(), (mc.player as IClientPlayerEntity).tarasande_getLastPitch())
                                            else
                                                RotationUtil.fakeRotation!!
                                        else
                                            RotationUtil.getRotations(mc.player?.eyePos!!, aimPoint),
                                        throughWalls.value)
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
                            lastFlex = null
                            event.dirty = true
                            waitForHit = false
                            attacked = true

                            if (!mode.isSelected(1)) break
                        }
                        if (!attacked)
                            if (swingInAir.value) {
                                attack(null)
                                event.dirty = true
                            }
                    }
                }
                if (targets.isNotEmpty() && targets.any { it.first !is PassiveEntity } && !waitForHit && !mc.player?.isUsingItem!! && !blockMode.isSelected(0)) {
                    var canBlock = true
                    if (blockCheckMode.isSelected(0)) {
                        val stack = mc.player?.getStackInHand(Hand.OFF_HAND)
                        if (stack?.item !is ShieldItem || mc.player?.itemCooldownManager?.isCoolingDown(stack.item)!! || mc.player?.getStackInHand(Hand.MAIN_HAND)?.useAction != UseAction.NONE) canBlock = false
                    }
                    if (blockCheckMode.isSelected(1) && (mc.player?.getStackInHand(Hand.MAIN_HAND)?.item !is SwordItem || mc.player?.getStackInHand(Hand.OFF_HAND)?.useAction.let { it != UseAction.NONE && it != UseAction.BLOCK }))
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
                    if (targets.isNotEmpty()) {
                        event.pressed = blocking
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
        clicked = clicked or (mc as IMinecraftClient).tarasande_invokeDoAttack()
        mc.crosshairTarget = original
    }

    private fun getAimPoint(box: Box, entity: LivingEntity): Vec3d {
        var best = MathUtil.getBestAimPoint(box)
        var visible = PlayerUtil.canVectorBeSeen(mc.player?.eyePos!!, best)

        if (rotations.isSelected(0)) {
            if (!visible) {
                var newBest: Vec3d? = null
                var distanceToVec = Double.POSITIVE_INFINITY
                var x = 0.0
                while (x <= 1.0) {
                    var y = 0.0
                    while (y <= 1.0) {
                        var z = 0.0
                        while (z <= 1.0) {
                            val vector = Vec3d(box.minX + (box.maxX - box.minX) * x, box.minY + (box.maxY - box.minY) * y, box.minZ + (box.maxZ - box.minZ) * z)
                            if (PlayerUtil.canVectorBeSeen(mc.player?.eyePos!!, vector)) {
                                val distSquared = mc.player?.eyePos?.squaredDistanceTo(vector)!!
                                if (distSquared < distanceToVec) {
                                    newBest = vector
                                    distanceToVec = distSquared
                                }
                            }
                            z += precision.value
                        }
                        y += precision.value
                    }
                    x += precision.value
                }
                if (newBest != null) {
                    best = newBest
                    visible = true
                }
            }
        }

        if (rotations.isSelected(1)) {
            var aimPoint = best.add(0.0, 0.0, 0.0)!! /* copy */

            // Humans always try to get to the middle
            val center = box.center
            val dist = 1.0 - MathUtil.getBias(mc.player?.eyePos?.distanceTo(aimPoint)!! / reach.maxValue, 0.65) // I have no idea why this works and looks like it does, but it's good, so why remove it then
            aimPoint = aimPoint.add((center.x - aimPoint.x) * dist, (center.y - aimPoint.y) * (1.0 - dist) * 0.4 /* Humans dislike aiming up and down */, (center.z - aimPoint.z) * dist)

            // Humans can't hold their hands still
            val actualVelocity = Vec3d(mc.player?.prevX!! - mc.player?.x!!, mc.player?.prevY!! - mc.player?.y!!, mc.player?.prevZ!! - mc.player?.z!!)
            val diff = actualVelocity.subtract(entity.prevX - entity.x, entity.prevY - entity.y, entity.prevZ - entity.z)!! * -1.0
            if (diff.lengthSquared() > 0.0) { // either the target or the player has to move otherwise changing the rotation doesn't make sense
                val horChange = diff.horizontalLength()
                aimPoint = aimPoint.add(
                    sin(System.currentTimeMillis() * 0.005) * horChange,
                    cos(System.currentTimeMillis() * 0.005) * (diff.y + horChange * 0.2),
                    sin(System.currentTimeMillis() * 0.005) * horChange
                )
            }

            // Human aim is slow
            aimPoint -= diff * 0.5

            // Don't aim through walls
            while (visible && !PlayerUtil.canVectorBeSeen(mc.player?.eyePos!!, aimPoint) && rotations.isSelected(0)) {
                aimPoint = Vec3d(MathUtil.bringCloser(aimPoint.x, best.x, precision.value), MathUtil.bringCloser(aimPoint.y, best.y, precision.value), MathUtil.bringCloser(aimPoint.z, best.z, precision.value))
            }

            var distToBest = mc.player?.eyePos?.squaredDistanceTo(best)!!
            if (distToBest <= reach.minValue * reach.minValue && visible) {
                while (mc.player?.eyePos?.squaredDistanceTo(aimPoint)!! > reach.minValue * reach.minValue)
                    aimPoint = Vec3d(MathUtil.bringCloser(aimPoint.x, best.x, precision.value), MathUtil.bringCloser(aimPoint.y, best.y, precision.value), MathUtil.bringCloser(aimPoint.z, best.z, precision.value))
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

            best = MathUtil.closestPointToBox(aimPoint, box)
        }

        return best
    }

}