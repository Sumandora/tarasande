package su.mandora.tarasande.module.player

import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.util.Hand
import net.minecraft.util.UseAction
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.util.registry.Registry
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventAttack
import su.mandora.tarasande.event.EventJump
import su.mandora.tarasande.event.EventMovement
import su.mandora.tarasande.event.EventPollEvents
import su.mandora.tarasande.mixin.accessor.IEntity
import su.mandora.tarasande.mixin.accessor.IMinecraftClient
import su.mandora.tarasande.util.math.TimeUtil
import su.mandora.tarasande.util.math.rotation.Rotation
import su.mandora.tarasande.util.math.rotation.RotationUtil
import su.mandora.tarasande.util.player.PlayerUtil
import su.mandora.tarasande.util.player.clickspeed.ClickMethodCooldown
import su.mandora.tarasande.util.player.clickspeed.ClickSpeedUtil
import su.mandora.tarasande.util.render.RenderUtil
import su.mandora.tarasande.value.*
import java.util.concurrent.ThreadLocalRandom
import java.util.function.Consumer
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.round
import kotlin.math.sin

class ModuleScaffoldWalk : Module("Scaffold walk", "Places blocks underneath you", ModuleCategory.PLAYER) {

    private val delay = ValueNumber(this, "Delay", 0.0, 0.0, 500.0, 50.0)
    private val alwaysClick = ValueBoolean(this, "Always click", false)
    private val clickSpeedUtil = ClickSpeedUtil(this, { alwaysClick.value }, ClickMethodCooldown::class.java)
    private val aimSpeed = ValueNumberRange(this, "Aim speed", 0.0, 1.0, 1.0, 1.0, 0.1)
    private val goalYaw = ValueNumber(this, "Goal yaw", 0.0, 180.0, 360.0, 45.0)
    private val offsetGoalYaw = ValueBoolean(this, "Offset goal yaw", true)
    private val edgeDistance = ValueNumber(this, "Edge distance", 0.0, 0.5, 1.0, 0.05)
    private val edgeIncrement = ValueBoolean(this, "Edge increment", true)
    private val edgeIncrementValue = object : ValueNumber(this, "Edge increment value", 0.0, 0.15, 0.5, 0.05) {
        override fun isEnabled() = edgeIncrement.value
    }
    private val preventImpossibleEdge = object : ValueBoolean(this, "Prevent impossible edge", true) {
        override fun isEnabled() = edgeIncrement.value
    }
    private val rotateAtEdge = ValueBoolean(this, "Rotate at edge", false)
    private val rotateAtEdgeMode = object : ValueMode(this, "Rotate at edge mode", false, "Distance", "Extrapolated position") {
        override fun isEnabled() = rotateAtEdge.value
    }
    private val rotateAtEdgeDistance = object : ValueNumber(this, "Rotate at edge distance", 0.0, 0.5, 1.0, 0.05) {
        override fun isEnabled() = rotateAtEdgeMode.isEnabled() && rotateAtEdgeMode.isSelected(0)
    }
    private val rotateAtEdgeExtrapolation = object : ValueNumber(this, "Rotate at edge extrapolation", 0.0, 1.0, 10.0, 1.0) {
        override fun isEnabled() = rotateAtEdgeMode.isEnabled() && rotateAtEdgeMode.isSelected(1)
    }
    private val silent = ValueBoolean(this, "Silent", false)
    private val lockView = ValueBoolean(this, "Lock view", false)
    private val headRoll = ValueMode(this, "Head roll", false, "Disabled", "Advantage", "Autism")
    private val forbiddenItems = object : ValueRegistry<Item>(this, "Forbidden items", Registry.ITEM) {
        override fun filter(key: Item) = key is BlockItem
        override fun keyToString(key: Any?) = (key as Item).name.string
    }
    private val cubeShape = ValueBoolean(this, "Cube shape", true)
    private val tower = ValueMode(this, "Tower", false, "Vanilla", "Motion", "Teleport")

    private val targets = ArrayList<Pair<BlockPos, Direction>>()
    private val timeUtil = TimeUtil()

    private var target: Pair<BlockPos, Direction>? = null
    private var lastRotation: Rotation? = null
    private var prevEdgeDistance = 0.5

