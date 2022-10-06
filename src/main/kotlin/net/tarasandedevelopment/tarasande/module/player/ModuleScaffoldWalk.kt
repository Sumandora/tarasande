package net.tarasandedevelopment.tarasande.module.player

import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.util.Hand
import net.minecraft.util.UseAction
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.*
import net.minecraft.util.registry.Registry
import net.minecraft.util.shape.VoxelShapes
import net.tarasandedevelopment.tarasande.base.event.Event
import net.tarasandedevelopment.tarasande.base.event.Priority
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.*
import net.tarasandedevelopment.tarasande.mixin.accessor.IEntity
import net.tarasandedevelopment.tarasande.mixin.accessor.IKeyBinding
import net.tarasandedevelopment.tarasande.mixin.accessor.IMinecraftClient
import net.tarasandedevelopment.tarasande.util.extension.minus
import net.tarasandedevelopment.tarasande.util.extension.plus
import net.tarasandedevelopment.tarasande.util.extension.times
import net.tarasandedevelopment.tarasande.util.math.MathUtil
import net.tarasandedevelopment.tarasande.util.math.TimeUtil
import net.tarasandedevelopment.tarasande.util.math.rotation.Rotation
import net.tarasandedevelopment.tarasande.util.math.rotation.RotationUtil
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil
import net.tarasandedevelopment.tarasande.util.player.clickspeed.ClickMethodCooldown
import net.tarasandedevelopment.tarasande.util.player.clickspeed.ClickSpeedUtil
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import net.tarasandedevelopment.tarasande.value.*
import java.util.concurrent.ThreadLocalRandom
import java.util.function.Consumer
import kotlin.math.*

class ModuleScaffoldWalk : Module("Scaffold walk", "Places blocks underneath you", ModuleCategory.PLAYER) {

    private val delay = ValueNumber(this, "Delay", 0.0, 0.0, 500.0, 50.0)
    private val alwaysClick = ValueBoolean(this, "Always click", false)
    private val clickSpeedUtil = ClickSpeedUtil(this, { alwaysClick.value }, ClickMethodCooldown::class.java)
    private val aimSpeed = ValueNumberRange(this, "Aim speed", 0.1, 1.0, 1.0, 1.0, 0.1)

    private val goalYaw = ValueNumber(this, "Goal yaw", -60.0, 0.0, 60.0, 1.0)
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
    private val preventRerotation = ValueBoolean(this, "Prevent re-rotation", false)
    private val rerotateOnFacingChange = object : ValueBoolean(this, "Re-rotate on facing change", false) {
        override fun isEnabled() = preventRerotation.value
    }
    private val aimHeight = ValueNumber(this, "Aim height", 0.0, 0.5, 1.0, 0.05)
    private val silent = ValueBoolean(this, "Silent", false)
    private val lockView = ValueBoolean(this, "Lock view", false)
    private val headRoll = ValueMode(this, "Head roll", false, "Disabled", "Advantage", "Autism")
    private val forbiddenItems = object : ValueRegistry<Item>(this, "Forbidden items", Registry.ITEM) {
        override fun filter(key: Item) = key is BlockItem
        override fun getTranslationKey(key: Any?) = (key as Item).translationKey
    }
    private val cubeShape = ValueBoolean(this, "Cube shape", true)
    private val tower = ValueMode(this, "Tower", false, "Vanilla", "Motion", "Teleport")
    private val blockColor = ValueColor(this, "Block color", 0.0f, 1.0f, 1.0f, 1.0f)
    private val facingColor = ValueColor(this, "Facing color", 0.0f, 1.0f, 1.0f, 1.0f)
    private val aimTargetColor = ValueColor(this, "Aim target color", 0.0f, 1.0f, 1.0f, 1.0f)
    private val placeLineColor = ValueColor(this, "Place line color", 0.0f, 1.0f, 1.0f, 1.0f)

    private val targets = ArrayList<Pair<BlockPos, Direction>>()
    private val timeUtil = TimeUtil()

    private var target: Pair<BlockPos, Direction>? = null
    private var placeLine: Pair<Vec3d, Vec3d>? = null
    private var aimTarget: Vec3d? = null
    private var lastRotation: Rotation? = null
    private var prevEdgeDistance = 0.5
    private var preferredSide: Int? = null

