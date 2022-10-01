package net.tarasandedevelopment.tarasande.util.player

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ChatScreen
import net.minecraft.client.input.KeyboardInput
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.mob.Monster
import net.minecraft.entity.passive.AnimalEntity
import net.minecraft.entity.passive.TameableEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.util.Hand
import net.minecraft.util.UseAction
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.RaycastContext
import net.minecraft.world.RaycastContext.FluidHandling
import net.minecraft.world.RaycastContext.ShapeType
import org.lwjgl.glfw.GLFW
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.event.EventInput
import net.tarasandedevelopment.tarasande.event.EventIsEntityAttackable
import net.tarasandedevelopment.tarasande.mixin.accessor.IClientPlayerEntity
import net.tarasandedevelopment.tarasande.mixin.accessor.IGameRenderer
import net.tarasandedevelopment.tarasande.mixin.accessor.IMinecraftClient
import net.tarasandedevelopment.tarasande.module.player.ModuleAutoTool
import net.tarasandedevelopment.tarasande.util.math.rotation.Rotation
import net.tarasandedevelopment.tarasande.util.math.rotation.RotationUtil

object PlayerUtil {

    val movementKeys = listOf(MinecraftClient.getInstance().options.forwardKey, MinecraftClient.getInstance().options.leftKey, MinecraftClient.getInstance().options.backKey, MinecraftClient.getInstance().options.rightKey)
    val input = KeyboardInput(MinecraftClient.getInstance().options)

    init {
        TarasandeMain.get().managerEvent.add { event ->
            if (event is EventInput) {
                if (event.input == MinecraftClient.getInstance().player?.input) {
                    input.tick(event.slowDown, event.slowdownAmount)
                }
            }
        }
    }

    fun isPlayerMoving() = MinecraftClient.getInstance().player!!.input.movementInput.lengthSquared() > 0.8f * 0.8f

    fun isAttackable(entity: Entity?): Boolean {
        var attackable = true
        if (entity == null) attackable = false
        else if (entity !is LivingEntity) attackable = false
        else if (entity == MinecraftClient.getInstance().player) attackable = false
        else if (!TarasandeMain.get().clientValues.targets.isSelected(0) && entity is PlayerEntity) attackable = false
        else if (TarasandeMain.get().clientValues.dontAttackTamedEntities.value && entity is TameableEntity && entity.ownerUuid == MinecraftClient.getInstance().player?.uuid) attackable = false
        else if (!TarasandeMain.get().clientValues.targets.isSelected(1) && entity is AnimalEntity) attackable = false
        else if (!TarasandeMain.get().clientValues.targets.isSelected(2) && ((entity is MobEntity || entity is Monster) && entity !is AnimalEntity)) attackable = false
        else if (!TarasandeMain.get().clientValues.targets.isSelected(3) && (entity !is PlayerEntity && entity !is AnimalEntity && entity !is MobEntity)) attackable = false

        val eventIsEntityAttackable = EventIsEntityAttackable(entity, attackable)
        TarasandeMain.get().managerEvent.call(eventIsEntityAttackable)
        return eventIsEntityAttackable.attackable
    }

    fun getTargetedEntity(reach: Double, rotation: Rotation, allowThroughWalls: Boolean = true): HitResult? {
        val gameRenderer = MinecraftClient.getInstance().gameRenderer
        val accessor = (gameRenderer as IGameRenderer)
        val renderTickCounter = (MinecraftClient.getInstance() as IMinecraftClient).tarasande_getRenderTickCounter()

        val prevAllowThroughWalls = accessor.tarasande_isAllowThroughWalls()
        val prevReach = accessor.tarasande_getReach()
        val prevReachExtension = accessor.tarasande_isDisableReachExtension()

        accessor.tarasande_setAllowThroughWalls(allowThroughWalls)
        accessor.tarasande_setReach(reach)
        accessor.tarasande_setDisableReachExtension(true)

        val prevCrosshairTarget = MinecraftClient.getInstance().crosshairTarget
        val prevTargetedEntity = MinecraftClient.getInstance().targetedEntity

        val prevCameraEntity = MinecraftClient.getInstance().cameraEntity
        MinecraftClient.getInstance().cameraEntity = MinecraftClient.getInstance().player // not using setter to avoid shader unloading, this is purely math, no rendering

        val prevYaw = MinecraftClient.getInstance().player!!.yaw
        val prevPitch = MinecraftClient.getInstance().player!!.pitch

        MinecraftClient.getInstance().player!!.yaw = rotation.yaw
        MinecraftClient.getInstance().player!!.pitch = rotation.pitch

        val prevFakeRotation = RotationUtil.fakeRotation
        RotationUtil.fakeRotation = null // prevent rotationvec override by mixin

        val prevTickDelta = renderTickCounter.tickDelta
        renderTickCounter.tickDelta = 1.0f

        gameRenderer.updateTargetedEntity(1.0f)
        val hitResult = MinecraftClient.getInstance().crosshairTarget

        renderTickCounter.tickDelta = prevTickDelta

        RotationUtil.fakeRotation = prevFakeRotation

        MinecraftClient.getInstance().player?.yaw = prevYaw
        MinecraftClient.getInstance().player?.pitch = prevPitch

        MinecraftClient.getInstance().cameraEntity = prevCameraEntity

        MinecraftClient.getInstance().crosshairTarget = prevCrosshairTarget
        MinecraftClient.getInstance().targetedEntity = prevTargetedEntity

        accessor.tarasande_setReach(prevReach)
        accessor.tarasande_setAllowThroughWalls(prevAllowThroughWalls)
        accessor.tarasande_setDisableReachExtension(prevReachExtension)

        return hitResult
    }

