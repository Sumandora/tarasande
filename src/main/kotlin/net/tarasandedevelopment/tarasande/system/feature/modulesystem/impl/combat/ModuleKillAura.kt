package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.combat

import net.minecraft.client.gui.screen.ingame.HandledScreen
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
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket
import net.minecraft.util.Hand
import net.minecraft.util.UseAction
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import net.minecraft.util.shape.VoxelShapes
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.event.*
import net.tarasandedevelopment.tarasande.injection.accessor.ILivingEntity
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.*
import net.tarasandedevelopment.tarasande.system.feature.clickmethodsystem.api.ClickSpeedUtil
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.movement.ModuleClickTP
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.player.ModuleAutoTool
import net.tarasandedevelopment.tarasande.util.extension.minecraft.minus
import net.tarasandedevelopment.tarasande.util.extension.minecraft.plus
import net.tarasandedevelopment.tarasande.util.extension.minecraft.times
import net.tarasandedevelopment.tarasande.util.math.MathUtil
import net.tarasandedevelopment.tarasande.util.math.rotation.Rotation
import net.tarasandedevelopment.tarasande.util.math.rotation.RotationUtil
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil
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
    private val dontAttackInvalidRaytraceEntities = object : ValueBoolean(this, "Don't attack invalid raytrace entities", false) {
        override fun isEnabled() = rayTrace.value
    }
    private val simulateMouseDelay = object : ValueBoolean(this, "Simulate mouse delay", false) {
        override fun isEnabled() = rayTrace.value && !mode.isSelected(1)
    }
    private val swingInAir = ValueBoolean(this, "Swing in air", true)
    private val aimSpeed = ValueNumberRange(this, "Aim speed", 0.1, 1.0, 1.0, 1.0, 0.1)
    private val dontAttackWhenBlocking = ValueBoolean(this, "Don't attack when blocking", false)
    private val simulateShieldBlock = object : ValueBoolean(this, "Simulate shield block", false) {
        override fun isEnabled() = dontAttackWhenBlocking.value
    }
    private val throughWalls = ValueMode(this, "Through walls", false, "Off", "Continue aiming", "Hit and aim through walls")
    private val attackCooldown = ValueBoolean(this, "Attack cooldown", false)
    private val autoBlock = object : ValueMode(this, "Auto block", false, "Disabled", "Permanent", "Legit") {
        override fun onChange() {
            blocking = false
        }
    }
    private val blockItem = object : ValueMode(this, "Block item", false, "Shield", "Sword") {
        override fun isEnabled() = !autoBlock.isSelected(0)
    }
    private val needUnblock = object : ValueBoolean(this, "Need unblock", true) {
        override fun isEnabled() = autoBlock.isSelected(1)
    }
    private val blockOutOfReach = object : ValueBoolean(this, "Block out of reach", true) {
        override fun isEnabled() = !autoBlock.isSelected(0)
    }
    private val preventBlockCooldown = object : ValueBoolean(this, "Prevent block cooldown", false) {
        override fun isEnabled() = !autoBlock.isSelected(0)
    }
    private val counterBlocking = ValueMode(this, "Counter blocking", false, "Off", "Wait for block", "Immediately")
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
    private val closedInventory = ValueBoolean(this, "Closed inventory", false)
    private val aimTargetColor = ValueColor(this, "Aim target color", 0.0, 1.0, 1.0, 1.0)

    val targets = CopyOnWriteArrayList<Pair<Entity, Vec3d>>()
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

    override fun onEnable() {
        clickSpeedUtil.reset()
    }

    override fun onDisable() {
        blocking = false
        lastFlex = null
        targets.clear()
        waitForDamage = true
        teleportPath = null
    }

    private fun fovRotation() = if (fakeRotationFov.value && RotationUtil.fakeRotation != null) RotationUtil.fakeRotation!! else Rotation(mc.player!!)

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
        registerEvent(EventPollEvents::class.java) { event ->
            val prevTargets = ArrayList(targets)
            targets.clear()
            val currentRot = if (RotationUtil.fakeRotation != null) Rotation(RotationUtil.fakeRotation!!) else Rotation(mc.player!!)
            if (closedInventory.value && mc.currentScreen is HandledScreen<*>) {
                if (RotationUtil.fakeRotation != null) {
                    event.rotation = currentRot
                    event.minRotateToOriginSpeed = aimSpeed.minValue
                    event.maxRotateToOriginSpeed = aimSpeed.maxValue
                }
                return@registerEvent
            }
            for (entity in mc.world?.entities!!) {
                if (!PlayerUtil.isAttackable(entity)) continue

                val boundingBox = entity.boundingBox.expand(entity.targetingMargin.toDouble())
                val bestAimPoint = MathUtil.getBestAimPoint(boundingBox)
                if (bestAimPoint.squaredDistanceTo(mc.player?.eyePos!!) > reach.maxValue * reach.maxValue) continue
                if (RotationUtil.getRotations(mc.player?.eyePos!!, bestAimPoint).fov(fovRotation()) > fov.value) continue
                var aimPoint =
                    if (boundingBox.contains(mc.player?.eyePos) && mc.player?.input?.movementInput?.lengthSquared() != 0.0F) {
                        mc.player?.eyePos!! + currentRot.forwardVector(0.01)
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

            val lowestHurtTime = target.first.let { if (it is LivingEntity && it.maxHurtTime > 0) (it.hurtTime - mc.tickDelta).coerceAtLeast(0.0F) / it.maxHurtTime else null }

            if (!flex.value || lowestHurtTime == null || lowestHurtTime < flexHurtTime.value) {
                finalRot = currentRot.smoothedTurn(targetRot, aimSpeed)
                val hitResult = PlayerUtil.getTargetedEntity(reach.minValue, finalRot)
                if (guaranteeHit.value && target.second.squaredDistanceTo(mc.player?.eyePos!!) <= reach.minValue * reach.minValue && (hitResult == null || hitResult !is EntityHitResult || hitResult.entity == null)) {
                    finalRot = targetRot
                }
            } else {
                val delta = (lowestHurtTime - flexHurtTime.value) / (1.0 - flexHurtTime.value)
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
                    if (RotationUtil.fakeRotation == null) {
                        return@forEach
                    } else {
                        val hitResult = PlayerUtil.getTargetedEntity(
                            reach.minValue,
                            if (!mode.isSelected(1))
                                if (simulateMouseDelay.value)
                                    Rotation(mc.player!!.lastYaw, mc.player!!.lastPitch)
                                else
                                    RotationUtil.fakeRotation!!
                            else
                                RotationUtil.getRotations(mc.player?.eyePos!!, aimPoint),
                            throughWalls.isSelected(2))
                        if (hitResult == null || hitResult !is EntityHitResult || hitResult.entity == null) {
                            return@forEach
                        } else {
                            target = hitResult.entity
                            if (!PlayerUtil.isAttackable(target) && dontAttackInvalidRaytraceEntities.value)
                                return@forEach
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

            if (validEntities.isNotEmpty() && !event.dirty && (!closedInventory.value || mc.currentScreen !is HandledScreen<*>)) {
                var clicks = clickSpeedUtil.getClicks()

                if (clicks == 0)
                    if (isCancellingShields() && !allAttackedLivingEntities { !it.isBlocking } && (counterBlocking.isSelected(2)))
                        clicks = 1 // Axes can cancel out shields whenever they want, so lets force a hit

                if (!autoBlock.isSelected(0) && mc.player?.isUsingItem!! && clicks > 0) {
                    if (unblock())
                        return@registerEvent
                }

                if (!mc.player?.isUsingItem!! || (autoBlock.isSelected(1) && !needUnblock.value)) {
                    var imaginaryPosition = mc.player?.pos!!
                    teleportPath = ArrayList()
                    val maxTeleportTime = (mc.renderTickCounter.tickTime / targets.size.toDouble()).toLong()
                    for (pair in validEntities) {
                        val target = pair.first
                        val aimPoint = pair.second

                        val distance = aimPoint.squaredDistanceTo(mc.player?.eyePos!!)

                        if (distance > 6.0 * 6.0 && distance <= reach.minValue * reach.minValue) {
                            (TarasandeMain.managerModule().get(ModuleClickTP::class.java).pathFinder.findPath(imaginaryPosition, target.pos, maxTeleportTime) ?: continue).forEach {
                                mc.networkHandler?.sendPacket(PlayerMoveC2SPacket.PositionAndOnGround(it.x, it.y, it.z, mc.world?.getBlockState(BlockPos(it.add(0.0, -1.0, 0.0)))?.isAir == false))
                                teleportPath?.add(it)
                                imaginaryPosition = it
                            }
                        }

                        attack(target, clicks)
                        lastFlex = null
                        event.dirty = true
                        waitForHit = false
                        attacked = true

                        if (mode.isSelected(0))
                            break
                    }
                    if (!attacked && swingInAir.value) {
                        if (PlayerUtil.getTargetedEntity(reach.minValue, RotationUtil.fakeRotation ?: Rotation(mc.player!!), false)?.type == HitResult.Type.MISS) {
                            attack(null, clicks)
                            event.dirty = true
                        }
                    }
                    if (mc.player?.pos != imaginaryPosition) {
                        TarasandeMain.managerModule().get(ModuleClickTP::class.java).pathFinder.findPath(imaginaryPosition, mc.player?.pos!!, maxTeleportTime)?.forEach {
                            mc.networkHandler?.sendPacket(PlayerMoveC2SPacket.PositionAndOnGround(it.x, it.y, it.z, mc.world?.getBlockState(BlockPos(it.add(0.0, -1.0, 0.0)))?.isAir == false))
                            teleportPath?.add(it)
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
            if(mc.crosshairTarget != null && mc.crosshairTarget?.type == HitResult.Type.ENTITY)
                RenderUtil.blockOutline(event.matrices, VoxelShapes.cuboid(Box.from(mc.crosshairTarget?.pos).offset(-0.5, -0.5, -0.5).expand(-0.45)), aimTargetColor.getColor().rgb)
            RenderUtil.renderPath(event.matrices, teleportPath ?: return@registerEvent, -1)
        }
    }

    private fun attack(entity: Entity?, repeat: Int) {
        for (i in 0 until repeat) {
            if (!attackCooldown.value) {
                mc.attackCooldown = 0
            }
            PlayerUtil.attack(entity)
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
            val dist = 1.0 - MathUtil.getBias(mc.player?.eyePos?.distanceTo(aimPoint)!! / reach.maxValue.coerceAtLeast(reach.minValue + 0.1), 0.65) // I have no idea why this works and looks like it does, but it's good, so why remove it then
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
            val aimDelta = (RotationUtil.fakeRotation ?: Rotation(mc.player!!)).fov(RotationUtil.getRotations(mc.player?.eyePos!!, aimPoint)) / Rotation.MAXIMUM_DELTA
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
        when {
            blockItem.isSelected(0) -> {
                val stack = mc.player?.getStackInHand(PlayerUtil.getUsedHand() ?: return)
                if (stack?.item !is ShieldItem || mc.player?.itemCooldownManager?.isCoolingDown(stack.item)!!)
                    return
            }

            blockItem.isSelected(1) -> {
                val stack = mc.player?.getStackInHand(Hand.MAIN_HAND)
                if (stack?.item !is SwordItem || mc.player?.getStackInHand(Hand.OFF_HAND)?.useAction != UseAction.NONE)
                    return
            }
        }

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
        if (TarasandeMain.managerModule().get(ModuleAutoTool::class.java).let { it.enabled && it.mode.isSelected(1) && it.useAxeToCounterBlocking.value } &&
            mc.player?.inventory?.main?.subList(0, 8)?.any { it.item is AxeItem } == true)
            return true
        return mc.player?.disablesShield() == true
    }

}