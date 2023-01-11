package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.player

import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.util.Hand
import net.minecraft.util.UseAction
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.*
import net.tarasandedevelopment.tarasande.event.*
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.*
import net.tarasandedevelopment.tarasande.system.feature.clickmethodsystem.api.ClickSpeedUtil
import net.tarasandedevelopment.tarasande.system.feature.clickmethodsystem.impl.ClickMethodCooldown
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory
import net.tarasandedevelopment.tarasande.util.extension.mc
import net.tarasandedevelopment.tarasande.util.extension.minecraft.*
import net.tarasandedevelopment.tarasande.util.math.MathUtil
import net.tarasandedevelopment.tarasande.util.math.TimeUtil
import net.tarasandedevelopment.tarasande.util.math.rotation.Rotation
import net.tarasandedevelopment.tarasande.util.math.rotation.RotationUtil
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import kotlin.math.*


class ModuleScaffoldWalk : Module("Scaffold walk", "Places blocks underneath you", ModuleCategory.PLAYER) {

    private val delay = ValueNumber(this, "Delay", 0.0, 0.0, 500.0, 50.0)
    private val alwaysClick = ValueBoolean(this, "Always click", false)
    private val clickSpeedUtil = ClickSpeedUtil(this, { alwaysClick.value }, ClickMethodCooldown::class.java)
    private val aimSpeed = ValueNumberRange(this, "Aim speed", 0.1, 1.0, 1.0, 1.0, 0.1)

    private val goalYaw = ValueNumber(this, "Goal yaw", 0.0, 0.0, 60.0, 1.0)
    private val offsetGoalYaw = ValueBoolean(this, "Offset goal yaw", true)
    private val edgeDistance = ValueNumber(this, "Edge distance", 0.0, 0.5, 1.0, 0.05)
    private val edgeIncrement = ValueBoolean(this, "Edge increment", true)
    private val increment = object : ValueNumber(this, "Increment", 0.0, 0.15, 0.5, 0.05) {
        override fun isEnabled() = edgeIncrement.value
    }
    private val preventImpossibleEdge = object : ValueBoolean(this, "Prevent impossible edge", true) {
        override fun isEnabled() = edgeIncrement.value
    }
    private val rotateAtEdgeMode = ValueMode(this, "Rotate at edge mode", false, "Off", "Distance", "Extrapolated position")
    private val distance = object : ValueNumber(this, "Distance", 0.0, 0.5, 1.0, 0.05) {
        override fun isEnabled() = rotateAtEdgeMode.isEnabled() && rotateAtEdgeMode.isSelected(1)
    }
    private val extrapolation = object : ValueNumber(this, "Extrapolation", 0.0, 1.0, 10.0, 1.0) {
        override fun isEnabled() = rotateAtEdgeMode.isEnabled() && rotateAtEdgeMode.isSelected(2)
    }
    private val preventRerotation = ValueBoolean(this, "Prevent re-rotation", false)
    private val rerotateOnFacingChange = object : ValueBoolean(this, "Re-rotate on facing change", false) {
        override fun isEnabled() = preventRerotation.value
    }
    private val aimHeight = ValueNumberRange(this, "Aim height", 0.0, 0.4, 0.6, 1.0, 0.05)
    private val speculativeWaiting = ValueBoolean(this, "Speculative waiting", false)
    private val silent = ValueMode(this, "Silent", false, "Disabled", "Invisible", "Visible")
    private val lockView = ValueBoolean(this, "Lock view", false)
    private val headRoll = ValueBoolean(this, "Head roll", false)
    private val forbiddenItems = object : ValueRegistry<Item>(this, "Forbidden items", Registries.ITEM) {
        override fun filter(key: Item) = key is BlockItem
        override fun getTranslationKey(key: Any?) = (key as Item).translationKey
    }
    private val cubeShape = ValueBoolean(this, "Cube shape", true)
    private val tower = ValueMode(this, "Tower", false, "Vanilla", "Motion", "Teleport")
    private val blockColor = ValueColor(this, "Block color", 0.0, 1.0, 1.0, 1.0)
    private val facingColor = ValueColor(this, "Facing color", 0.0, 1.0, 1.0, 1.0)
    private val aimTargetColorRerotated = ValueColor(this, "Aim target color (rerotated)", 0.0, 1.0, 1.0, 1.0)
    private val aimTargetColorStable = ValueColor(this, "Aim target color (stable)", 0.0, 1.0, 1.0, 1.0)
    private val placeLineColor = ValueColor(this, "Place line color", 0.0, 1.0, 1.0, 1.0)

