package su.mandora.tarasande.module.player

import net.minecraft.block.Block
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.registry.Registry
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventAttack
import su.mandora.tarasande.event.EventHandleBlockBreaking
import su.mandora.tarasande.event.EventPollEvents
import su.mandora.tarasande.mixin.accessor.IMinecraftClient
import su.mandora.tarasande.util.extension.minus
import su.mandora.tarasande.util.extension.plus
import su.mandora.tarasande.util.math.rotation.RotationUtil
import su.mandora.tarasande.util.player.PlayerUtil
import su.mandora.tarasande.value.ValueMode
import su.mandora.tarasande.value.ValueNumber
import su.mandora.tarasande.value.ValueRegistry
import java.util.function.Consumer
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.pow

class ModuleNuker : Module("Nuker", "Destroys certain blocks in a certain radius", ModuleCategory.PLAYER) {

    private val selectionMode = ValueMode(this, "Selection mode", false, "Include", "Exclude")
    private val includedBlocks = object : ValueRegistry<Block>(this, "Included blocks", Registry.BLOCK) {
        override fun isEnabled() = selectionMode.isSelected(0)
        override fun filter(key: Block) = !key.defaultState.getCollisionShape(mc.world, BlockPos.ORIGIN).isEmpty && key.defaultState.calcBlockBreakingDelta(mc.player, mc.world, BlockPos.ORIGIN) > 0.0
        override fun getTranslationKey(key: Any?) = (key as Block).translationKey
    }
    private val excludedBlocks = object : ValueRegistry<Block>(this, "Excluded blocks", Registry.BLOCK) {
        override fun isEnabled() = selectionMode.isSelected(1)
        override fun filter(key: Block) = !key.defaultState.getCollisionShape(mc.world, BlockPos.ORIGIN).isEmpty && key.defaultState.calcBlockBreakingDelta(mc.player, mc.world, BlockPos.ORIGIN) > 0.0
        override fun getTranslationKey(key: Any?) = (key as Block).translationKey
    }
    private val radius = ValueNumber(this, "Radius", 0.1, 4.5, 6.0, 0.1)
    private val throughWalls = ValueMode(this, "Through walls", false, "Off", "On", "Free")
    private val breakSpeed = ValueMode(this, "Break speed", false, "Vanilla", "Instant")
    private val maxDestructions = object : ValueNumber(this, "Max destructions", 1.0, floor(4.5.pow(3.0)), 6.0.pow(3.0), 1.0) {
        override fun isEnabled() = breakSpeed.isSelected(1)
    }
    private val priority = ValueMode(this, "Priority", false, "Far away", "Nearby", "Break speed")

    private var list = ArrayList<Pair<BlockPos, BlockHitResult>>()
    private var breaking = false

    private val selector: (Pair<BlockPos, BlockHitResult>) -> Double? = {
        when {
            priority.isSelected(0) -> mc.player?.squaredDistanceTo(Vec3d.ofCenter(it.first))?.times(-1)
            priority.isSelected(1) -> mc.player?.squaredDistanceTo(Vec3d.ofCenter(it.first))
            priority.isSelected(2) -> TarasandeMain.get().managerModule.get(ModuleAutoTool::class.java).getBreakSpeed(it.first)
            else -> 0.0
        }
    }

    val eventConsumer = Consumer<Event> { event ->
        when (event) {
            is EventPollEvents -> {
                val rad = ceil(radius.value).toInt()

                list.clear()

                for (x in -rad..rad) for (y in -rad..rad) for (z in -rad..rad) {
                    var blockPos = BlockPos(mc.player?.eyePos).add(x, y, z)
                    val blockState = mc.world?.getBlockState(blockPos)

                    if (blockState?.calcBlockBreakingDelta(mc.player, mc.world, BlockPos.ORIGIN)!! <= 0.0)
                        continue
                    if (!(selectionMode.isSelected(0) && includedBlocks.list.contains(blockState.block)) && !(selectionMode.isSelected(1) && !excludedBlocks.list.contains(blockState.block)))
                        continue

                    val collisionShape = blockState.getCollisionShape(mc.world, blockPos)

                    if (collisionShape != null && !collisionShape.isEmpty) {
                        val pos = collisionShape.boundingBox.offset(blockPos.x.toDouble(), blockPos.y.toDouble(), blockPos.z.toDouble()).center
                        if (pos.squaredDistanceTo(mc.player?.eyePos) <= radius.value * radius.value) {
                            val blockVec = Vec3d.ofCenter(blockPos)
                            val hitResult = PlayerUtil.rayCast(mc.player?.eyePos!!, blockVec + (pos - blockVec))
                            if (hitResult.type != HitResult.Type.BLOCK)
                                continue
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

                if (list.isNotEmpty()) {
                    val newList = ArrayList(list.distinct().sortedBy(selector).let { it.subList(0, min(maxDestructions.value.toInt(), it.size)) })
                    list = newList

                    event.rotation = RotationUtil.getRotations(mc.player?.eyePos!!, list[0].second.pos).correctSensitivity()

                    event.minRotateToOriginSpeed = 1.0
                    event.maxRotateToOriginSpeed = 1.0
                }
            }

            is EventAttack -> {
                breaking = false
                when {
                    breakSpeed.isSelected(0) -> {
                        if (list.isNotEmpty()) {
                            val pair = list[0]
                            mc.crosshairTarget = if (pair.second.blockPos == pair.first) pair.second else pair.second.withBlockPos(pair.first)
                            if ((mc as IMinecraftClient).tarasande_getAttackCooldown() == 0)
                                if (!(mc as IMinecraftClient).tarasande_invokeDoAttack())
                                    breaking = true
                        }
                    }

                    breakSpeed.isSelected(1) -> {
                        if ((mc as IMinecraftClient).tarasande_getAttackCooldown() > 0)
                            return@Consumer
                        for (pair in list) {
                            val original = mc.crosshairTarget
                            mc.crosshairTarget = if (pair.second.blockPos == pair.first) pair.second else pair.second.withBlockPos(pair.first)
                            if (!(mc as IMinecraftClient).tarasande_invokeDoAttack()) {
                                while (mc.interactionManager?.isBreakingBlock == true)
                                    (mc as IMinecraftClient).tarasande_invokeHandleBlockBreaking(true)
                            }
                            mc.crosshairTarget = original
                        }
                    }
                }
            }

            is EventHandleBlockBreaking -> {
                event.parameter = event.parameter || (breaking && (mc as IMinecraftClient).tarasande_getAttackCooldown() == 0)
            }
        }
    }

}