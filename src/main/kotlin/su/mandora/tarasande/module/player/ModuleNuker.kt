package su.mandora.tarasande.module.player

import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventHandleBlockBreaking
import su.mandora.tarasande.event.EventPollEvents
import su.mandora.tarasande.event.EventUpdate
import su.mandora.tarasande.mixin.accessor.IMinecraftClient
import su.mandora.tarasande.util.math.rotation.RotationUtil
import su.mandora.tarasande.util.player.PlayerUtil
import su.mandora.tarasande.value.ValueBlock
import su.mandora.tarasande.value.ValueMode
import su.mandora.tarasande.value.ValueNumber
import java.util.function.Consumer
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.pow

class ModuleNuker : Module("Nuker", "Destroys certain blocks in a certain radius", ModuleCategory.PLAYER) {

    private val selectionMode = ValueMode(this, "Selection mode", false, "Include", "Exclude")
    private val blocks = object : ValueBlock(this, "Blocks") {
        override fun filter(block: Block) = block != Blocks.AIR
    }
    private val radius = ValueNumber(this, "Radius", 0.1, 4.5, 6.0, 0.1)
    private val throughWalls = ValueMode(this, "Through walls", false, "Off", "On", "Free")
    private val breakSpeed = ValueMode(this, "Break speed", false, "Vanilla", "Instant")
    private val maxDestructions = object : ValueNumber(this, "Max destructions", 1.0, floor(4.5.pow(3.0)), 6.0.pow(3.0), 1.0) {
        override fun isEnabled() = breakSpeed.isSelected(1)
    }

    private val list = ArrayList<Pair<BlockPos, BlockHitResult>>()
    private var breaking = false

    val eventConsumer = Consumer<Event> { event ->
        when (event) {
            is EventPollEvents -> {
                val rad = ceil(radius.value).toInt()

                list.clear()

                for (x in -rad..rad)
                    for (y in -rad..rad)
                        for (z in -rad..rad) {
                            var blockPos = BlockPos(mc.player?.eyePos).add(x, y, z)
                            val blockState = mc.world?.getBlockState(blockPos)
                            if (
                                (selectionMode.isSelected(0) && blocks.list.contains(blockState?.block)) ||
                                (selectionMode.isSelected(1) && !blocks.list.contains(blockState?.block))
                            ) {
                                val collisionShape = blockState?.block?.defaultState?.getCollisionShape(mc.world, blockPos)
                                if (collisionShape != null && !collisionShape.isEmpty) {
                                    val pos = collisionShape.boundingBox.offset(blockPos.x.toDouble(), blockPos.y.toDouble(), blockPos.z.toDouble()).center
                                    if (pos.squaredDistanceTo(mc.player?.eyePos) <= radius.value * radius.value) {
                                        val blockVec = Vec3d.of(blockPos)
                                        val hitResult = PlayerUtil.rayCast(mc.player?.eyePos!!, blockVec.add(pos.subtract(blockVec))) ?: continue
                                        if (hitResult.type != HitResult.Type.BLOCK) continue
                                        if (!throughWalls.isSelected(1)) {
                                            when {
                                                throughWalls.isSelected(0) -> {
                                                    if (hitResult.blockPos != blockPos)
                                                        continue
                                                }
                                                throughWalls.isSelected(2) -> {
                                                    blockPos = hitResult.blockPos
                                                }
                                            }
                                        }
                                        list.add(Pair(blockPos, hitResult))
                                    }
                                }
                            }
                        }

                if (list.isNotEmpty()) {
                    val newList = ArrayList(list.distinct()
                        .sortedBy { it.first.getSquaredDistance(mc.player?.eyePos) }
                        .let { it.subList(0, min(maxDestructions.value.toInt(), it.size)) })
                    list.clear()
                    list.addAll(newList)

                    event.rotation = RotationUtil.getRotations(mc.player?.eyePos!!, Vec3d.ofCenter(list[0].first)).correctSensitivity()

                    event.minRotateToOriginSpeed = 1.0
                    event.maxRotateToOriginSpeed = 1.0
                }
            }
            is EventUpdate -> {
                if (event.state == EventUpdate.State.PRE) {
                    breaking = false
                    when {
                        breakSpeed.isSelected(0) -> {
                            if (list.isNotEmpty()) {
                                val pair = list[0]
                                if (!mc.interactionManager?.isBreakingBlock!!) {
                                    val original = mc.crosshairTarget
                                    mc.crosshairTarget = pair.second
                                    (mc as IMinecraftClient).invokeDoAttack()
                                    mc.crosshairTarget = original
                                    breaking = true
                                }
                            }
                        }
                        breakSpeed.isSelected(1) -> {
                            for (pair in list) {
                                mc.networkHandler?.sendPacket(PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pair.first, pair.second.side))
                                mc.player?.swingHand(Hand.MAIN_HAND)
                                mc.networkHandler?.sendPacket(PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, pair.first, pair.second.side))
                            }
                        }
                    }
                }
            }
            is EventHandleBlockBreaking -> {
                event.parameter = event.parameter || breaking
            }
        }
    }

}