    private val targets = ArrayList<Pair<BlockPos, Direction>>()
    private val timeUtil = TimeUtil()

    private var target: Pair<BlockPos, Direction>? = null
    private var placeLine: Pair<Vec3d, Vec3d>? = null
    private var aimTarget: Vec3d? = null
    private var rotation: Rotation? = null
    private var prevEdgeDistance = 0.5
    private var preferredSide: Int? = null
    private var rerotated = false
    private var preferredSlot: Int? = null

    init {
        // up & down (down should never happen unless we are trying to save ourselves)
        targets.add(Pair(BlockPos(0, -1, 0), Direction.UP))

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
        preferredSlot = mc.player?.inventory?.selectedSlot
        clickSpeedUtil.reset()
    }

    private fun placeBlock(blockHitResult: BlockHitResult) {
        val original = mc.crosshairTarget
        mc.crosshairTarget = blockHitResult
        mc.doItemUse()
        mc.crosshairTarget = original
    }

    override fun onDisable() {
        if(silent.isSelected(2) && preferredSlot != null)
            mc.player?.inventory?.selectedSlot = preferredSlot
        target = null
        aimTarget = null
        placeLine = null
        rotation = null
        preferredSide = null
        rerotated = false
        preferredSlot = null
        prevEdgeDistance = 0.5
    }

    private fun getAdjacentBlock(blockPos: BlockPos): Pair<BlockPos, Direction>? {
        val arrayList = ArrayList<Triple<BlockPos, BlockPos, Direction>>()
        for (target in targets) {
            val adjacent = blockPos.add(target.first.x, target.first.y, target.first.z)
            if (!mc.world?.isAir(adjacent)!! && mc.world?.isAir(adjacent.add(target.second.hitResultSide().vector))!!)
                arrayList.add(Triple(target.first, adjacent, target.second))
        }
        var best: Pair<BlockPos, Direction>? = null
        var dist = 0.0
        for (target in arrayList) {
            if (target.third.offsetY != 0) {
                if (PlayerUtil.input.jumping)
                    return Pair(target.second, target.third)
                else if (best == null)
                    continue
            }
            val dist2 = mc.player?.pos?.squaredDistanceTo(Vec3d.ofCenter(target.second) - Vec3d.of(target.first) * 0.5)!!
            if (best == null || dist2 < dist) {
                best = Pair(target.second, target.third)
                dist = dist2
            }
        }
        return best
    }

