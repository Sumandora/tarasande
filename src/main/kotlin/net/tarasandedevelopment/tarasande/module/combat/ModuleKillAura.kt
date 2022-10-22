package net.tarasandedevelopment.tarasande.module.combat

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.BufferRenderer
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexFormat
import net.minecraft.client.render.VertexFormats
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityStatuses
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.passive.PassiveEntity
import net.minecraft.entity.player.PlayerEntity
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
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.*
import net.tarasandedevelopment.tarasande.module.movement.ModuleClickTP
import net.tarasandedevelopment.tarasande.util.extension.minus
import net.tarasandedevelopment.tarasande.util.extension.plus
import net.tarasandedevelopment.tarasande.util.extension.times
import net.tarasandedevelopment.tarasande.util.math.MathUtil
import net.tarasandedevelopment.tarasande.util.math.rotation.Rotation
import net.tarasandedevelopment.tarasande.util.math.rotation.RotationUtil
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil
import net.tarasandedevelopment.tarasande.util.player.clickspeed.ClickSpeedUtil
import net.tarasandedevelopment.tarasande.value.ValueBoolean
import net.tarasandedevelopment.tarasande.value.ValueMode
import net.tarasandedevelopment.tarasande.value.ValueNumber
import net.tarasandedevelopment.tarasande.value.ValueNumberRange
import org.lwjgl.opengl.GL11
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
    private val counterBlocking = ValueBoolean(this, "Counter blocking", false)
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
    private var clicked = false
    private var performedTick = false
    private var lastFlex: Rotation? = null
    private var waitForDamage = true

    private var teleportPath: ArrayList<Vec3d>? = null
    private var entityCooldowns = HashMap<Entity, Int>()

    override fun onEnable() {
        clickSpeedUtil.reset()
    }

    override fun onDisable() {
        clicked = false
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

    private fun allAttacked(block: (LivingEntity) -> Boolean): Boolean {
        return (mode.isSelected(0) && targets.firstOrNull()?.first.let { it is LivingEntity && block(it) }) || (mode.isSelected(1) && targets.all { it.first is LivingEntity && block((it.first as LivingEntity)) })
    }

    init {
        registerEvent(EventPollEvents::class.java) { event ->
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
                waitForDamage = true
                teleportPath = null
                return@registerEvent
            }

            targets.sortWith(comparator)

            targets.sortBy { it.first is LivingEntity && (it.first as LivingEntity).isDead }
            targets.sortBy { it.second.squaredDistanceTo(mc.player?.eyePos!!) > reach.minValue * reach.minValue }
            targets.sortBy { shouldAttackEntity(it.first) }

            val target = targets[0]

            val targetRot = RotationUtil.getRotations(mc.player?.eyePos!!, target.second)
            var finalRot = targetRot

            val lowestHurtTime = target.first.let { if(it is LivingEntity && it.maxHurtTime > 0) (it.hurtTime - mc.tickDelta).coerceAtLeast(0.0f) / it.maxHurtTime else null }

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
            clicked = false

            var canHit = true

            if (waitForCritical.value) if (!dontWaitWhenEnemyHasShield.value || allAttacked { !hasShield(it) })
                if (!mc.player?.isClimbing!! && !mc.player?.isTouchingWater!! && !mc.player?.hasStatusEffect(StatusEffects.BLINDNESS)!! && !mc.player?.hasVehicle()!!)
                    if (!mc.player?.isOnGround!! && mc.player?.velocity?.y!! != 0.0 && (mc.player?.fallDistance == 0.0f || (criticalSprint.value && !mc.player?.isSprinting!!)))
                        canHit = false

            if (canHit && allAttacked { !shouldAttackEntity(it) })
                canHit = false

            if (canHit && waitForDamageValue.value && waitForDamage)
                canHit = false

            if (targets.isEmpty() || event.dirty || !canHit) {
                if (targets.isNotEmpty())
                    block() // This is a rare case of us, only being able to hit the enemy the first tick and later becoming unable to.
                clickSpeedUtil.reset()
                waitForHit = false
                return@registerEvent
            }

            val clicks = clickSpeedUtil.getClicks()

            if (!autoBlock.isSelected(0) && mc.player?.isUsingItem!! && clicks > 0) {
                if (unblock())
                    return@registerEvent
            }

            if (!mc.player?.isUsingItem!! || (autoBlock.isSelected(1) && !needUnblock.value)) {
                var attacked = false
                var imaginaryPosition = mc.player?.pos!!
                teleportPath = ArrayList()
                val maxTeleportTime = (mc.renderTickCounter.tickTime / targets.size.toDouble()).toLong()
                for (entry in targets) {
                    var target = entry.first
                    val aimPoint = entry.second

                    if (!shouldAttackEntity(target))
                        continue

                    val distance = aimPoint.squaredDistanceTo(mc.player?.eyePos!!)

                    if (rayTrace.value) {
                        if (RotationUtil.fakeRotation == null) {
                            continue
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
                                throughWalls.value)
                            if (hitResult == null || hitResult !is EntityHitResult || hitResult.entity == null) {
                                continue
                            } else {
                                target = hitResult.entity
                            }
                        }
                    } else if (distance > reach.minValue * reach.minValue) {
                        continue
                    }

                    if (distance > 6.0 * 6.0 && distance <= reach.minValue * reach.minValue) {
                        (TarasandeMain.get().managerModule.get(ModuleClickTP::class.java).pathFinder.findPath(imaginaryPosition, target.pos, maxTeleportTime) ?: continue).forEach {
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

                    if (!mode.isSelected(1))
                        break
                }
                if (!attacked && swingInAir.value) {
                    attack(null, clicks)
                    event.dirty = true
                }
                if (mc.player?.pos != imaginaryPosition) {
                    TarasandeMain.get().managerModule.get(ModuleClickTP::class.java).pathFinder.findPath(imaginaryPosition, mc.player?.pos!!, maxTeleportTime)?.forEach {
                        mc.networkHandler?.sendPacket(PlayerMoveC2SPacket.PositionAndOnGround(it.x, it.y, it.z, mc.world?.getBlockState(BlockPos(it.add(0.0, -1.0, 0.0)))?.isAir == false))
                        teleportPath?.add(it)
                    }
                }
            }

            if (targets.isNotEmpty() && targets.any { it.first !is PassiveEntity } && !waitForHit && !mc.player?.isUsingItem!! && !autoBlock.isSelected(0)) {
                block()
            }
        }

        registerEvent(EventKeyBindingIsPressed::class.java) { event ->
            if (event.keyBinding == mc.options.useKey) {
                if (targets.isNotEmpty()) {
                    event.pressed = blocking && (!preventBlockCooldown.value || allAttacked { !it.disablesShield() })
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

        registerEvent(EventHandleBlockBreaking::class.java) { event ->
            if (!throughWalls.value)
                event.parameter = event.parameter || clicked
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
            if (teleportPath == null || teleportPath?.isEmpty() == true)
                return@registerEvent
            RenderSystem.enableBlend()
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            RenderSystem.disableCull()
            GL11.glEnable(GL11.GL_LINE_SMOOTH)
            RenderSystem.disableDepthTest()
            event.matrices.push()
            val vec3d = MinecraftClient.getInstance().gameRenderer.camera.pos
            event.matrices.translate(-vec3d.x, -vec3d.y, -vec3d.z)
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
            val bufferBuilder = Tessellator.getInstance().buffer
            bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR)
            val matrix = event.matrices.peek()?.positionMatrix!!
            for (vec in teleportPath!!) {
                bufferBuilder.vertex(matrix, vec.x.toFloat(), vec.y.toFloat(), vec.z.toFloat()).color(1f, 1f, 1f, 1f).next()
            }
            BufferRenderer.drawWithShader(bufferBuilder.end())
            event.matrices.pop()
            RenderSystem.enableDepthTest()
            GL11.glDisable(GL11.GL_LINE_SMOOTH)
            RenderSystem.enableCull()
            RenderSystem.disableBlend()
        }
    }

    private fun attack(entity: Entity?, repeat: Int) {
        val original = mc.crosshairTarget
        if (entity != null) {
            mc.crosshairTarget = EntityHitResult(entity)
            if (!attackCooldown.value) {
                mc.attackCooldown = 0
            }
        } else {
            mc.crosshairTarget = object : HitResult(null) {
                override fun getType() = Type.MISS
            }
        }
        for (i in 0 until repeat)
            clicked = clicked or mc.doAttack()
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

            // Humans can't move their mouse in a straight line
            val aimDelta = (RotationUtil.fakeRotation ?: Rotation(mc.player!!)).fov(RotationUtil.getRotations(mc.player?.eyePos!!, aimPoint)) / Rotation.MAXIMUM_DELTA
            aimPoint = aimPoint.add(0.0, -aimDelta * box.yLength, 0.0)

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

    private fun block() {
        when {
            blockItem.isSelected(0) -> {
                val stack = mc.player?.getStackInHand(Hand.OFF_HAND)
                if (stack?.item !is ShieldItem || mc.player?.itemCooldownManager?.isCoolingDown(stack.item)!! || mc.player?.getStackInHand(Hand.MAIN_HAND)?.useAction != UseAction.NONE)
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
            mc.options.useKey.timesPressed = 1
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
        if (mc.player?.disablesShield() == false) {
            if (dontAttackWhenBlocking.value && entity is LivingEntity && entity.isBlocking)
                if (!simulateShieldBlock.value || entity.blockedByShield(DamageSource.player(mc.player)))
                    return false
        } else if (counterBlocking.value) {
            if (entity is PlayerEntity) {
                if (entity.offHandStack.item is ShieldItem && !entity.isBlocking)
                    return false
            }
        }
        return true
    }

}