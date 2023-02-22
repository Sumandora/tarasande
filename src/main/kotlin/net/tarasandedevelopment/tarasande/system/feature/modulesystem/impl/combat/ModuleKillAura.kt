package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.combat

import net.minecraft.entity.Entity
import net.minecraft.entity.EntityStatuses
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.passive.PassiveEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.AxeItem
import net.minecraft.item.Items
import net.minecraft.item.ShieldItem
import net.minecraft.item.SwordItem
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket
import net.minecraft.util.UseAction
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import net.tarasandedevelopment.tarasande.event.impl.*
import net.tarasandedevelopment.tarasande.feature.rotation.Rotations
import net.tarasandedevelopment.tarasande.injection.accessor.ILivingEntity
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.*
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.meta.abstracted.ValueButtonOwnerValues
import net.tarasandedevelopment.tarasande.system.feature.clickmethodsystem.api.ClickSpeedUtil
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ManagerModule
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.movement.ModuleClickTP
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.player.ModuleAutoTool
import net.tarasandedevelopment.tarasande.util.extension.minecraft.*
import net.tarasandedevelopment.tarasande.util.math.MathUtil
import net.tarasandedevelopment.tarasande.util.math.rotation.Rotation
import net.tarasandedevelopment.tarasande.util.math.rotation.RotationUtil
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil
import net.tarasandedevelopment.tarasande.util.player.container.ContainerUtil
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class ModuleKillAura : Module("Kill aura", "Automatically attacks near players", ModuleCategory.COMBAT) {

    private val mode = ValueMode(this, "Mode", false, "Single", "Multi")
    private val priority = ValueMode(this, "Priority", false, "Distance", "Health", "Hurt time", "FOV", "Attack cooldown")
    private val fov = ValueNumber(this, "FOV", 0.0, Rotation.MAXIMUM_DELTA, Rotation.MAXIMUM_DELTA, 1.0)
    private val fakeRotationFov = ValueBoolean(this, "Fake rotation FOV", false)
    private val reach = ValueNumberRange(this, "Reach", 0.1, 3.0, 4.0, 6.0, 0.1)
    private val clickSpeedUtil = ClickSpeedUtil(this, { true }) // for setting order
    private val waitForDamageValue = ValueBoolean(this, "Wait for damage", false)
    private val rayTrace = ValueBoolean(this, "Ray trace", false)
    private val simulateMouseDelay = ValueBoolean(this, "Simulate mouse delay", false, isEnabled = { rayTrace.value && !mode.isSelected(1) })
    private val swingInAir = ValueBoolean(this, "Swing in air", true)
    private val aimSpeed = ValueNumberRange(this, "Aim speed", 0.1, 1.0, 1.0, 1.0, 0.1)
    private val dontAttackWhenBlocking = ValueBoolean(this, "Don't attack when blocking", false)
    private val simulateShieldBlock = ValueBoolean(this, "Simulate shield block", false, isEnabled = { dontAttackWhenBlocking.value })
    private val throughWalls = ValueMode(this, "Through walls", false, "Off", "Continue aiming", "Hit and aim through walls")
    private val autoBlock = object : ValueMode(this, "Auto block", false, "Disabled", "Permanent", "Legit") {
        override fun onChange(index: Int, oldSelected: Boolean, newSelected: Boolean) {
            blocking = false
        }
    }
    private val expectSwordBlocking = ValueBoolean(this, "Expect sword blocking", false, isEnabled = { !autoBlock.isSelected(0) })
    private val needUnblock = ValueBoolean(this, "Need unblock", true, isEnabled = { autoBlock.isSelected(1) })
    private val blockOutOfReach = ValueBoolean(this, "Block out of reach", true, isEnabled = { !autoBlock.isSelected(0) })
    private val preventBlockCooldown = ValueBoolean(this, "Prevent block cooldown", false, isEnabled = { !autoBlock.isSelected(0) })
    private val counterBlocking = ValueMode(this, "Counter blocking", false, "Off", "Wait for block", "Immediately")
    private val guaranteeHit = ValueBoolean(this, "Guarantee hit", false)
    private val rotations = ValueMode(this, "Rotations", true, "Around walls", "Randomized")
    private val precision = ValueNumber(this, "Precision", 0.0, 0.01, 1.0, 0.01, isEnabled = { rotations.anySelected() })
    private val flex = ValueBoolean(this, "Flex", false)
    private val flexTurn = ValueNumber(this, "Flex turn", 1.0, 90.0, 180.0, 1.0, isEnabled = { flex.value })
    private val flexHurtTime = ValueNumber(this, "Flex hurt time", 0.1, 0.5, 0.9, 0.1, isEnabled = { flex.value })
    private val waitForCritical = ValueBoolean(this, "Wait for critical", false)
    private val dontWaitWhenEnemyHasShield = ValueBoolean(this, "Don't wait when enemy has shield", true, isEnabled = { waitForCritical.value })
    private val criticalSprint = ValueBoolean(this, "Critical sprint", false, isEnabled = { waitForCritical.value })
    private val forceCritical = ValueBoolean(this, "Force critical", true, isEnabled = { criticalSprint.isEnabled() && criticalSprint.value })
    object SmartClickingBehaviour {
        val enableSmartClickingBehaviour = ValueBoolean(this, "Enable smart clicking behaviour", false)
        val enemyHurtTime = ValueNumber(this, "Enemy hurt time", 0.1, 1.0, 1.0, 0.1)
        val selfHurtTime = ValueNumber(this, "Self hurt time", 0.1, 0.6,1.0, 0.1)
    }
    init {
        ValueButtonOwnerValues(this, "Smart clicking behaviour", SmartClickingBehaviour)
    }
    private val aimTargetColor = ValueColor(this, "Aim target color", 0.0, 1.0, 1.0, 1.0)

    var targets = CopyOnWriteArrayList<Pair<Entity, Vec3d>>()
    private val comparator: Comparator<Pair<Entity, Vec3d>> = Comparator.comparing {
        when {
            priority.isSelected(0) -> {
                mc.player?.eyePos?.squaredDistanceTo(MathUtil.getBestAimPoint(it.first.boundingBox.expand(it.first.targetingMargin.toDouble())))!!
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
                RotationUtil.getRotations(mc.player?.eyePos!!, MathUtil.getBestAimPoint(it.first.boundingBox)).fov(fovRotation())
            }

            priority.isSelected(4) -> {
                entityCooldowns.getOrDefault(it.first, 0)
            }

            else -> 0.0
        }.toDouble()
    }

    private var blocking = false
    private var waitForHit = false
    private var performedTick = false
    private var lastFlex: Rotation? = null
    private var waitForDamage = true

    private var teleportPath: ArrayList<Vec3d>? = null
    private var entityCooldowns = HashMap<Entity, Int>()

    private val moduleClickTP by lazy { ManagerModule.get(ModuleClickTP::class.java) } // make it lazy, this might not be ready when the kill aura is

    override fun onEnable() {
        clickSpeedUtil.reset()
    }

    override fun onDisable() {
        blocking = false
        lastFlex = null
        targets = CopyOnWriteArrayList()
        waitForDamage = true
        teleportPath = null
    }

    private fun fovRotation() = if (fakeRotationFov.value && Rotations.fakeRotation != null) Rotations.fakeRotation!! else Rotation(mc.player!!)

    private fun hasShield(entity: Entity): Boolean {
        if (entity is PlayerEntity) {
            return entity.inventory.mainHandStack.item == Items.SHIELD || entity.inventory.offHand[0].item == Items.SHIELD
        }
        return false
    }

    private fun allAttackedLivingEntities(block: (LivingEntity) -> Boolean): Boolean {
        return (mode.isSelected(0) && targets.firstOrNull()?.first.let { it is LivingEntity && block(it) }) || (mode.isSelected(1) && targets.all { it.first is LivingEntity && block((it.first as LivingEntity)) })
    }

    init {
        registerEvent(EventRotation::class.java) { event ->
            val prevTargets = ArrayList(targets)
            targets.clear()
            val currentRot = Rotations.fakeRotation ?: Rotation(mc.player!!)
            for (entity in mc.world?.entities!!) {
                if (!PlayerUtil.isAttackable(entity)) continue

                val boundingBox = entity.boundingBox.expand(entity.targetingMargin.toDouble())
                val bestAimPoint = MathUtil.getBestAimPoint(boundingBox)
                if (bestAimPoint.squaredDistanceTo(mc.player?.eyePos!!) > reach.maxValue * reach.maxValue) continue
                if (RotationUtil.getRotations(mc.player?.eyePos!!, bestAimPoint).fov(fovRotation()) > fov.value) continue
                var aimPoint =
                    if (boundingBox.contains(mc.player?.eyePos) && mc.player?.input?.movementInput?.lengthSquared() != 0.0F) {
                        mc.player?.eyePos!! + currentRot.forwardVector() * 0.01
                    } else {
                        // aim point calculation maybe slower, only run it if the range check is actually able to succeed under best conditions
                        getAimPoint(boundingBox, entity)
                    }
                // in case the eyepos is inside the boundingbox the next 2 checks will always succeed, but keeping them might prevent some retarded situation which is going to be added with an update
                if (aimPoint.squaredDistanceTo(mc.player?.eyePos!!) > reach.maxValue * reach.maxValue) {
                    aimPoint = bestAimPoint
                }
                if (!throughWalls.isSelected(2))
                    if ((throughWalls.isSelected(0) || !prevTargets.any { it.first == entity }) && !PlayerUtil.canVectorBeSeen(mc.player?.eyePos!!, aimPoint)) continue
                targets.add(Pair(entity, aimPoint))
            }
            if (targets.isEmpty()) {
                blocking = false
                lastFlex = null
                waitForDamage = true
                teleportPath = null
                return@registerEvent
            }

            //@formatter:off
            targets.sortWith(
                Comparator.comparing { it: Pair<Entity, Vec3d> -> if (it.first is LivingEntity) (it.first as LivingEntity).isDead else true }
                    .thenBy { !shouldAttackEntity(it.first) }
                    .thenBy { mc.player?.eyePos?.squaredDistanceTo(it.second)!! > reach.minValue * reach.minValue }
                    .then(comparator)
            )
            //@formatter:on

            val target = targets[0]

            val targetRot = RotationUtil.getRotations(mc.player?.eyePos!!, target.second)
            var finalRot = targetRot

            val lowestHurtTime = target.first.let { if (it is LivingEntity && it.maxHurtTime > 0) it.smoothedHurtTime() else null }

            if (!flex.value || lowestHurtTime == null || lowestHurtTime < flexHurtTime.value) {
                finalRot = currentRot.smoothedTurn(targetRot, aimSpeed)
                val hitResult = PlayerUtil.getTargetedEntity(reach.minValue, finalRot)
                if (guaranteeHit.value && target.second.squaredDistanceTo(mc.player?.eyePos!!) <= reach.minValue * reach.minValue && !hitResult.isEntityHitResult()) {
                    finalRot = targetRot
                }
            } else {
                val delta = (lowestHurtTime - flexHurtTime.value) / (1.0 - flexHurtTime.value)
                if (lastFlex == null) {
                    lastFlex = Rotation(ThreadLocalRandom.current().nextDouble(-flexTurn.value, flexTurn.value).toFloat(), ThreadLocalRandom.current().nextDouble(-flexTurn.value / 2, flexTurn.value / 2).toFloat())
                }
                finalRot = finalRot.smoothedTurn(Rotation(finalRot.yaw + lastFlex?.yaw!!, lastFlex?.pitch!!), delta)
            }

            event.rotation = finalRot.correctSensitivity(preference = {
                PlayerUtil.getTargetedEntity(reach.minValue, it, throughWalls.isSelected(2))?.isEntityHitResult() == true
            })
        }

        registerEvent(EventTick::class.java) { event ->
            if (event.state == EventTick.State.PRE) {
                if (performedTick)
                    clickSpeedUtil.reset()
                performedTick = true
            }
        }

        registerEvent(EventAttack::class.java) { event ->
            performedTick = false

            val validEntities = ArrayList<Pair<Entity, Vec3d>>()

            targets.filter { shouldAttackEntity(it.first) }.forEach {
                var target = it.first
                val aimPoint = it.second

                val distance = aimPoint.squaredDistanceTo(mc.player?.eyePos!!)

                if (rayTrace.value) {
                    if (Rotations.fakeRotation == null) {
                        return@forEach
                    } else {
                        val hitResult = PlayerUtil.getTargetedEntity(
                            reach.minValue,
                            if (!mode.isSelected(1))
                                if (simulateMouseDelay.value)
                                    Rotation(mc.player!!.lastYaw, mc.player!!.lastPitch)
                                else
                                    Rotations.fakeRotation!!
                            else
                                RotationUtil.getRotations(mc.player?.eyePos!!, aimPoint),
                            throughWalls.isSelected(2))
                        if (hitResult == null || hitResult !is EntityHitResult || hitResult.entity == null) {
                            return@forEach
                        } else {
                            target = hitResult.entity
                        }
                    }
                } else if (distance > reach.minValue * reach.minValue) {
                    return@forEach
                }
                validEntities.add(Pair(target, aimPoint))
            }

            if ((waitForDamageValue.value && waitForDamage) ||
                allAttackedLivingEntities { !shouldAttackEntity(it) } ||
                (waitForCritical.value && mc.player?.isOnGround != true && willPerformCritical(criticalSprint = false, fallDistance = false) && !willPerformCritical(criticalSprint.value, true) && (!dontWaitWhenEnemyHasShield.value || allAttackedLivingEntities { !hasShield(it) })))
                validEntities.clear()

            var attacked = false

            if (validEntities.isNotEmpty() && !event.dirty) {
                var clicks = clickSpeedUtil.getClicks()

                if (clicks == 0)
                    if (isCancellingShields() && !allAttackedLivingEntities { !it.isBlocking } && (counterBlocking.isSelected(2)))
                        clicks = 1 // Axes can cancel out shields whenever they want, so lets force a hit

                if (!autoBlock.isSelected(0) && mc.player?.isUsingItem!! && clicks > 0) {
                    if (unblock())
                        return@registerEvent
                }

                if (!mc.player?.isUsingItem!! || (autoBlock.isSelected(1) && !needUnblock.value)) {
                    val previousPos = mc.player?.pos!!
                    teleportPath = ArrayList()
                    val maxTeleportTime = (mc.renderTickCounter.tickTime / targets.size.toDouble()).toLong()
                    for (pair in validEntities) {
                        val target = pair.first
                        val aimPoint = pair.second

                        val distance = aimPoint.squaredDistanceTo(mc.player?.eyePos!!)

                        if (distance > 6.0 * 6.0 && distance <= reach.minValue * reach.minValue) {
                            val path = moduleClickTP.teleportToPosition(BlockPos(target.pos), maxTeleportTime) ?: continue // failed
                            teleportPath!!.addAll(path)
                        }

                        attack(target, clicks, aimPoint)
                        lastFlex = null
                        event.dirty = true
                        waitForHit = false
                        attacked = true

                        if (mode.isSelected(0))
                            break
                    }
                    if (!attacked && swingInAir.value) {
                        if (PlayerUtil.getTargetedEntity(reach.minValue, Rotations.fakeRotation ?: Rotation(mc.player!!), false)?.isMissHitResult() == true) {
                            attack(null, clicks)
                            event.dirty = true
                        }
                    }
                    if (mc.player?.pos != previousPos) {
                        val backPath = moduleClickTP.teleportToPosition(BlockPos(previousPos), maxTeleportTime)
                        if (backPath != null) {
                            teleportPath!!.addAll(backPath)

                            mc.player?.setPosition(previousPos)
                            teleportPath!!.add(mc.player?.pos!!)
                        }
                    }
                }
            } else {
                clickSpeedUtil.reset()
                waitForHit = false
            }

            if (!blocking && targets.isNotEmpty() && targets.any { it.first !is PassiveEntity } && (validEntities.isEmpty() || attacked) && !mc.player?.isUsingItem!! && !autoBlock.isSelected(0)) {
                block()
            }
        }

        registerEvent(EventKeyBindingIsPressed::class.java) { event ->
            if (event.keyBinding == mc.options.useKey) {
                if (targets.isNotEmpty()) {
                    event.pressed = event.pressed || blocking && (!preventBlockCooldown.value || allAttackedLivingEntities { !it.disablesShield() })
                }
            }
            if (PlayerUtil.movementKeys.contains(event.keyBinding) && targets.isNotEmpty()) {
                if (waitForCritical.value && criticalSprint.value && forceCritical.value)
                    if (!dontWaitWhenEnemyHasShield.value || !allAttackedLivingEntities { !hasShield(it) })
                        if (willPerformCritical(criticalSprint = false, fallDistance = true))
                            if (mc.player?.isSprinting!!)
                                event.pressed = false
            }
        }

        registerEvent(EventPacket::class.java) { event ->
            if (event.type == EventPacket.Type.RECEIVE) {
                when (event.packet) {
                    is EntityStatusS2CPacket -> {
                        if (mc.world != null && event.packet.getEntity(mc.world) == mc.player && event.packet.status == EntityStatuses.DAMAGE_FROM_GENERIC_SOURCE)
                            waitForDamage = false
                    }

                    is EntityAnimationS2CPacket -> {
                        if (event.packet.animationId == EntityAnimationS2CPacket.SWING_MAIN_HAND) {
                            val entity = mc.world?.getEntityById(event.packet.id)
                            if (entity != null)
                                entityCooldowns[entity] = entity.age
                        }
                    }
                }
            }
        }

        registerEvent(EventRender3D::class.java) { event ->
            if (Rotations.fakeRotation != null)
                if (mc.crosshairTarget != null && mc.crosshairTarget?.isEntityHitResult() == true)
                    if (mc.crosshairTarget is EntityHitResult && targets.any { it.first == (mc.crosshairTarget as EntityHitResult).entity }) {
                        RenderUtil.blockOutline(event.matrices, Box.from(mc.crosshairTarget?.pos).offset(-0.5, -0.5, -0.5).expand(-0.45), aimTargetColor.getColor().rgb)
                    }
            RenderUtil.renderPath(event.matrices, teleportPath ?: return@registerEvent, -1)
        }
    }

    private fun attack(entity: Entity?, amount: Int, aimPoint: Vec3d? = null) {
        repeat(amount) {
            PlayerUtil.attack(entity, aimPoint)
        }
    }

    private fun getAimPoint(box: Box, entity: Entity): Vec3d {
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
            val dist = 1.0 - MathUtil.getBias(mc.player?.eyePos?.distanceTo(aimPoint)!! / reach.maxValue.coerceAtLeast(reach.minValue + 0.5), 0.65) // I have no idea why this works and looks like it does, but it's good, so why remove it then
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

            // Humans can't move their mouse in a straight line
            val aimDelta = (Rotations.fakeRotation ?: Rotation(mc.player!!)).fov(RotationUtil.getRotations(mc.player?.eyePos!!, aimPoint)) / Rotation.MAXIMUM_DELTA
            aimPoint = aimPoint.add(0.0, -aimDelta * box.yLength, 0.0)

            // Don't aim through walls
            while (visible && !PlayerUtil.canVectorBeSeen(mc.player?.eyePos!!, aimPoint) && rotations.isSelected(0)) {
                aimPoint = Vec3d(MathUtil.bringCloser(aimPoint.x, best.x, precision.value),
                    MathUtil.bringCloser(aimPoint.y, best.y, precision.value),
                    MathUtil.bringCloser(aimPoint.z, best.z, precision.value)
                )
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

    private fun block() {
        if (preventBlockCooldown.value && !allAttackedLivingEntities { !it.disablesShield() })
            return
        val usedStack = mc.player?.getStackInHand(PlayerUtil.getUsedHand() ?: return) ?: return
        if(usedStack.useAction != UseAction.BLOCK && !(expectSwordBlocking.value && usedStack.item is SwordItem))
            return
        if(mc.player?.itemCooldownManager?.isCoolingDown(usedStack.item)!!)
            return // rip :c

        var hasTarget = false
        for (entry in targets) {
            if (blockOutOfReach.value || entry.second.squaredDistanceTo(mc.player?.eyePos!!) <= reach.minValue * reach.minValue) {
                hasTarget = true
                break
            }
        }
        if (hasTarget) {
            blocking = true
        }
    }

    private fun unblock(): Boolean {
        var hasTarget = false
        for (entry in targets) {
            if (entry.second.squaredDistanceTo(mc.player?.eyePos!!) <= reach.minValue * reach.minValue) {
                hasTarget = true
                break
            }
        }
        if (hasTarget) {
            if (!autoBlock.isSelected(1) || needUnblock.value)
                blocking = false
            waitForHit = true
            when {
                autoBlock.isSelected(1) && needUnblock.value -> {
                    mc.interactionManager?.stopUsingItem(mc.player)
                }

                autoBlock.isSelected(2) -> {
                    clickSpeedUtil.reset() // we can't count this as an attack, can we?
                    return true
                }
            }
        }
        return false
    }

    private fun shouldAttackEntity(entity: Entity): Boolean {
        if (!isCancellingShields()) {
            if (dontAttackWhenBlocking.value && entity is LivingEntity && entity.isBlocking)
                if (!simulateShieldBlock.value || entity.blockedByShield(DamageSource.player(mc.player)))
                    return false
        } else if (counterBlocking.isSelected(1)) {
            if (entity is PlayerEntity) {
                if (entity.offHandStack.item is ShieldItem && !entity.isBlocking)
                    return false
            }
        }

        if(SmartClickingBehaviour.enableSmartClickingBehaviour.value && entity is LivingEntity) {
            val otherHurtTime = entity.smoothedHurtTime()
            // Is the enemy attackable?
            if(otherHurtTime >= SmartClickingBehaviour.enemyHurtTime.value) {
                // If not, have we just been attacked?
                val selfHurtTime = mc.player?.smoothedHurtTime()!!
                if(selfHurtTime <= SmartClickingBehaviour.selfHurtTime.value)
                    return false // Have we received damage lately? If yes, ignore this opportunity, because we need to reduce our knock-back
            }
        }
        return true
    }

    private fun willPerformCritical(criticalSprint: Boolean, fallDistance: Boolean): Boolean {
        if (mc.player?.isOnGround != true &&
            mc.player?.isClimbing != true &&
            mc.player?.isTouchingWater != true &&
            !(mc.player as ILivingEntity).tarasande_forceHasStatusEffect(StatusEffects.BLINDNESS) &&
            mc.player?.hasVehicle() == false)
            if (!fallDistance || mc.player?.fallDistance!! > 0.0F)
                if (!criticalSprint || !mc.player?.isSprinting!!)
                    return true
        return false
    }

    private fun isCancellingShields(): Boolean {
        if (ManagerModule.get(ModuleAutoTool::class.java).let { it.enabled.value && it.mode.isSelected(1) && it.useAxeToCounterBlocking.value } && ContainerUtil.getHotbarSlots().any { it.item is AxeItem })
            return true
        return mc.player?.disablesShield() == true
    }

}