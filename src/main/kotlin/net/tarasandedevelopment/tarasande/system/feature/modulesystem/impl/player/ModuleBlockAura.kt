package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.player

import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket
import net.minecraft.registry.Registries
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.tarasandedevelopment.tarasande.event.EventAttack
import net.tarasandedevelopment.tarasande.event.EventPacket
import net.tarasandedevelopment.tarasande.event.EventPollEvents
import net.tarasandedevelopment.tarasande.feature.notification.Notifications
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueRegistry
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ManagerModule
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.render.ModuleBlockESP
import net.tarasandedevelopment.tarasande.util.extension.javaruntime.clearAndGC
import net.tarasandedevelopment.tarasande.util.extension.minecraft.isMissHitResult
import net.tarasandedevelopment.tarasande.util.extension.minecraft.packet.isNewWorld
import net.tarasandedevelopment.tarasande.util.math.TimeUtil
import net.tarasandedevelopment.tarasande.util.math.rotation.RotationUtil
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil
import kotlin.math.ceil

class ModuleBlockAura : Module("Block aura", "Automatically interacts with blocks", ModuleCategory.PLAYER) {

    private val delay = ValueNumber(this, "Delay", 0.0, 200.0, 1000.0, 50.0)
    private val reach = ValueNumber(this, "Reach", 0.1, 4.5, 6.0, 0.1)
    private val block = object : ValueRegistry<Block>(this, "Blocks", Registries.BLOCK, Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.ENDER_CHEST) {
        override fun filter(key: Block) = !key.defaultState.getCollisionShape(mc.world, BlockPos.ORIGIN).isEmpty
        override fun getTranslationKey(key: Any?) = (key as Block).translationKey
    }
    private val closedInventory = ValueBoolean(this, "Closed inventory", false)
    private val autoCloseScreens = ValueBoolean(this, "Auto close screens", false)
    private val throughWalls = ValueBoolean(this, "Through walls", true)

    private var interactedBlocks = ArrayList<BlockPos>()
    private val timeUtil = TimeUtil()

    private var focusedBlock: Pair<BlockPos, BlockHitResult>? = null

    override fun onDisable() {
        interactedBlocks.clearAndGC()
        focusedBlock = null
    }

    init {
        registerEvent(EventPollEvents::class.java) { event ->
            focusedBlock = null

            if (!timeUtil.hasReached(delay.value.toLong()))
                return@registerEvent

            val rad = ceil(reach.value).toInt()

            val list = ArrayList<Pair<BlockPos, HitResult>>()

            for (x in -rad..rad) for (y in -rad..rad) for (z in -rad..rad) {
                val blockPos = BlockPos(mc.player?.eyePos).add(x, y, z)
                val blockState = mc.world?.getBlockState(blockPos) ?: continue
                if (!block.isSelected(blockState.block)) continue

                val collisionShape = blockState.getCollisionShape(mc.world, blockPos)

                if (collisionShape != null && !collisionShape.isEmpty) {
                    val pos = collisionShape.boundingBox.offset(blockPos.x.toDouble(), blockPos.y.toDouble(), blockPos.z.toDouble()).center
                    if (pos.squaredDistanceTo(mc.player?.eyePos) <= reach.value * reach.value) {
                        val hitResult = PlayerUtil.rayCast(mc.player?.eyePos!!, pos)
                        if (hitResult.isMissHitResult())
                            continue
                        if (!throughWalls.value && hitResult.blockPos != pos)
                            continue
                        list.add(Pair(blockPos, hitResult))
                    }
                }
            }

            if (list.isNotEmpty()) {
                val best = list.distinct().filter { !interactedBlocks.contains(it.first) }.minByOrNull { it.second.pos.distanceTo(mc.player?.eyePos) } ?: return@registerEvent
                val hitResult = best.second as BlockHitResult

                focusedBlock = Pair(best.first, hitResult)
                event.rotation = RotationUtil.getRotations(mc.player?.eyePos!!, hitResult.pos).correctSensitivity()
            }
        }
        registerEvent(EventAttack::class.java, 1001) {
            if (closedInventory.value && mc.currentScreen != null) return@registerEvent

            if (focusedBlock != null) {
                PlayerUtil.placeBlock(focusedBlock!!.second.withBlockPos(focusedBlock!!.first)!!)
                interactedBlocks.add(focusedBlock!!.first)
                val moduleBlockESP = ManagerModule.get(ModuleBlockESP::class.java)
                if (moduleBlockESP.enabled.value)
                    moduleBlockESP.list.removeIf { it.first == focusedBlock!!.first }
                timeUtil.reset()
            }
        }
        registerEvent(EventPacket::class.java) { event ->
            if (event.type == EventPacket.Type.RECEIVE) {
                when (event.packet) {
                    is OpenScreenS2CPacket -> {
                        if (autoCloseScreens.value) {
                            mc.networkHandler?.sendPacket(CloseHandledScreenC2SPacket(event.packet.syncId))
                            Notifications.notify("Auto closed a screen")
                        }
                    }

                    is PlayerRespawnS2CPacket -> {
                        if (event.packet.isNewWorld()) {
                            onDisable()
                        }
                    }
                }
            }
        }
    }

}