    init {
        registerEvent(EventPollEvents::class.java, 1001) { event ->
            if (rotation != null && headRoll.value) {
                rotation = Rotation(mc.player?.yaw!!, rotation?.pitch!!)
            }

            val below = mc.player?.blockPos?.add(0, -1, 0)!!
            val currentRot = if (RotationUtil.fakeRotation != null) Rotation(RotationUtil.fakeRotation!!) else Rotation(mc.player!!)
            if (mc.world?.isAir(below)!!) {
                val prevTarget = target
                target = getAdjacentBlock(below)
                if(speculativeWaiting.value && target?.second?.offsetY == 0 && target?.second?.opposite == prevTarget?.second)
                    target = null
                if (target != null) {
                    if (when {
                            rotateAtEdgeMode.isSelected(0) -> true
                            rotateAtEdgeMode.isSelected(1) -> ((Vec3d.ofCenter(target?.first) - mc.player?.pos!!) * Vec3d.of(target?.second?.vector)).horizontalLengthSquared() >= distance.value * distance.value || target?.second?.offsetY != 0
                            rotateAtEdgeMode.isSelected(2) -> PlayerUtil.isOnEdge(extrapolation.value)
                            else -> false
                    }) {
                        if (rotation == null || run {
                                if (!preventRerotation.value)
                                    true
                                else if (rerotateOnFacingChange.value && (prevTarget != null && prevTarget.second != target?.second)) {
                                    preferredSide = null
                                    true
                                } else {
                                    val rotationVector = rotation?.forwardVector(mc.interactionManager?.reachDistance?.toDouble()!!)!!
                                    val hitResult = PlayerUtil.rayCast(mc.player?.eyePos!!, mc.player?.eyePos!! + rotationVector)
                                    !hitResult.isSame(target?.second!!, target?.first!!)
                                }
                            }) {

                            val dirVec = target?.second?.vector!!
                            var point = Vec3d.ofCenter(target?.first) + Vec3d.of(dirVec) * -0.5
                            val eye = mc.player?.eyePos!!

                            val blockPos = target?.first
                            val blockState = mc.world?.getBlockState(target?.first)
                            val shape = blockState?.getOutlineShape(mc.world, blockPos)!!

                            val padding = 0.01

                            val finalPoint = if (target?.second?.offsetY != 0) {
                                placeLine = null
                                if (shape.isEmpty)
                                    point
                                else {
                                    val positionShape = shape.offset(blockPos?.x?.toDouble()!!, blockPos.y.toDouble(), blockPos.z.toDouble()).boundingBox.expand(-padding)
                                    val lastLook = aimTarget ?: eye

                                    MathUtil.closestPointToBox(lastLook, positionShape)
                                }
                            } else {
                                val randomizedAimHeight = aimHeight.randomNumber()
                                val absoluteAimHeight = if (shape.isEmpty) randomizedAimHeight else shape.boundingBox.let { it.minY + (it.maxY - it.minY) * randomizedAimHeight }
                                point = point.add(0.0, MathHelper.clamp(absoluteAimHeight, 0.0, 1.0) - 0.5, 0.0)

                                val rotatedVec = dirVec.let { Vec3d(abs(it.x) - 1.0, 0.0, abs(it.z) - 1.0) }

                                val sideBegin = point + rotatedVec * 0.5
                                val sideEnd = point + rotatedVec * -0.5

                                placeLine = Pair(sideBegin, sideEnd)

                                // calculating the closest point on the line to feet
                                var goalAim = eye

                                if (offsetGoalYaw.value) {
                                    val targetRot = mc.player?.yaw!! - 180
                                    goalAim = intersection( // adjust our target
                                        Vec2f(sideBegin.x.toFloat(), sideBegin.z.toFloat()), Vec2f(sideEnd.x.toFloat(), sideEnd.z.toFloat()),
                                        Vec2f(eye.x.toFloat(), eye.z.toFloat()), (eye + Rotation(targetRot, 0.0F).forwardVector(mc.interactionManager?.reachDistance?.toDouble()!!)).let { Vec2f(it.x.toFloat(), it.z.toFloat()) }
                                    ).let { Vec3d(it.x.toDouble(), point.y, it.y.toDouble()) }
                                }

                                val beginToPoint = Vec2f((goalAim.x - sideBegin.x).toFloat(), (goalAim.z - sideBegin.z).toFloat())
                                val beginToEnd = Vec2f((sideEnd.x - sideBegin.x).toFloat(), (sideEnd.z - sideBegin.z).toFloat())

                                val squaredDist = beginToEnd.lengthSquared()
                                val dotProd = beginToPoint.dot(beginToEnd)

                                var t = dotProd / squaredDist
                                val prevT = t

                                val closest = sideBegin + (sideEnd - sideBegin) * MathHelper.clamp(t.toDouble(), padding, 1.0 - padding)

                                if (t in padding..1.0F - padding) {
                                    val dist = (eye - closest).horizontalLength()
                                    val a = sin(Math.toRadians(-goalYaw.value * (60 / 45f /* those are triangles bitch */))) * dist
                                    if (preferredSide == null) {
                                        val solutions = listOf(t + a.toFloat(), t - a.toFloat())
                                        val bestSolution = solutions.minBy { abs(it - 0.5) }
                                        t = bestSolution

                                        preferredSide =
                                            when {
                                                prevT > t -> 1
                                                prevT < t -> -1
                                                else -> null
                                            }

                                    } else {
                                        t += (a * preferredSide!!).toFloat()
                                    }
                                }

                                sideBegin + (sideEnd - sideBegin) * MathHelper.clamp(t.toDouble(), padding, 1.0F - padding)
                            }

                            aimTarget = finalPoint
                            rerotated = true

                            rotation = RotationUtil.getRotations(eye, finalPoint)
                        }
                    }
                } else {
                    prevEdgeDistance = 0.5
                    clickSpeedUtil.reset()
                }
            }
            if (rotation == null) {
                val rad = Math.toRadians(((PlayerUtil.getMoveDirection()) / 45.0).roundToInt() * 45.0) - PI / 2
                var targetRot = RotationUtil.getRotations(mc.player?.eyePos!!, mc.player?.pos!! + Vec3d(cos(rad), 0.0, sin(rad)) * 0.3)

                val diagonal = abs(round(mc.player?.yaw!! / 90) * 90 - mc.player?.yaw!!) > 22.5
                if (!diagonal) {
                    val deltaRot = (-goalYaw.value * (45.0 / 60.0)).toFloat() // those are not triangles anymore :c
                    val centerRot = RotationUtil.getRotations(mc.player?.eyePos!!, Vec3d.ofBottomCenter(mc.player?.blockPos))
                    val rot = Rotation(targetRot)
                    val firstSolution = rot.withYaw(rot.yaw + deltaRot)
                    val firstFov = centerRot.fov(firstSolution)
                    val secondSolution = rot.withYaw(rot.yaw - deltaRot)
                    val secondFov = centerRot.fov(secondSolution)

                    targetRot = targetRot.withYaw(
                        if (firstFov < secondFov)
                            targetRot.yaw + deltaRot
                        else
                            targetRot.yaw - deltaRot
                    )
                }

                rotation = targetRot
            }

            event.rotation = currentRot.smoothedTurn(rotation!!, aimSpeed).correctSensitivity(preference = {
                if(target == null)
                    return@correctSensitivity true
                val rotationVector = it.forwardVector(mc.interactionManager?.reachDistance?.toDouble()!!)
                val hitResult = PlayerUtil.rayCast(mc.player?.eyePos!!, mc.player?.eyePos!! + rotationVector)
                hitResult.isSame(target?.second!!, target?.first!!)
            })

            if (lockView.value) {
                mc.player?.yaw = event.rotation.yaw
                mc.player?.pitch = event.rotation.pitch
            }
        }

        registerEvent(EventAttack::class.java, 1001) { event ->
            if (target == null || RotationUtil.fakeRotation == null || event.dirty) {
                clickSpeedUtil.reset()
                return@registerEvent
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
                    if (!silent.isSelected(0)) {
                        val blockSlot = PlayerUtil.findSlot { it.value.item is BlockItem && isBlockItemValid(it.value.item as BlockItem) }
                        if (blockSlot != null) {
                            mc.player?.inventory?.selectedSlot = blockSlot
                        } else return@registerEvent
                    } else return@registerEvent
                }
                val rotationVector = RotationUtil.fakeRotation?.forwardVector(mc.interactionManager?.reachDistance?.toDouble()!!)!!
                val hitResult = PlayerUtil.rayCast(mc.player?.eyePos!!, mc.player?.eyePos!! + rotationVector)
                if (hitResult.isSame(target?.second!!, target?.first!!)) {
                    if (airBelow && (((Vec3d.ofCenter(target?.first) - mc.player?.pos!!) * Vec3d.of(target?.second?.vector)).horizontalLengthSquared() >= newEdgeDist * newEdgeDist || target?.first?.y!! < (mc.player?.blockPos?.y!! - 1))) {
                        if (timeUtil.hasReached(delay.value.toLong())) {
                            aimTarget = hitResult.pos
                            rerotated = false
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
                if (silent.isSelected(1)) {
                    mc.player?.inventory?.selectedSlot = prevSlot
                }
            }
        }

        registerEvent(EventMovement::class.java) { event ->
            if (event.entity != mc.player)
                return@registerEvent
            if (target == null)
                return@registerEvent
            if (!PlayerUtil.input.jumping)
                return@registerEvent

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

        registerEvent(EventJump::class.java) { event ->
            if (event.state != EventJump.State.PRE) return@registerEvent
            prevEdgeDistance = 0.5
        }

        registerEvent(EventRender3D::class.java) { event ->
            if (target != null) {
                val blockPos = target?.first
                val blockState = mc.world?.getBlockState(target?.first)
                val shape = blockState?.getOutlineShape(mc.world, blockPos)?.offset(blockPos?.x?.toDouble()!!, blockPos.y.toDouble(), blockPos.z.toDouble())?.neverEmpty()!!
                RenderUtil.blockOutline(event.matrices, shape.boundingBox, blockColor.getColor().rgb)
                val facing = target?.second?.opposite
                RenderUtil.blockOutline(event.matrices, shape.offset(facing?.offsetX?.toDouble()!!, facing.offsetY.toDouble(), facing.offsetZ.toDouble()).boundingBox, facingColor.getColor().rgb)
            }
            if (aimTarget != null)
                RenderUtil.blockOutline(event.matrices, Box.from(aimTarget).offset(-0.5, -0.5, -0.5).expand(-0.45), (if(rerotated) aimTargetColorRerotated else aimTargetColorStable).getColor().rgb)
            if (placeLine != null)
                RenderUtil.blockOutline(event.matrices, Box.from(placeLine?.first).offset(-0.5, -0.5, -0.5).expand(-0.49).union(Box.from(placeLine?.second).offset(-0.5, -0.5, -0.5).expand(-0.49)), placeLineColor.getColor().rgb)
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
            val newEdgeDist = prevEdgeDistance + increment.value
            if (newEdgeDist > edgeDistance.value) 0.5 else newEdgeDist
        }
    }
}