    fun rayCast(start: Vec3d, end: Vec3d): BlockHitResult = MinecraftClient.getInstance().world!!.raycast(RaycastContext(start, end, ShapeType.OUTLINE, FluidHandling.NONE, MinecraftClient.getInstance().player!!))

    fun canVectorBeSeen(start: Vec3d, end: Vec3d): Boolean {
        val hitResult = rayCast(start, end)
        return hitResult.type != HitResult.Type.BLOCK
    }

    fun getMoveDirection(): Double {
        return RotationUtil.getYaw(input.movementInput) + (if (input.movementInput.lengthSquared() != 0.0f) 0.0 else 90.0) + MinecraftClient.getInstance().player!!.yaw
    }

    fun isOnEdge(extrapolation: Double) = MinecraftClient.getInstance().player!!.let {
        MinecraftClient.getInstance().world!!.isSpaceEmpty(it, it.boundingBox.offset(it.velocity.x * extrapolation, -it.stepHeight.toDouble(), it.velocity.z * extrapolation))
    }

    fun getUsedHand(): Hand? {
        for (hand in Hand.values()) {
            val stack = MinecraftClient.getInstance().player!!.getStackInHand(hand)
            if (stack.item != Items.AIR) {
                if (stack.useAction == UseAction.NONE)
                    continue

                return hand
            }
        }
        return null
    }

    const val walkSpeed = 0.28
    fun calcBaseSpeed(baseSpeed: Double = walkSpeed): Double {
        return baseSpeed + 0.03 *
                if (MinecraftClient.getInstance().player?.hasStatusEffect(StatusEffects.SPEED)!!)
                    MinecraftClient.getInstance().player?.getStatusEffect(StatusEffects.SPEED)?.amplifier!!
                else
                    0
    }

    fun sendChatMessage(text: String) {
        val prevBypassChat = (MinecraftClient.getInstance().player as IClientPlayerEntity).tarasande_getBypassChat()
        (MinecraftClient.getInstance().player as IClientPlayerEntity).tarasande_setBypassChat(true)
        // this method COULD be static, but Mojangs god tier coders didn't think of that
        object : ChatScreen("") {
            override fun narrateScreenIfNarrationEnabled(onlyChangedNarrations: Boolean) {
            }

            override fun close() {
            }

            override fun removed() {
            }

            override fun sendMessage(chatText: String?, addToHistory: Boolean): Boolean {
                super.sendMessage(chatText, addToHistory)
                return false
            }
        }.also {
            it.init(MinecraftClient.getInstance(), MinecraftClient.getInstance().window.scaledWidth, MinecraftClient.getInstance().window.scaledHeight)
            for (c in text.toCharArray()) it.charTyped(c, 0)
            it.keyPressed(GLFW.GLFW_KEY_ENTER, 0, 0)
        }
        (MinecraftClient.getInstance().player as IClientPlayerEntity).tarasande_setBypassChat(prevBypassChat)
    }

    fun getBreakSpeed(blockPos: BlockPos): Pair<Double, Int> {
        if (!TarasandeMain.get().managerModule.get(ModuleAutoTool::class.java).enabled)
            return MinecraftClient.getInstance().player?.inventory?.selectedSlot!!.let { Pair(getBreakSpeed(blockPos, it), it) }

        val origSlot = MinecraftClient.getInstance().player?.inventory?.selectedSlot ?: return Pair(1.0, -1)
        var bestMult = 1.0
        var bestTool = -1
        for (i in 0..8) {
            val mult = getBreakSpeed(blockPos, i)
            if (bestMult > mult) {
                bestTool = i
                bestMult = mult
            }
        }
        MinecraftClient.getInstance().player?.inventory?.selectedSlot = origSlot
        return Pair(bestMult, bestTool)
    }

    fun getBreakSpeed(blockPos: BlockPos, item: Int): Double {
        val state = MinecraftClient.getInstance().world?.getBlockState(blockPos)
        if (state?.isAir!! || state.getOutlineShape(MinecraftClient.getInstance().world, blockPos).isEmpty) return 1.0
        val hardness = state.getHardness(MinecraftClient.getInstance().world, blockPos)
        if (hardness <= 0.0f) return 1.0
        MinecraftClient.getInstance().player?.inventory?.selectedSlot = item
        var mult = MinecraftClient.getInstance().player?.getBlockBreakingSpeed(state)!!
        if (!MinecraftClient.getInstance().player?.isOnGround!!) {
            mult *= 5.0f // bruh
        }
        return 1.0 - mult / hardness / 30.0
    }
}