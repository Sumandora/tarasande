package su.mandora.tarasande.module.combat

import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ShieldItem
import net.minecraft.item.SwordItem
import net.minecraft.util.Hand
import net.minecraft.util.UseAction
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.Box
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventKeyBindingIsPressed
import su.mandora.tarasande.event.EventPollEvents
import su.mandora.tarasande.event.EventUpdate
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
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import java.util.function.Consumer
import kotlin.Comparator
import kotlin.collections.ArrayList
import kotlin.math.sqrt


class ModuleKillAura : Module("Kill aura", "Automatically attacks near players", ModuleCategory.COMBAT) {

	private val mode = ValueMode(this, "Mode", false, "Single", "Multi")
	private val priority = ValueMode(this, "Priority", false, "Distance", "Health", "Hurt Time", "Damage", "FOV")
	private val fov = ValueNumber(this, "FOV", 0.0, 255.0, 255.0, 1.0)
	private val reach = ValueNumberRange(this, "Reach", 0.1, 3.0, 4.0, 6.0, 0.1)
	private val clickSpeedUtil = ClickSpeedUtil(this, { true }) // for setting order
	private val rayTrace = ValueBoolean(this, "Ray trace", false)
	private val simulateMouseDelay = object : ValueBoolean(this, "Simulate mouse delay", false) {
		override fun isVisible() = rayTrace.value && !mode.isSelected(1)
	}
	private val swingInAir = ValueBoolean(this, "Swing in air", true)
	private val aimSpeed = ValueNumberRange(this, "Aim speed", 0.0, 1.0, 1.0, 1.0, 0.1)
	private val dontAttackWhenBlocking = ValueBoolean(this, "Don't attack when blocking", false)
	private val throughWalls = ValueBoolean(this, "Through walls", false)
	private val attackCooldown = ValueBoolean(this, "Attack cooldown", false)
	private val blockMode = object : ValueMode(this, "Auto block", false, "Disabled", "Permanent", "Legit") {
		override fun onChange() {
			blocking = false
		}
	}
	private val needUnblock = object : ValueBoolean(this, "Need unblock", true) {
		override fun isVisible() = blockMode.isSelected(1)
	}
	private val blockCheckMode = object : ValueMode(this, "Auto block check", false, "Shield", "Sword") {
		override fun isVisible() = !blockMode.isSelected(0)
	}
	private val blockOutOfReach = object : ValueBoolean(this, "Block out of reach", true) {
		override fun isVisible() = !blockMode.isSelected(0)
	}
	private val guaranteeHit = ValueBoolean(this, "Guarantee hit", false)
	private val rotations = ValueMode(this, "Rotations", true, "Around walls", "Randomized")
	private val precision = object : ValueNumber(this, "Precision", 0.0, 0.01, 1.0, 0.01) {
		override fun isVisible() = rotations.anySelected()
	}
	private val silent = ValueBoolean(this, "Silent", true)
	private val flex = ValueBoolean(this, "Flex", false)
	private val flexTurn = object : ValueNumber(this, "Flex turn", 0.0, 90.0, 180.0, 1.0) {
		override fun isVisible() = flex.value
	}
	private val flexHurtTime = object : ValueNumber(this, "Flex hurt time", 0.0, 0.5, 1.0, 0.1) {
		override fun isVisible() = flex.value
	}
	private val syncPosition = ValueBoolean(this, "Sync position", false)

	private val targets = ArrayList<Pair<Entity, Vec3d>>()
	private val comparator: Comparator<Pair<Entity, Vec3d>> = Comparator.comparing {
		when {
			priority.isSelected(0) -> {
				if (it.first is LivingEntity)
					mc.player?.eyePos!!.squaredDistanceTo(MathUtil.closestPointToBox(mc.player?.eyePos!!, it.first.boundingBox.expand(it.first.targetingMargin.toDouble())))
				else
					mc.player?.eyePos!!.squaredDistanceTo(MathUtil.closestPointToBox(mc.player?.eyePos!!, it.first.boundingBox.expand(it.first.targetingMargin.toDouble())))
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
				if (it.first is LivingEntity)
					PlayerUtil.simulateAttack(it.first as LivingEntity, false)
				else
					0
			}
			priority.isSelected(4) -> {
				RotationUtil.getRotations(mc.player?.eyePos!!, getBestAimPoint(it.first.boundingBox)).fov(Rotation(mc.player?.yaw!!, mc.player?.pitch!!))
			}
			else -> 0.0
		}.toDouble()
	}

	private var blocking = false
	private var waitForHit = false

	override fun onEnable() {
		clickSpeedUtil.reset()
	}

	override fun onDisable() {
		blocking = false
	}