    init {
        // up & down (down should never happen unless we are trying to save ourselves)
        targets.add(Pair(BlockPos(0, -1, 0), Direction.UP))
        targets.add(Pair(BlockPos(0, 1, 0), Direction.DOWN))

        for (y in 0 downTo -1) {
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

    private fun intersection(line1Begin: Vec2f, line1End: Vec2f, line2Begin: Vec2f, line2End: Vec2f): Vec2f {
        // https://en.wikipedia.org/wiki/Line%E2%80%93line_intersection
        return Vec2f(
            ((line1Begin.x * line1End.y - line1Begin.y * line1End.x) * (line2Begin.x - line2End.x) - (line1Begin.x - line1End.x) * (line2Begin.x * line2End.y - line2Begin.y * line2End.x)) / ((line1Begin.x - line1End.x) * (line2Begin.y - line2End.y) - (line1Begin.y - line1End.y) * (line2Begin.x - line2End.x)),
            ((line1Begin.x * line1End.y - line1Begin.y * line1End.x) * (line2Begin.y - line2End.y) - (line1Begin.y - line1End.y) * (line2Begin.x * line2End.y - line2Begin.y * line2End.x)) / ((line1Begin.x - line1End.x) * (line2Begin.y - line2End.y) - (line1Begin.y - line1End.y) * (line2Begin.x - line2End.x))
        )
    }

    override fun onEnable() {
        prevEdgeDistance = 0.5
        clickSpeedUtil.reset()
    }

    private fun placeBlock(blockHitResult: BlockHitResult) {
        val original = mc.crosshairTarget
        mc.crosshairTarget = blockHitResult
        (mc as IMinecraftClient).tarasande_invokeDoItemUse()
        mc.crosshairTarget = original
    }

    override fun onDisable() {
        target = null
        aimTarget = null
        placeLine = null
        lastRotation = null
        preferredSide = null
    }

    private fun getAdjacentBlock(blockPos: BlockPos): Pair<BlockPos, Direction>? {
        val arrayList = ArrayList<Triple<BlockPos, BlockPos, Direction>>()
        for (target in targets) {
            val adjacent = blockPos.add(target.first.x, target.first.y, target.first.z)
            if (!mc.world?.isAir(adjacent)!!) arrayList.add(Triple(target.first, adjacent, target.second))
        }
        var best: Pair<BlockPos, Direction>? = null
        var dist = 0.0
        for (target in arrayList) {
            if (target.third.offsetY != 0) {
                if ((mc.options.jumpKey as IKeyBinding).tarasande_forceIsPressed())
                    return Pair(target.second, target.third)
                else if (best == null)
                    continue
            }
            val dist2 = (Vec3d.ofCenter(target.second) + Vec3d.of(target.third.opposite.vector) * 0.5 - mc.player?.pos!!).horizontalLengthSquared()
            if (best == null || dist2 < dist) {
                best = Pair(target.second, target.third)
                dist = dist2
            }
        }
        return best
    }

    @Priority(1001) // Killaura packets have to go first
    val eventConsumer = Consumer<Event> { event ->
        when (event) {
            is EventPollEvents -> {
                if (lastRotation != null) {
                    when {
                        headRoll.isSelected(1) -> {
                            lastRotation = Rotation(mc.player?.yaw!!, lastRotation?.pitch!!)
                        }

                        headRoll.isSelected(2) -> {
                            lastRotation = Rotation(lastRotation?.yaw!! + mc.player?.age!! * 45, lastRotation?.pitch!!)
                        }
                    }
                }

                val below = mc.player?.blockPos?.add(0, -1, 0)!!
                val currentRot = if (RotationUtil.fakeRotation != null) Rotation(RotationUtil.fakeRotation!!) else Rotation(mc.player!!)
                if (mc.world?.isAir(below)!!) {
                    val prevTarget = target
                    target = getAdjacentBlock(below)
                    if (target != null) {
                        if (!rotateAtEdge.value || ((rotateAtEdgeMode.isSelected(0) && ((Vec3d.ofCenter(target?.first) - mc.player?.pos!!) * Vec3d.of(target?.second?.vector)).horizontalLengthSquared() >= rotateAtEdgeDistance.value * rotateAtEdgeDistance.value || target?.second?.offsetY != 0) || (rotateAtEdgeMode.isSelected(1) && PlayerUtil.isOnEdge(rotateAtEdgeExtrapolation.value)))) {

                            if (lastRotation == null || run {
                                    if (!preventRerotation.value)
                                        true
                                    else if (rerotateOnFacingChange.value && (prevTarget != null && prevTarget.second != target?.second)) {
                                        preferredSide = null
                                        true
                                    } else {
                                        val rotationVector = (mc.player as IEntity).tarasande_invokeGetRotationVector(lastRotation?.pitch!!, lastRotation?.yaw!!)
                                        val hitResult = PlayerUtil.rayCast(mc.player?.eyePos!!, mc.player?.eyePos!! + rotationVector * mc.interactionManager?.reachDistance?.toDouble()!!)
                                        hitResult.type != HitResult.Type.BLOCK || hitResult.side != (if (target?.second?.offsetY != 0) target?.second else target?.second?.opposite) || hitResult.blockPos != target?.first
                                    }
                                }) {

                                val dirVec = target?.second?.vector!!
                                var point = Vec3d.ofCenter(target?.first) + Vec3d.of(dirVec) * -0.5
                                val eye = mc.player?.eyePos!!

                                val padding = 0.01

                                val finalPoint = if (target?.second?.offsetY != 0) {
                                    placeLine = null
                                    val blockPos = target?.first
                                    val blockState = mc.world?.getBlockState(target?.first)
                                    val shape = blockState?.getOutlineShape(mc.world, blockPos)?.offset(blockPos?.x?.toDouble()!!, blockPos.y.toDouble(), blockPos.z.toDouble())!!
                                    if (shape.isEmpty)
                                        point
                                    else
                                        MathUtil.closestPointToBox(aimTarget ?: eye, shape.boundingBox.expand(-padding))
                                } else {
                                    val rotatedVec = dirVec.let { Vec3d(abs(it.x) - 1.0, 0.0, abs(it.z) - 1.0) }
                                    point = point.add(0.0, MathHelper.clamp(aimHeight.value + ThreadLocalRandom.current().nextFloat() * 0.1 - 0.05, 0.0, 1.0) - 0.5, 0.0)

                                    val sideBegin = point + rotatedVec * 0.5
                                    val sideEnd = point + rotatedVec * -0.5

                                    placeLine = Pair(sideBegin, sideEnd)

                                    // calculating the closest point on the line to feet
                                    var goalAim = eye

                                    if (offsetGoalYaw.value) {
                                        val targetRot = mc.player?.yaw!! - 180
                                        goalAim = intersection( // adjust our target
                                            Vec2f(sideBegin.x.toFloat(), sideBegin.z.toFloat()), Vec2f(sideEnd.x.toFloat(), sideEnd.z.toFloat()),
                                            Vec2f(eye.x.toFloat(), eye.z.toFloat()), (eye + Rotation(targetRot, 0.0f).forwardVector(mc.interactionManager?.reachDistance?.toDouble()!!)).let { Vec2f(it.x.toFloat(), it.z.toFloat()) }
                                        ).let { Vec3d(it.x.toDouble(), point.y, it.y.toDouble()) }
                                    }

                                    val beginToPoint = Vec2f((goalAim.x - sideBegin.x).toFloat(), (goalAim.z - sideBegin.z).toFloat())
                                    val beginToEnd = Vec2f((sideEnd.x - sideBegin.x).toFloat(), (sideEnd.z - sideBegin.z).toFloat())

                                    val squaredDist = beginToEnd.lengthSquared()
                                    val dotProd = beginToPoint.dot(beginToEnd)

                                    var t = dotProd / squaredDist
                                    val prevT = t

                                    val closest = sideBegin + (sideEnd - sideBegin) * MathHelper.clamp(t.toDouble(), padding, 1.0 - padding)

                                    if (t in padding..1.0f - padding) {
                                        val dist = (eye - closest).horizontalLength()
                                        val a = sin(Math.toRadians(-goalYaw.value * (60 / 45f /* those are triangles bitch */))) * dist
                                        if (preferredSide == null) {
                                            val solutions = listOf(t + a.toFloat(), t - a.toFloat())
                                            val bestSolution = solutions.minBy { abs(it - 0.5) }
                                            t = bestSolution

                                            preferredSide = when {
                                                prevT < t -> 1
                                                prevT > t -> -1
                                                else -> null
                                            }
                                        } else {
                                            t += (a * preferredSide!!).toFloat()
                                        }
                                    }

                                    sideBegin + (sideEnd - sideBegin) * MathHelper.clamp(t.toDouble(), padding, 1.0f - padding)
                                }

                                aimTarget = finalPoint

                                lastRotation = RotationUtil.getRotations(eye, finalPoint)
                            }
                        }
                    } else {
                        prevEdgeDistance = 0.5
                        clickSpeedUtil.reset()
                    }
                }
                if (lastRotation == null) {
                    val rad = Math.toRadians(PlayerUtil.getMoveDirection()) - PI / 2
                    val targetRot = RotationUtil.getRotations(mc.player?.eyePos!!, mc.player?.pos!! + Vec3d(cos(rad), 0.0, sin(rad)) * 0.3)

                    val diagonal = abs(round(mc.player?.yaw!! / 90) * 90 - mc.player?.yaw!!) > 22.5
                    if (!diagonal)
                        targetRot.yaw += (-goalYaw.value * (45.0 / 60.0)).toFloat() // those are not triangles anymore :c

                    lastRotation = targetRot
                }

                event.rotation = currentRot.smoothedTurn(lastRotation!!, aimSpeed).correctSensitivity()

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
                    val rotationVector = (mc.player as IEntity).tarasande_invokeGetRotationVector(RotationUtil.fakeRotation?.pitch!!, RotationUtil.fakeRotation?.yaw!!)
                    val hitResult = PlayerUtil.rayCast(mc.player?.eyePos!!, mc.player?.eyePos!! + rotationVector * mc.interactionManager?.reachDistance?.toDouble()!!)
                    if (hitResult.type == HitResult.Type.BLOCK && hitResult.side == (if (target?.second?.offsetY != 0) target?.second else target?.second?.opposite) && hitResult.blockPos == target?.first) {
                        if (airBelow && (((Vec3d.ofCenter(target?.first) - mc.player?.pos!!) * Vec3d.of(target?.second?.vector)).horizontalLengthSquared() >= newEdgeDist * newEdgeDist || target?.first?.y!! < mc.player?.y!!)) {
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
                    if (silent.value) {
                        mc.player?.inventory?.selectedSlot = prevSlot
                    }
                }
            }

            is EventMovement -> {
                if (event.entity != mc.player)
                    return@Consumer
                if (target != null) {
                    if (PlayerUtil.input.jumping) {
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

            is EventRender3D -> {
                if (target != null) {
                    val blockPos = target?.first
                    val blockState = mc.world?.getBlockState(target?.first)
                    val shape = blockState?.getOutlineShape(mc.world, blockPos)?.offset(blockPos?.x?.toDouble()!!, blockPos.y.toDouble(), blockPos.z.toDouble())!!
                    RenderUtil.blockOutline(event.matrices, shape, blockColor.getColor().rgb)
                    val facing = target?.second?.opposite
                    RenderUtil.blockOutline(event.matrices, shape.offset(facing?.offsetX?.toDouble()!!, facing.offsetY.toDouble(), facing.offsetZ.toDouble()), facingColor.getColor().rgb)
                }
                if (aimTarget != null)
                    RenderUtil.blockOutline(event.matrices, VoxelShapes.cuboid(Box.from(aimTarget).offset(-0.5, -0.5, -0.5).expand(-0.45)), aimTargetColor.getColor().rgb)
                if (placeLine != null)
                    RenderUtil.blockOutline(event.matrices, VoxelShapes.union(VoxelShapes.cuboid(Box.from(placeLine?.first).offset(-0.5, -0.5, -0.5).expand(-0.49)), VoxelShapes.cuboid(Box.from(placeLine?.second).offset(-0.5, -0.5, -0.5).expand(-0.49))), placeLineColor.getColor().rgb)
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
        return if (!edgeIncrement.value) edgeDistance.value
        else {
            val newEdgeDist = prevEdgeDistance + edgeIncrementValue.value
            if (newEdgeDist > edgeDistance.value) 0.5 else newEdgeDist
        }
    }
}