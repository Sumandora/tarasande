package su.mandora.tarasande.system.feature.modulesystem.impl.player

import net.minecraft.block.Block
import net.minecraft.registry.Registries
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import su.mandora.tarasande.event.impl.EventAttack
import su.mandora.tarasande.event.impl.EventHandleBlockBreaking
import su.mandora.tarasande.event.impl.EventRotation
import su.mandora.tarasande.event.impl.EventSwing
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.base.valuesystem.impl.ValueRegistry
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.util.DEFAULT_BLOCK_REACH
import su.mandora.tarasande.util.extension.javaruntime.clearAndGC
import su.mandora.tarasande.util.extension.minecraft.isMissHitResult
import su.mandora.tarasande.util.extension.minecraft.math.BlockPos
import su.mandora.tarasande.util.math.TimeUtil
import su.mandora.tarasande.feature.rotation.api.RotationUtil
import su.mandora.tarasande.util.maxReach
import su.mandora.tarasande.util.player.PlayerUtil
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.pow

class ModuleNuker : Module("Nuker", "Destroys certain blocks in a certain radius", ModuleCategory.PLAYER) {

    private val selectionMode = ValueMode(this, "Selection mode", false, "Include", "Exclude")
    private val includedBlocks = object : ValueRegistry<Block>(this, "Included blocks", Registries.BLOCK, true, isEnabled = { selectionMode.isSelected(0) }) {
        override fun filter(key: Block) = !key.defaultState.getCollisionShape(mc.world, BlockPos.ORIGIN).isEmpty && key.defaultState.calcBlockBreakingDelta(mc.player, mc.world, BlockPos.ORIGIN) > 0.0
        override fun getTranslationKey(key: Any?) = (key as Block).translationKey
    }
    private val excludedBlocks = object : ValueRegistry<Block>(this, "Excluded blocks", Registries.BLOCK, true, isEnabled = { selectionMode.isSelected(1) }) {
        override fun filter(key: Block) = !key.defaultState.getCollisionShape(mc.world, BlockPos.ORIGIN).isEmpty && key.defaultState.calcBlockBreakingDelta(mc.player, mc.world, BlockPos.ORIGIN) > 0.0
        override fun getTranslationKey(key: Any?) = (key as Block).translationKey
    }
    private val radius = ValueNumber(this, "Radius", 0.1, DEFAULT_BLOCK_REACH, maxReach, 0.1)
    private val throughWalls = ValueMode(this, "Through walls", false, "Off", "On", "Free")
    private val breakSpeed = ValueMode(this, "Break speed", false, "Vanilla", "Instant")
    private val delay = ValueNumber(this, "Delay", 0.0, 200.0, 1000.0, 10.0, isEnabled = { breakSpeed.isSelected(1) })
    private val maxDestructions = ValueNumber(this, "Max destructions", 1.0, floor(DEFAULT_BLOCK_REACH.pow(3.0)), maxReach.pow(3.0), 1.0, isEnabled = { breakSpeed.isSelected(1) })
    private val priority = ValueMode(this, "Priority", false, "Far away", "Nearby", "Break speed")

    private var list = ArrayList<Pair<BlockPos, BlockHitResult>>()
    private var breaking = false

    private val selector: (Pair<BlockPos, BlockHitResult>) -> Double? = {
        when {
            priority.isSelected(0) -> mc.player?.eyePos?.squaredDistanceTo(Vec3d.ofCenter(it.first))?.times(-1)
            priority.isSelected(1) -> mc.player?.eyePos?.squaredDistanceTo(Vec3d.ofCenter(it.first))
            priority.isSelected(2) -> PlayerUtil.getBreakSpeed(it.first).first.toDouble()
            else -> 0.0
        }
    }

    private val timeUtil = TimeUtil()
    private var succeededBreak = false

    override fun onDisable() {
        list.clearAndGC()
        breaking = false
    }

    init {
        registerEvent(EventRotation::class.java) { event ->
            val rad = ceil(radius.value).toInt()

            list.clear()

            for (x in -rad..rad) for (y in -rad..rad) for (z in -rad..rad) {
                val blockPos = BlockPos(mc.player?.eyePos!!).add(x, y, z)
                val blockState = mc.world?.getBlockState(blockPos)

                if (blockState?.calcBlockBreakingDelta(mc.player, mc.world, blockPos)!! <= 0.0)
                    continue
                if (!(selectionMode.isSelected(0) && includedBlocks.isSelected(blockState.block)) && !(selectionMode.isSelected(1) && !excludedBlocks.isSelected(blockState.block)))
                    continue

                val collisionShape = blockState.getCollisionShape(mc.world, blockPos)

                if (collisionShape != null && !collisionShape.isEmpty) {
                    val pos = collisionShape.boundingBox.offset(blockPos.x.toDouble(), blockPos.y.toDouble(), blockPos.z.toDouble()).center
                    if (pos.squaredDistanceTo(mc.player?.eyePos) <= radius.value * radius.value) {
                        val hitResult = PlayerUtil.rayCast(mc.player?.eyePos!!, pos)
                        if (hitResult.isMissHitResult())
                            continue
                        if (throughWalls.isSelected(0) && hitResult.blockPos != blockPos)
                            continue
                        list.add(Pair(blockPos, hitResult))
                    }
                }
            }

            if (list.isNotEmpty()) {
                list = ArrayList(list.distinct().sortedBy(selector).let { if (maxDestructions.isEnabled()) it.subList(0, min(maxDestructions.value.toInt(), it.size)) else it })
                if (throughWalls.isSelected(2))
                    list = ArrayList(list.map { Pair(it.second.blockPos, it.second) })

                event.rotation = RotationUtil.getRotations(mc.player?.eyePos!!, list[0].second.pos).correctSensitivity()
            }
        }

        registerEvent(EventAttack::class.java) { event ->
            if (event.dirty)
                return@registerEvent
            when {
                breakSpeed.isSelected(0) -> {
                    if (list.isNotEmpty()) {
                        val pair = list[0]
                        mc.crosshairTarget = if (pair.second.blockPos == pair.first) pair.second else pair.second.withBlockPos(pair.first)
                        if (!breaking && mc.attackCooldown == 0) {
                            if (!mc.doAttack())
                                breaking = true
                            event.dirty = true
                        }
                    } else {
                        breaking = false
                    }
                }

                breakSpeed.isSelected(1) -> {
                    if (mc.attackCooldown > 0)
                        return@registerEvent
                    if (!timeUtil.hasReached(delay.value.toLong()))
                        return@registerEvent
                    for (pair in list) {
                        val original = mc.crosshairTarget
                        mc.crosshairTarget = if (pair.second.blockPos == pair.first) pair.second else pair.second.withBlockPos(pair.first)
                        if (!mc.doAttack()) {
                            while (mc.interactionManager?.isBreakingBlock == true) {
                                succeededBreak = false
                                mc.handleBlockBreaking(true)
                                if(!succeededBreak)
                                    break
                            }
                        }

                        mc.crosshairTarget = original
                        timeUtil.reset()
                        event.dirty = true
                    }
                }
            }
        }

        registerEvent(EventSwing::class.java) {
            succeededBreak = true
        }

        registerEvent(EventHandleBlockBreaking::class.java) { event ->
            event.parameter = event.parameter || (breaking && mc.attackCooldown == 0)
        }
    }
}
