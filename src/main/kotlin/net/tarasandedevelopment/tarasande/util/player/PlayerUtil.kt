package net.tarasandedevelopment.tarasande.util.player

import net.minecraft.client.gui.screen.ChatScreen
import net.minecraft.client.gui.screen.GameMenuScreen
import net.minecraft.client.input.KeyboardInput
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
import net.tarasandedevelopment.tarasande.event.EventInput
import net.tarasandedevelopment.tarasande.event.EventIsEntityAttackable
import net.tarasandedevelopment.tarasande.event.EventTick
import net.tarasandedevelopment.tarasande.feature.rotation.Rotations
import net.tarasandedevelopment.tarasande.injection.accessor.IChatScreen
import net.tarasandedevelopment.tarasande.injection.accessor.IGameRenderer
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ManagerModule
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.player.ModuleAutoTool
import net.tarasandedevelopment.tarasande.util.extension.minecraft.isBlockHitResult
import net.tarasandedevelopment.tarasande.util.math.rotation.Rotation
import net.tarasandedevelopment.tarasande.util.math.rotation.RotationUtil
import org.lwjgl.glfw.GLFW
import su.mandora.event.EventDispatcher

object PlayerUtil {

    val movementKeys = listOf(
        mc.options.forwardKey,
        mc.options.leftKey,
        mc.options.backKey,
        mc.options.rightKey
    )
    val input = KeyboardInput(mc.options)

    init {
        EventDispatcher.add(EventInput::class.java) {
            if (it.input == mc.player?.input)
                input.tick(it.slowDown, it.slowdownAmount)
        }
    }

    fun isPlayerMoving() = mc.player!!.input.movementInput.lengthSquared() != 0.0F

    fun isAttackable(entity: Entity?): Boolean {
        if (entity == null) return false

        val eventIsEntityAttackable = EventIsEntityAttackable(entity, entity != mc.player)
        EventDispatcher.call(eventIsEntityAttackable)
        return eventIsEntityAttackable.attackable
    }

    fun getTargetedEntity(reach: Double, rotation: Rotation, allowThroughWalls: Boolean = true): HitResult? {
        val gameRenderer = mc.gameRenderer
        val accessor = (gameRenderer as IGameRenderer)
        val renderTickCounter = mc.renderTickCounter

        val prevAllowThroughWalls = accessor.tarasande_isAllowThroughWalls()
        val prevReach = accessor.tarasande_getReach()
        val prevReachExtension = accessor.tarasande_isDisableReachExtension()

        accessor.tarasande_setAllowThroughWalls(allowThroughWalls)
        accessor.tarasande_setReach(reach)
        accessor.tarasande_setDisableReachExtension(true)

        val prevCrosshairTarget = mc.crosshairTarget
        val prevTargetedEntity = mc.targetedEntity

        val prevCameraEntity = mc.cameraEntity
        mc.cameraEntity = mc.player // not using setter to avoid shader unloading, this is purely math, no rendering

        val prevYaw = mc.player!!.yaw
        val prevPitch = mc.player!!.pitch

        mc.player!!.yaw = rotation.yaw
        mc.player!!.pitch = rotation.pitch

        val prevFakeRotation = Rotations.fakeRotation
        Rotations.fakeRotation = null // prevent rotationvec override by mixin

        val prevTickDelta = renderTickCounter.tickDelta
        renderTickCounter.tickDelta = 1.0F

        gameRenderer.updateTargetedEntity(1.0F)
        val hitResult = mc.crosshairTarget

        renderTickCounter.tickDelta = prevTickDelta

        Rotations.fakeRotation = prevFakeRotation

        mc.player?.yaw = prevYaw
        mc.player?.pitch = prevPitch

        mc.cameraEntity = prevCameraEntity

        mc.crosshairTarget = prevCrosshairTarget
        mc.targetedEntity = prevTargetedEntity

        accessor.tarasande_setReach(prevReach)
        accessor.tarasande_setAllowThroughWalls(prevAllowThroughWalls)
        accessor.tarasande_setDisableReachExtension(prevReachExtension)

        return hitResult
    }

    fun rayCast(start: Vec3d, end: Vec3d): BlockHitResult = mc.world!!.raycast(RaycastContext(start, end, ShapeType.OUTLINE, FluidHandling.NONE, mc.player!!))

    fun canVectorBeSeen(start: Vec3d, end: Vec3d): Boolean {
        return !rayCast(start, end).isBlockHitResult()
    }

