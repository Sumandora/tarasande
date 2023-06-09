package su.mandora.tarasande.system.feature.modulesystem.impl.player

import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket
import net.minecraft.registry.Registries
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import su.mandora.tarasande.event.impl.EventAttack
import su.mandora.tarasande.event.impl.EventPacket
import su.mandora.tarasande.event.impl.EventRotation
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.base.valuesystem.impl.ValueRegistry
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.system.feature.modulesystem.impl.render.ModuleBlockESP
import su.mandora.tarasande.util.extension.javaruntime.clearAndGC
import su.mandora.tarasande.util.extension.minecraft.BlockPos
import su.mandora.tarasande.util.extension.minecraft.isMissHitResult
import su.mandora.tarasande.util.extension.minecraft.packet.isNewWorld
import su.mandora.tarasande.util.math.TimeUtil
import su.mandora.tarasande.util.math.rotation.RotationUtil
import su.mandora.tarasande.util.player.PlayerUtil
import su.mandora.tarasande.util.player.chat.CustomChat
import kotlin.math.ceil

class ModuleBlockAura : Module("Block aura", "Automatically interacts with blocks", ModuleCategory.PLAYER) {

    private val delay = ValueNumber(this, "Delay", 0.0, 200.0, 1000.0, 50.0)
    private val reach = ValueNumber(this, "Reach", 0.1, 4.5, 6.0, 0.1)
    private val blocks = object : ValueRegistry<Block>(this, "Blocks", Registries.BLOCK, true, Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.ENDER_CHEST) {
        override fun filter(key: Block) = !key.defaultState.getOutlineShape(mc.world, BlockPos.ORIGIN).isEmpty
        override fun getTranslationKey(key: Any?) = (key as Block).translationKey
    }
    private val closedInventory = ValueBoolean(this, "Closed inventory", false)
    private val autoCloseScreens = ValueBoolean(this, "Auto close screens", false)
    private val throughWalls = ValueBoolean(this, "Through walls", true)
    private val interactOnce = object : ValueBoolean(this, "Interact once", true) {
        override fun onChange(oldValue: Boolean?, newValue: Boolean) {
            interactedBlocks.clearAndGC()
        }
    }

    private var interactedBlocks = ArrayList<BlockPos>()
    private val timeUtil = TimeUtil()

    private var focusedBlock: Pair<BlockPos, BlockHitResult>? = null

    override fun onDisable() {
        interactedBlocks.clearAndGC()
        focusedBlock = null
    }

    init {
        registerEvent(EventRotation::class.java) { event ->
            focusedBlock = null

            if (!timeUtil.hasReached(delay.value.toLong()))
                return@registerEvent

            val rad = ceil(reach.value).toInt()

            val list = ArrayList<Pair<BlockPos, HitResult>>()

            for (x in -rad..rad) for (y in -rad..rad) for (z in -rad..rad) {
                val blockPos = BlockPos(mc.player?.eyePos!!).add(x, y, z)
                val blockState = mc.world?.getBlockState(blockPos) ?: continue
                if (!blocks.isSelected(blockState.block)) continue

                val collisionShape = blockState.getOutlineShape(mc.world, blockPos)

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
                if(interactOnce.value)
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
                            CustomChat.printChatMessage("Auto closed a screen")
                            event.cancelled = true
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