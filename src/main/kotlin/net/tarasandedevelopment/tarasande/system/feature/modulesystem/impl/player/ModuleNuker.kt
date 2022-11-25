package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.player

import net.minecraft.block.Block
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.registry.Registry
import net.tarasandedevelopment.tarasande.event.EventAttack
import net.tarasandedevelopment.tarasande.event.EventHandleBlockBreaking
import net.tarasandedevelopment.tarasande.event.EventPollEvents
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueMode
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueRegistry
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory
import net.tarasandedevelopment.tarasande.util.math.TimeUtil
import net.tarasandedevelopment.tarasande.util.math.rotation.RotationUtil
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil
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
    private val delay = object : ValueNumber(this, "Delay", 0.0, 200.0, 1000.0, 1.0) {
        override fun isEnabled() = breakSpeed.isSelected(1)
    }
    private val maxDestructions = object : ValueNumber(this, "Max destructions", 1.0, floor(4.5.pow(3.0)), 6.0.pow(3.0), 1.0) {
        override fun isEnabled() = breakSpeed.isSelected(1)
    }
    private val priority = ValueMode(this, "Priority", false, "Far away", "Nearby", "Break speed")

    private var list = ArrayList<Pair<BlockPos, BlockHitResult>>()
    private var breaking = false

    private val selector: (Pair<BlockPos, BlockHitResult>) -> Double? = {
        when {
            priority.isSelected(0) -> mc.player?.eyePos?.squaredDistanceTo(Vec3d.ofCenter(it.first))?.times(-1)
            priority.isSelected(1) -> mc.player?.eyePos?.squaredDistanceTo(Vec3d.ofCenter(it.first))
            priority.isSelected(2) -> PlayerUtil.getBreakSpeed(it.first).first
            else -> 0.0
        }
    }

    private val timeUtil = TimeUtil()

    override fun onDisable() {
        if (breaking)
            mc.interactionManager?.cancelBlockBreaking()
        breaking = false
    }

    init {
        registerEvent(EventPollEvents::class.java) { event ->
            val rad = ceil(radius.value).toInt()

            list.clear()

            for (x in -rad..rad) for (y in -rad..rad) for (z in -rad..rad) {
                val blockPos = BlockPos(mc.player?.eyePos).add(x, y, z)
                val blockState = mc.world?.getBlockState(blockPos)

                if (blockState?.calcBlockBreakingDelta(mc.player, mc.world, blockPos)!! <= 0.0)
                    continue
                if (!(selectionMode.isSelected(0) && includedBlocks.list.contains(blockState.block)) && !(selectionMode.isSelected(1) && !excludedBlocks.list.contains(blockState.block)))
                    continue

                val collisionShape = blockState.getCollisionShape(mc.world, blockPos)

                if (collisionShape != null && !collisionShape.isEmpty) {
                    val pos = collisionShape.boundingBox.offset(blockPos.x.toDouble(), blockPos.y.toDouble(), blockPos.z.toDouble()).center
                    if (pos.squaredDistanceTo(mc.player?.eyePos) <= radius.value * radius.value) {
                        val hitResult = PlayerUtil.rayCast(mc.player?.eyePos!!, pos)
                        if (hitResult.type != HitResult.Type.BLOCK)
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
                mc.player?.yaw = event.rotation.yaw
                mc.player?.pitch = event.rotation.pitch

                event.minRotateToOriginSpeed = 1.0
                event.maxRotateToOriginSpeed = 1.0
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
                            while (mc.interactionManager?.isBreakingBlock == true)
                                mc.handleBlockBreaking(true)
                        }
                        mc.crosshairTarget = original
                        timeUtil.reset()
                        event.dirty = true
                    }
                }
            }
        }

        registerEvent(EventHandleBlockBreaking::class.java) { event ->
            event.parameter = event.parameter || (breaking && mc.attackCooldown == 0)
        }
    }
}