    fun getMoveDirection(): Double {
        return RotationUtil.getYaw(input.movementInput) + (if (input.movementInput.lengthSquared() != 0.0F) 0.0 else 90.0) + mc.player!!.yaw
    }

    fun isOnEdge(extrapolation: Double) = mc.player!!.let {
        mc.world!!.isSpaceEmpty(it, it.boundingBox.offset(it.velocity.x * extrapolation, -it.stepHeight.toDouble(), it.velocity.z * extrapolation))
    }

    fun getUsedHand(): Hand? {
        for (hand in Hand.values()) {
            val stack = mc.player!!.getStackInHand(hand)
            if (!stack.isEmpty) {
                if (stack.useAction == UseAction.NONE)
                    continue

                return hand
            }
        }
        return null
    }

    const val DEFAULT_WALK_SPEED = 0.28

    fun calcBaseSpeed(baseSpeed: Double = DEFAULT_WALK_SPEED): Double {
        return baseSpeed + 0.03 *
                if (mc.player?.hasStatusEffect(StatusEffects.SPEED) == true)
                    mc.player?.getStatusEffect(StatusEffects.SPEED)?.amplifier!!
                else
                    0
    }

    private fun createFakeChat(block: (ChatScreen) -> Unit) {
        object : ChatScreen("") {
            override fun narrateScreenIfNarrationEnabled(onlyChangedNarrations: Boolean) {
                // Disable narrator
            }

            override fun close() {
            }

            override fun removed() {
            }

            override fun sendMessage(chatText: String?, addToHistory: Boolean): Boolean {
                super.sendMessage(chatText, false)
                return false
            }
        }.also {
            it.init(mc, mc.window.scaledWidth, mc.window.scaledHeight)
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

    fun getBreakSpeed(blockPos: BlockPos): Pair<Float, Int> {
        if (!ManagerModule.get(ModuleAutoTool::class.java).let { it.enabled.value && it.mode.isSelected(0) })
            return mc.player?.inventory?.selectedSlot!!.let { Pair(getBreakSpeed(blockPos, it), it) }


        val origSlot = mc.player?.inventory?.selectedSlot
        val best = (0..8).map { getBreakSpeed(blockPos, it) to it }.minBy { it.first }
        mc.player?.inventory?.selectedSlot = origSlot
        return best
    }

    fun getBreakSpeed(blockPos: BlockPos, item: Int): Float {
        val state = mc.world?.getBlockState(blockPos) ?: return 0.0F

        val origSlot = mc.player?.inventory?.selectedSlot
        mc.player?.inventory?.selectedSlot = item

        val speed = state.calcBlockBreakingDelta(mc.player, mc.world, blockPos)

        mc.player?.inventory?.selectedSlot = origSlot

        return 1.0F - speed
    }

    fun predictFallDistance(position: BlockPos = mc.player?.blockPos!!): Int? {
        var y = 0
        while (mc.world?.isAir(position.add(0, -y, 0).also { if (it.y < 0) return null })!!) {
            y++
        }
        return (y - 1).coerceAtLeast(0)
    }

    private var queuedDisconnect = false
    init {
        EventDispatcher.add(EventTick::class.java, Int.MAX_VALUE) {
            if(mc.world == null)
                queuedDisconnect = false // race condition?
            else if(queuedDisconnect) {
                GameMenuScreen(true).also {
                    it.init(mc, mc.window.scaledWidth, mc.window.scaledHeight)
                    it.exitButton!!.onPress()
                }
                queuedDisconnect = false
            }
        }
    }

    fun disconnect() {
        if(mc.world != null)
            queuedDisconnect = true
    }

    fun attack(entity: Entity?, position: Vec3d? = null) {
        val original = mc.crosshairTarget
        if (entity != null) {
            mc.crosshairTarget = if(position == null) EntityHitResult(entity) else EntityHitResult(entity, position)
        } else {
            mc.crosshairTarget = object : HitResult(null) {
                override fun getType() = Type.MISS
            }
        }
        mc.doAttack()
        mc.crosshairTarget = original
    }

    fun placeBlock(blockHitResult: BlockHitResult) {
        val original = mc.crosshairTarget
        mc.crosshairTarget = blockHitResult
        mc.doItemUse()
        mc.crosshairTarget = original
    }

    fun getSequence(): Int {
        return mc.world?.pendingUpdateManager?.incrementSequence()?.sequence!!
    }

    fun updateLastPosition() {
        mc.player?.apply {
            lastX = x
            lastBaseY = y
            lastZ = z
        }
    }
}