	val eventConsumer = Consumer<Event> { event ->
		when (event) {
			is EventPollEvents -> {
				targets.clear()
				for (entity in mc.world?.entities!!) {
					if (!PlayerUtil.isAttackable(entity))
						continue
					val entity = entity as LivingEntity

					var boundingBox = entity.boundingBox.expand(entity.targetingMargin.toDouble())
					if (syncPosition.value) {
						val accessor = entity as ILivingEntity
						boundingBox = boundingBox.offset(accessor.serverX - entity.x, accessor.serverY - entity.y, accessor.serverZ - entity.z)
					}
					val bestAimPoint = getBestAimPoint(boundingBox)
					if (bestAimPoint.squaredDistanceTo(mc.player?.eyePos!!) > reach.maxValue * reach.maxValue)
						continue
					if (RotationUtil.getRotations(mc.player?.eyePos!!, bestAimPoint).fov(Rotation(mc.player?.yaw!!, mc.player?.pitch!!)) > fov.value)
						continue
					// aim point calculation maybe slower, only run it if the range check is actually able to succeed under best conditions
					val aimPoint = getAimPoint(boundingBox, entity) ?: continue
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
				targets.sortBy {
					val hitResult = PlayerUtil.getTargetedEntity(reach.minValue, RotationUtil.getRotations(mc.player?.eyePos!!, it.second))
					hitResult != null && hitResult is EntityHitResult && hitResult.entity != null
				}

				val target = targets[0]

				val currentRot = if (RotationUtil.fakeRotation != null) Rotation(RotationUtil.fakeRotation!!) else Rotation(mc.player?.yaw!!, mc.player?.pitch!!)
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

				if (!silent.value) {
					mc.player?.yaw = finalRot.yaw
					mc.player?.pitch = finalRot.pitch
				}

				event.minRotateToOriginSpeed = aimSpeed.minValue
				event.maxRotateToOriginSpeed = aimSpeed.maxValue
			}
			is EventUpdate -> {
				if (event.state != EventUpdate.State.PRE)
					return@Consumer

				if (targets.isEmpty()) {
					clickSpeedUtil.reset()
					waitForHit = false
					return@Consumer
				}

				if (attackCooldown.value && (mc as IMinecraftClient).attackCooldown > 0)
					return@Consumer

				TarasandeMain.get().log.println(MinecraftClient.getInstance().player?.getAttackCooldownProgress(0.5F)!!)
				val clicks = clickSpeedUtil.getClicks()

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
								continue

							if (rayTrace.value) {
								if (RotationUtil.fakeRotation == null) {
									continue
								} else {
									val hitResult = PlayerUtil.getTargetedEntity(reach.minValue, if (!mode.isSelected(1)) (if (simulateMouseDelay.value) Rotation((mc.player as IClientPlayerEntity).lastYaw, (mc.player as IClientPlayerEntity).lastPitch) else RotationUtil.fakeRotation!!) else RotationUtil.getRotations(mc.player?.eyePos!!, aimPoint))
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
							waitForHit = false
							attacked = true

							if (!mode.isSelected(1))
								break
						}
						if (!attacked)
							if (swingInAir.value) {
								mc.player?.swingHand(Hand.MAIN_HAND)
								if (mc.interactionManager?.hasLimitedAttackSpeed()!!) {
									(mc as IMinecraftClient).attackCooldown = 10
								}
							}
					}
				}
				if (targets.isNotEmpty() && (!waitForHit) && !mc.player?.isUsingItem!! && !blockMode.isSelected(0)) {
					var canBlock = true
					if (blockCheckMode.isSelected(0) && (mc.player?.getStackInHand(Hand.OFF_HAND)?.item !is ShieldItem || mc.player?.getStackInHand(Hand.MAIN_HAND)?.useAction != UseAction.NONE))
						canBlock = false
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
							(mc.options.keyUse as IKeyBinding).setTimesPressed(1)
						}
					}
				}
			}
			is EventKeyBindingIsPressed -> {
				if (event.keyBinding == mc.options.keyUse)
					if (blocking && targets.isNotEmpty())
						event.pressed = true
			}
		}
	}

	private fun attack(entity: Entity) {
		val original = mc.crosshairTarget
		mc.crosshairTarget = EntityHitResult(entity)
		if (!attackCooldown.value) {
			(mc as IMinecraftClient).attackCooldown = 0
		}
		(mc as IMinecraftClient).invokeDoAttack()
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
								val distSquared = mc.player?.eyePos!!.squaredDistanceTo(vector)
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
			var aimPoint = best!!.add(0.0, 0.0, 0.0) /* copy */

			// Humans always try to get to the middle
			val dist = MathUtil.getBias(mc.player?.eyePos!!.squaredDistanceTo(aimPoint) / (reach.maxValue * reach.maxValue) * (reach.minValue / reach.maxValue), 0.45) // I have no idea why this works and looks like it does, but it's good and why remove it then
			aimPoint = aimPoint.add(
				(box.center.x - aimPoint.x) * (1 - dist),
				(box.center.y - aimPoint.y) * (1 - dist) * 0.3 /* Humans dislike aiming up and down */,
				(box.center.z - aimPoint.z) * (1 - dist)
			)

			// Humans can't hold their hands still
			if (mc.player?.velocity?.lengthSquared()!! > 0.0 || (entity.prevX != entity.x || entity.prevY != entity.y || entity.prevZ != entity.z)) { // either the target or the player has to move otherwise changing the rotation doesn't make sense
//				aimPoint = aimPoint.add(
//					sin(System.currentTimeMillis() / 150.0) * 0.1,
//					cos(System.currentTimeMillis() / 150.0) * 0.1,
//					sin(System.currentTimeMillis() / 150.0) * 0.1
//				)
				// TODO Add Noise
			}

			// Human aim is slow
			aimPoint = aimPoint.subtract(
				(entity.prevX - entity.x) + mc.player?.velocity!!.x,
				(entity.prevY - entity.y) + mc.player?.velocity!!.y,
				(entity.prevZ - entity.z) + mc.player?.velocity!!.z
			)

			// Don't aim through walls
			while (!PlayerUtil.canVectorBeSeen(mc.player?.eyePos!!, aimPoint) && rotations.isSelected(0)) {
				aimPoint = Vec3d(
					MathUtil.bringCloser(aimPoint.x, best.x, precision.value),
					MathUtil.bringCloser(aimPoint.y, best.y, precision.value),
					MathUtil.bringCloser(aimPoint.z, best.z, precision.value)
				)
			}

			var distToBest = mc.player?.eyePos!!.squaredDistanceTo(best)
			if (distToBest <= reach.minValue * reach.minValue) {
				while (mc.player?.eyePos!!.squaredDistanceTo(aimPoint) > reach.minValue * reach.minValue)
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