    init {
        for (y in 0 downTo -1) {
            // up
            if (y == 0) {
                targets.add(Pair(BlockPos(0, -1, 0), Direction.UP))
                targets.add(Pair(BlockPos(0, 1, 0), Direction.DOWN))
            }
            // straight
            targets.add(Pair(BlockPos(0, y, 1), Direction.SOUTH))
            targets.add(Pair(BlockPos(1, y, 0), Direction.EAST))
            targets.add(Pair(BlockPos(-1, y, 0), Direction.WEST))
            targets.add(Pair(BlockPos(0, y, -1), Direction.NORTH))

            // diagonals clockwise
            targets.add(Pair(BlockPos(-1, y, 1), Direction.SOUTH))
            targets.add(Pair(BlockPos(-1, y, -1), Direction.WEST))
            targets.add(Pair(BlockPos(1, y, 1), Direction.EAST))
            targets.add(Pair(BlockPos(1, y, -1), Direction.NORTH))

            // diagonals counter-clockwise
            targets.add(Pair(BlockPos(-1, y, 1), Direction.WEST))
            targets.add(Pair(BlockPos(-1, y, -1), Direction.NORTH))
            targets.add(Pair(BlockPos(1, y, 1), Direction.SOUTH))
            targets.add(Pair(BlockPos(1, y, -1), Direction.EAST))
        }
    }

    override fun onEnable() {
        prevEdgeDistance = 0.5
        clickSpeedUtil.reset()
    }

    private fun placeBlock(blockHitResult: BlockHitResult) {
        val original = mc.crosshairTarget
        mc.crosshairTarget = blockHitResult
        (mc as IMinecraftClient).invokeDoItemUse()
        mc.crosshairTarget = original
    }

    override fun onDisable() {
        target = null
        lastRotation = null
    }

    private fun getAdjacentBlock(blockPos: BlockPos): Pair<BlockPos, Direction>? {
        val arrayList = ArrayList<Triple<BlockPos, BlockPos, Direction>>()
        for (target in targets) {
            val adjacent = blockPos.add(target.first.x, target.first.y, target.first.z)
            if (!mc.world?.isAir(adjacent)!!)
                arrayList.add(Triple(target.first, adjacent, target.second))
        }
        var best: Pair<BlockPos, Direction>? = null
        var dist = 0.0
        for (target in arrayList) {
            if (mc.player?.blockPos?.y!! > target.second.y)
                return Pair(target.second, target.third)
            val dist2 = Vec3d.ofCenter(target.second).add(Vec3d.of(target.third.opposite.vector).multiply(0.5)).subtract(mc.player?.pos!!).horizontalLengthSquared()
            if (best == null || dist2 < dist) {
                best = Pair(target.second, target.third)
                dist = dist2
            }
        }
        return best
    }

