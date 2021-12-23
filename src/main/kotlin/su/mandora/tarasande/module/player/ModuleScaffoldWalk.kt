package su.mandora.tarasande.module.player

import net.minecraft.item.BlockItem
import net.minecraft.util.Hand
import net.minecraft.util.UseAction
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventJump
import su.mandora.tarasande.event.EventPollEvents
import su.mandora.tarasande.event.EventUpdate
import su.mandora.tarasande.mixin.accessor.IEntity
import su.mandora.tarasande.mixin.accessor.IMinecraftClient
import su.mandora.tarasande.util.math.TimeUtil
import su.mandora.tarasande.util.math.rotation.Rotation
import su.mandora.tarasande.util.math.rotation.RotationUtil
import su.mandora.tarasande.util.player.PlayerUtil
import su.mandora.tarasande.util.player.clickspeed.ClickMethodCooldown
import su.mandora.tarasande.util.player.clickspeed.ClickSpeedUtil
import su.mandora.tarasande.util.render.RenderUtil
import su.mandora.tarasande.value.ValueBoolean
import su.mandora.tarasande.value.ValueMode
import su.mandora.tarasande.value.ValueNumber
import su.mandora.tarasande.value.ValueNumberRange
import java.util.concurrent.ThreadLocalRandom
import java.util.function.Consumer
import kotlin.math.abs
import kotlin.math.round

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
		override fun isVisible() = edgeIncrement.value
	}
	private val preventImpossibleEdge = object : ValueBoolean(this, "Prevent impossible edge", true) {
		override fun isVisible() = edgeIncrement.value
	}
	private val rotateAtEdge = ValueBoolean(this, "Rotate at edge", false)
	private val silent = ValueBoolean(this, "Silent", false)
	private val lockView = ValueBoolean(this, "Lock view", false)
	private val sneak = ValueBoolean(this, "Sneak", false)
	private val headRoll = ValueMode(this, "Head roll", false, "Disabled", "Advantage", "Autism")

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
			// diagonals
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
		mc.options?.keySneak?.isPressed = false
	}

	private fun getAdjacentBlock(blockPos: BlockPos): Pair<BlockPos, Direction>? {
		val arrayList = ArrayList<Pair<BlockPos, Direction>>()
		for (target in targets) {
			val adjacent = blockPos.add(target.first.x, target.first.y, target.first.z)
			if (!mc.world?.isAir(adjacent)!!)
				arrayList.add(Pair(adjacent, target.second))
		}
		var best: Pair<BlockPos, Direction>? = null
		var dist = 0.0
		for(target in arrayList) {
			val dist2 = Vec3d.ofCenter(target.first).squaredDistanceTo(mc.player?.pos)
			if(best == null || dist2 < dist) {
				best = target
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
							lastRotation = Rotation(mc.player?.yaw!!, lastRotation!!.pitch)
						}
						headRoll.isSelected(2) -> {
							lastRotation = Rotation(mc.player?.yaw!! * mc.player?.age!! * 45, lastRotation!!.pitch)
						}
					}
				}

				val below = mc.player?.blockPos!!.add(0, -1, 0)
				if (mc.world?.isAir(below)!!) {
					target = getAdjacentBlock(below)
					if (target != null) {
						val currentRot = if (RotationUtil.fakeRotation != null) Rotation(RotationUtil.fakeRotation!!) else Rotation(mc.player?.yaw!!, mc.player?.pitch!!)
						val newEdgeDist = getNewEdgeDist()
						if (!rotateAtEdge.value || (round(Vec3d.ofCenter(target!!.first).subtract(mc.player?.pos!!).multiply(Vec3d.of(target!!.second.vector)).horizontalLengthSquared() * 100) / 100.0 in (newEdgeDist * newEdgeDist)..1.0 || target!!.second.vector.y != 0)) {
							var point = Vec3d.of(target!!.first)
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
										val rotationVector = (mc.player as IEntity).invokeGetRotationVector(rot.pitch, rot.yaw)
										val hitResult = PlayerUtil.raycast(mc.player?.eyePos!!, mc.player?.eyePos!!.add(rotationVector.multiply(mc.interactionManager?.reachDistance!!.toDouble())))
										if (hitResult != null && hitResult.type == HitResult.Type.BLOCK && (target == null || (hitResult.side == (if (target!!.second.vector.y != 0) target!!.second else target!!.second.opposite) && hitResult.blockPos == target!!.first))) {
											val dir = target!!.second.opposite
											val delta = abs(
												MathHelper.wrapDegrees
													(
													(if (offsetGoalYaw.value)
																mc.player?.yaw!!.toDouble()
															else
																RotationUtil.getYaw(
																	Vec3d.of(
																		if (dir.vector.y == 0)
																			dir.vector
																		else
																			Direction.fromRotation(
																				RotationUtil.getYaw(
																					mc.player?.velocity?.x!!,
																					mc.player?.velocity?.z!!
																				)
																			).vector
																	)
																)
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
							val targetRot = RotationUtil.getRotations(mc.player?.eyePos!!, point)
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

							lastRotation = smoothedRot
						}
					} else {
						prevEdgeDistance = 0.5
						clickSpeedUtil.reset()
					}
				}
				if (lastRotation != null) {
					event.rotation = lastRotation!!.correctSensitivity()

					if (lockView.value) {
						mc.player?.yaw = event.rotation.yaw
						mc.player?.pitch = event.rotation.pitch
					}
				}
				event.minRotateToOriginSpeed = aimSpeed.minValue
				event.maxRotateToOriginSpeed = aimSpeed.maxValue
			}
			is EventUpdate -> {
				if (event.state != EventUpdate.State.PRE)
					return@Consumer

				if(!timeUtil.hasReached(delay.value.toLong()))
					return@Consumer

				if (target != null && RotationUtil.fakeRotation != null) {
					val airBelow = mc.world?.isAir(BlockPos(mc.player?.pos!!.add(0.0, -1.0, 0.0)))!!
					if (sneak.value)
						mc.options?.keySneak?.isPressed = airBelow
					else
						mc.options?.keySneak?.isPressed = false
					if (airBelow || alwaysClick.value) {
						val newEdgeDist = getNewEdgeDist()
						val rotationVector = (mc.player as IEntity).invokeGetRotationVector(RotationUtil.fakeRotation!!.pitch, RotationUtil.fakeRotation!!.yaw)
						val hitResult = PlayerUtil.raycast(mc.player?.eyePos!!, mc.player?.eyePos!!.add(rotationVector.multiply(mc.interactionManager?.reachDistance!!.toDouble())))
						val clicks = clickSpeedUtil.getClicks()
						val prevSlot = mc.player?.inventory?.selectedSlot
						if (silent.value) {
							var hasBlock = false
							for (hand in Hand.values().reversed()) {
								val stack = mc.player?.getStackInHand(hand)
								if (stack != null) {
									if (stack.item is BlockItem && isBlockItemValid(stack.item as BlockItem)) {
										hasBlock = true
									} else if(stack.item.getUseAction(stack) != UseAction.NONE) {
										break
									}
								}
							}
							if (!hasBlock) {
								var blockSlot: Int? = null
								for (slot in 0..8) {
									val stack = mc.player?.inventory?.main?.get(slot)
									if (stack != null && stack.item is BlockItem && isBlockItemValid(stack.item as BlockItem)) {
										blockSlot = slot
										break
									}
								}
								if (blockSlot != null) {
									mc.player?.inventory?.selectedSlot = blockSlot
								}
							}
						}
						if (airBelow && (round(Vec3d.ofCenter(target!!.first).subtract(mc.player?.pos!!).multiply(Vec3d.of(target!!.second.vector)).horizontalLengthSquared() * 100) / 100.0 in (newEdgeDist * newEdgeDist)..1.0 || target!!.second.vector.y != 0)) {
							for (hand in Hand.values()) {
								val stack = mc.player?.getStackInHand(hand)
								if (stack != null && stack.item is BlockItem) {
									if (hitResult != null && hitResult.type == HitResult.Type.BLOCK) {
										placeBlock(hitResult)
										timeUtil.reset()

										if(target!!.second.vector.y == 0) {
											if (edgeIncrement.value && preventImpossibleEdge.value && prevEdgeDistance > newEdgeDist && mc.player?.isOnGround!!)
												mc.player?.jump()
											prevEdgeDistance = newEdgeDist
										}
									}
								}
							}
						} else if(alwaysClick.value) {
							if(hitResult != null && (hitResult.side != target!!.second || hitResult.type != HitResult.Type.BLOCK)) {
								for(i in 1..clicks)
									placeBlock(hitResult)
							}
						}
						if (silent.value) {
							mc.player?.inventory?.selectedSlot = prevSlot
						}
					}
				}
			}
			is EventJump -> {
				prevEdgeDistance = 0.5
			}
		}
	}

	private fun isBlockItemValid(blockItem: BlockItem): Boolean {
		val block = blockItem.block
		val shape = block.defaultState.getCollisionShape(mc.world, BlockPos.ORIGIN)
		return !shape.isEmpty && shape.boundingBox.xLength == 1.0 && shape.boundingBox.yLength == 1.0 && shape.boundingBox.zLength == 1.0 && block.defaultState.material.isSolid
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