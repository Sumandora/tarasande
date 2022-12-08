package net.tarasandedevelopment.tarasande.util.player

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ChatScreen
import net.minecraft.client.gui.screen.TitleScreen
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen
import net.minecraft.client.input.KeyboardInput
import net.minecraft.client.realms.gui.screen.RealmsMainScreen
import net.minecraft.entity.Entity
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.util.Hand
import net.minecraft.util.UseAction
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.RaycastContext
import net.minecraft.world.RaycastContext.FluidHandling
import net.minecraft.world.RaycastContext.ShapeType
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.event.EventInput
import net.tarasandedevelopment.tarasande.event.EventIsEntityAttackable
import net.tarasandedevelopment.tarasande.injection.accessor.IChatScreen
import net.tarasandedevelopment.tarasande.injection.accessor.IGameRenderer
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.player.ModuleAutoTool
import net.tarasandedevelopment.tarasande.util.math.rotation.Rotation
import net.tarasandedevelopment.tarasande.util.math.rotation.RotationUtil
import org.lwjgl.glfw.GLFW
import su.mandora.event.EventDispatcher

object PlayerUtil {

    val movementKeys = listOf(
        MinecraftClient.getInstance().options.forwardKey,
        MinecraftClient.getInstance().options.leftKey,
        MinecraftClient.getInstance().options.backKey,
        MinecraftClient.getInstance().options.rightKey
    )
    val input = KeyboardInput(MinecraftClient.getInstance().options)

    init {
        EventDispatcher.add(EventInput::class.java) {
            if (it.input == MinecraftClient.getInstance().player?.input)
                input.tick(it.slowDown, it.slowdownAmount)
        }
    }

    fun isPlayerMoving() = MinecraftClient.getInstance().player!!.input.movementInput.lengthSquared() != 0.0F

    fun isAttackable(entity: Entity?): Boolean {
        if (entity == null) return false

        val eventIsEntityAttackable = EventIsEntityAttackable(entity, entity != MinecraftClient.getInstance().player)
        EventDispatcher.call(eventIsEntityAttackable)
        return eventIsEntityAttackable.attackable
    }

    fun getTargetedEntity(reach: Double, rotation: Rotation, allowThroughWalls: Boolean = true): HitResult? {
        val gameRenderer = MinecraftClient.getInstance().gameRenderer
        val accessor = (gameRenderer as IGameRenderer)
        val renderTickCounter = MinecraftClient.getInstance().renderTickCounter

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
        renderTickCounter.tickDelta = 1.0F

        gameRenderer.updateTargetedEntity(1.0F)
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
        return RotationUtil.getYaw(input.movementInput) + (if (input.movementInput.lengthSquared() != 0.0F) 0.0 else 90.0) + MinecraftClient.getInstance().player!!.yaw
    }

    fun isOnEdge(extrapolation: Double) = MinecraftClient.getInstance().player!!.let {
        MinecraftClient.getInstance().world!!.isSpaceEmpty(it, it.boundingBox.offset(it.velocity.x * extrapolation, -it.stepHeight.toDouble(), it.velocity.z * extrapolation))
    }

    fun getUsedHand(): Hand? {
        for (hand in Hand.values()) {
            val stack = MinecraftClient.getInstance().player!!.getStackInHand(hand)
            if (!stack.isEmpty) {
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
                if (MinecraftClient.getInstance().player?.hasStatusEffect(StatusEffects.SPEED) == true)
                    MinecraftClient.getInstance().player?.getStatusEffect(StatusEffects.SPEED)?.amplifier!!
                else
                    0
    }

    private fun createFakeChat(block: (ChatScreen) -> Unit) {
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
            block(it)
        }
    }

    fun sendChatMessage(text: String, bypassEvent: Boolean) {
        createFakeChat {
            (it as IChatScreen).tarasande_setBypassChat(bypassEvent)
            for (c in text.toCharArray()) it.charTyped(c, 0)
            it.keyPressed(GLFW.GLFW_KEY_ENTER, 0, 0)
        }
    }

    fun getBreakSpeed(blockPos: BlockPos): Pair<Double, Int> {
        if (!TarasandeMain.managerModule().get(ModuleAutoTool::class.java).enabled)
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
        if (hardness <= 0.0F) return 1.0
        MinecraftClient.getInstance().player?.inventory?.selectedSlot = item
        var mult = MinecraftClient.getInstance().player?.getBlockBreakingSpeed(state)!!
        if (!MinecraftClient.getInstance().player?.isOnGround!!) {
            mult *= 5.0F // bruh
        }
        return 1.0 - mult / hardness / 30.0
    }

    fun predictFallDistance(position: BlockPos = MinecraftClient.getInstance().player?.blockPos!!): Int? {
        var y = 0
        while (MinecraftClient.getInstance().world?.isAir(position.add(0, -y, 0).also { if (it.y < 0) return null })!!) {
            y++
        }
        return y
    }

    fun disconnect() {
        MinecraftClient.getInstance().world?.disconnect()
        MinecraftClient.getInstance().disconnect()

        val title = TitleScreen()

        if (MinecraftClient.getInstance().isInSingleplayer) {
            MinecraftClient.getInstance().setScreen(title)
        } else if (MinecraftClient.getInstance().isConnectedToRealms) {
            MinecraftClient.getInstance().setScreen(RealmsMainScreen(title))
        } else {
            MinecraftClient.getInstance().setScreen(MultiplayerScreen(title))
        }
    }

    fun attack(entity: Entity?) {
        val original = MinecraftClient.getInstance().crosshairTarget
        if (entity != null) {
            MinecraftClient.getInstance().crosshairTarget = EntityHitResult(entity)
        } else {
            MinecraftClient.getInstance().crosshairTarget = object : HitResult(null) {
                override fun getType() = Type.MISS
            }
        }
        MinecraftClient.getInstance().doAttack()
        MinecraftClient.getInstance().crosshairTarget = original
    }
}