    val eventConsumer = Consumer<Event> { event ->
        when (event) {
            is EventPollEvents -> {
                if (lastRotation != null) {
                    when {
                        headRoll.isSelected(1) -> {
                            lastRotation = Rotation(mc.player?.yaw!!, lastRotation?.pitch!!)
                        }
                        headRoll.isSelected(2) -> {
                            lastRotation = Rotation(mc.player?.yaw!! * mc.player?.age!! * 45, lastRotation?.pitch!!)
                        }
                    }
                }

                val below = mc.player?.blockPos?.add(0, -1, 0)!!
                val currentRot = if (RotationUtil.fakeRotation != null) Rotation(RotationUtil.fakeRotation!!) else Rotation(mc.player!!)
                if (mc.world?.isAir(below)!!) {
                    target = getAdjacentBlock(below)
                    if (target != null) {
                        if (!rotateAtEdge.value ||
                            (
                                    (rotateAtEdgeMode.isSelected(0) && round(Vec3d.ofCenter(target?.first).subtract(mc.player?.pos!!).multiply(Vec3d.of(target?.second?.vector)).horizontalLengthSquared() * 100) / 100.0 in (rotateAtEdgeDistance.value * rotateAtEdgeDistance.value)..1.0 || target?.second?.offsetY != 0) ||
                                            (rotateAtEdgeMode.isSelected(1) && PlayerUtil.isOnEdge(rotateAtEdgeExtrapolation.value))
                                    )
                        ) {
                            var point = Vec3d.of(target?.first)
                            var bestPoint: Vec3d? = null
                            var rotDelta = 0.0
                            var x = 0.0
                            while (x <= 1.0) {
                                var y = 0.0
                                while (y <= 1.0) {
                                    var z = 0.0
                                    while (z <= 1.0) {
                                        val newPoint = point.add(Vec3d(x, y, z))
                                        val rot = RotationUtil.getRotations(mc.player?.eyePos!!, newPoint)
                                        val rotationVector =
                                            (mc.player as IEntity).invokeGetRotationVector(rot.pitch, rot.yaw)
                                        val hitResult = PlayerUtil.rayCast(
                                            mc.player?.eyePos!!,
                                            mc.player?.eyePos?.add(rotationVector.multiply(mc.interactionManager?.reachDistance?.toDouble()!!))!!
                                        )
                                        if (hitResult != null && hitResult.type == HitResult.Type.BLOCK && hitResult.side == (if (target?.second?.offsetY != 0) target?.second else target?.second?.opposite) && hitResult.blockPos == target?.first) {
                                            val dir = target?.second?.opposite!!
                                            val delta = /*if (lastRotation != null) lastRotation?.fov(rot)?.toDouble()!! else */abs(
                                                MathHelper.wrapDegrees
                                                    (
                                                    (
                                                            if (dir.offsetY != 0 && mc.player?.velocity?.horizontalLengthSquared()!! <= 0.01)
                                                                mc.player?.yaw?.toDouble()!!
                                                            else if (dir.offsetY != 0 || offsetGoalYaw.value)
                                                                RotationUtil.getYaw(mc.player?.velocity?.x!!, mc.player?.velocity?.z!!)
                                                            else
                                                                RotationUtil.getYaw(Vec3d.of(dir.vector))
                                                            )
                                                            -
                                                            (rot.yaw + goalYaw.value)
                                                )
                                            )
                                            if (bestPoint == null || rotDelta > delta) {
                                                bestPoint = newPoint
                                                rotDelta = delta
                                            }
                                        }
                                        z += 0.05
                                    }
                                    y += 0.05
                                }
                                x += 0.05
                            }

                            point = bestPoint ?: point
                            lastRotation = RotationUtil.getRotations(mc.player?.eyePos!!, point)
                        }
                    } else {
                        prevEdgeDistance = 0.5
                        clickSpeedUtil.reset()
                    }
                }
                if (lastRotation == null) {
                    var rad = (if (mc.player?.input?.movementInput?.lengthSquared()!! > 0.0) PlayerUtil.getMoveDirection() + Math.PI else Math.toRadians(mc.player?.yaw!! - 90.0))
                    if (!offsetGoalYaw.value)
                        rad -= Math.toRadians(goalYaw.value - 180.0)
                    val targetRot = RotationUtil.getRotations(
                        mc.player?.eyePos!!,
                        mc.player?.pos?.add(cos(rad), 0.0, sin(rad))?.add(
                            0.0,
                            -3.0 /* premium way of forcing a far look down without locking pitch to some value */,
                            0.0
                        )!!
                    )

                    lastRotation = targetRot
                }

                event.rotation = currentRot.smoothedTurn(
                    lastRotation!!,
                    if (aimSpeed.minValue == 1.0 && aimSpeed.maxValue == 1.0)
                        1.0
                    else
                        MathHelper.clamp((
                                if (aimSpeed.minValue == aimSpeed.maxValue)
                                    aimSpeed.minValue
                                else
                                    ThreadLocalRandom.current().nextDouble(aimSpeed.minValue, aimSpeed.maxValue)) * RenderUtil.deltaTime * 0.05,
                            0.0,
                            1.0
                        )
                ).correctSensitivity()

                if (lockView.value) {
                    mc.player?.yaw = event.rotation.yaw
                    mc.player?.pitch = event.rotation.pitch
                }

                event.minRotateToOriginSpeed = aimSpeed.minValue
                event.maxRotateToOriginSpeed = aimSpeed.maxValue
            }
            is EventAttack -> {
                if (target == null || RotationUtil.fakeRotation == null || event.dirty) {
                    clickSpeedUtil.reset()
                    return@Consumer
                }

                val airBelow = mc.world?.isAir(BlockPos(mc.player?.pos?.add(0.0, -1.0, 0.0)))!!

                if (airBelow || alwaysClick.value) {
                    val newEdgeDist = getNewEdgeDist()
                    val rotationVector = (mc.player as IEntity).invokeGetRotationVector(
                        RotationUtil.fakeRotation?.pitch!!,
                        RotationUtil.fakeRotation?.yaw!!
                    )
                    val hitResult = PlayerUtil.rayCast(
                        mc.player?.eyePos!!,
                        mc.player?.eyePos?.add(rotationVector.multiply(mc.interactionManager?.reachDistance?.toDouble()!!))!!
                    )
                    val clicks = clickSpeedUtil.getClicks()
                    val prevSlot = mc.player?.inventory?.selectedSlot
                    var hasBlock = false
                    for (hand in Hand.values()) {
                        val stack = mc.player?.getStackInHand(hand)
                        if (stack != null) {
                            if (stack.item is BlockItem && isBlockItemValid(stack.item as BlockItem)) {
                                hasBlock = true
                            } else if (stack.item.getUseAction(stack) != UseAction.NONE) {
                                break
                            }
                        }
                    }
                    if (!hasBlock) {
                        if (silent.value) {
                            var blockAmount = 0
                            var blockSlot: Int? = null
                            for (slot in 0..8) {
                                val stack = mc.player?.inventory?.main?.get(slot)
                                if (stack != null && stack.item is BlockItem && isBlockItemValid(stack.item as BlockItem)) {
                                    if (blockSlot == null || blockAmount > stack.count) {
                                        blockSlot = slot
                                        blockAmount = stack.count
                                    }
                                }
                            }
                            if (blockSlot != null) {
                                mc.player?.inventory?.selectedSlot = blockSlot
                            } else return@Consumer
                        } else return@Consumer
                    }
                    if (hitResult != null) {
                        if (hitResult.type == HitResult.Type.BLOCK && hitResult.side == (if (target?.second?.offsetY != 0) target?.second else target?.second?.opposite) && hitResult.blockPos == target?.first) {
                            if ((airBelow && (round(
                                    Vec3d.ofCenter(target?.first).subtract(mc.player?.pos!!)
                                        .multiply(Vec3d.of(target?.second?.vector)).horizontalLengthSquared() * 100
                                ) / 100.0 in (newEdgeDist * newEdgeDist)..1.0 || target?.first?.y!! < mc.player?.y!!))
                            ) {
                                if (timeUtil.hasReached(delay.value.toLong())) {
                                    placeBlock(hitResult)
                                    event.dirty = true

                                    if (target?.second?.offsetY == 0) {
                                        if (edgeIncrement.value && preventImpossibleEdge.value && prevEdgeDistance > newEdgeDist && mc.player?.isOnGround!!)
                                            mc.player?.jump()
                                        prevEdgeDistance = newEdgeDist
                                    }

                                    timeUtil.reset()
                                }
                            }
                        } else if (alwaysClick.value) {
                            for (i in 1..clicks)
                                placeBlock(hitResult)
                            event.dirty = true
                        }
                    }
                    if (silent.value) {
                        mc.player?.inventory?.selectedSlot = prevSlot
                    }
                }
            }
            is EventMovement -> {
                if (event.entity != mc.player)
                    return@Consumer
                if (target != null) {
                    if (mc.player?.input?.jumping!!) {
                        when {
                            tower.isSelected(1) -> {
                                val velocity = event.velocity.add(0.0, 0.0, 0.0)
                                val playerVelocity = mc.player?.velocity?.add(0.0, 0.0, 0.0)
                                mc.player?.jump()
                                event.velocity = velocity?.withAxis(Direction.Axis.Y, mc.player?.velocity?.y!!)!!
                                mc.player?.velocity = playerVelocity
                            }
                            tower.isSelected(2) -> {
                                event.velocity = event.velocity.withAxis(Direction.Axis.Y, 1.0)
                            }
                        }
                    }
                }
            }
            is EventJump -> {
                if (event.state != EventJump.State.PRE) return@Consumer
                prevEdgeDistance = 0.5
            }
        }
    }

    private fun isBlockItemValid(blockItem: BlockItem): Boolean {
        if (forbiddenItems.list.contains(blockItem)) return false

        return if (cubeShape.value) {
            val block = blockItem.block
            val shape = block.defaultState.getCollisionShape(mc.world, BlockPos.ORIGIN)
            !shape.isEmpty && shape.boundingBox.xLength == 1.0 && shape.boundingBox.yLength == 1.0 && shape.boundingBox.zLength == 1.0 && block.defaultState.material.isSolid
        } else true
    }

    private fun getNewEdgeDist(): Double {
        return if (!edgeIncrement.value)
            edgeDistance.value
        else {
            val newEdgeDist = prevEdgeDistance + edgeIncrementValue.value
            if (newEdgeDist > edgeDistance.value) 0.5 else round(newEdgeDist * 100) / 100.0
        }
    }
}