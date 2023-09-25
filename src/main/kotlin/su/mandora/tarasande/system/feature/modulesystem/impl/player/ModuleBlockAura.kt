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
import su.mandora.tarasande.feature.rotation.api.RotationUtil
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.base.valuesystem.impl.ValueRegistry
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.system.feature.modulesystem.impl.render.ModuleBlockESP
import su.mandora.tarasande.util.DEFAULT_BLOCK_REACH
import su.mandora.tarasande.util.extension.minecraft.isBlockHitResult
import su.mandora.tarasande.util.extension.minecraft.math.BlockPos
import su.mandora.tarasande.util.extension.minecraft.packet.isNewWorld
import su.mandora.tarasande.util.math.time.TimeUtil
import su.mandora.tarasande.util.maxReach
import su.mandora.tarasande.util.player.PlayerUtil
import su.mandora.tarasande.util.player.chat.CustomChat
import kotlin.math.ceil
import kotlin.math.min
import kotlin.math.pow

class ModuleBlockAura : Module("Block aura", "Automatically interacts with blocks", ModuleCategory.PLAYER) {

    private val delay = ValueNumber(this, "Delay", 0.0, 200.0, 1000.0, 50.0)
    private val reach = ValueNumber(this, "Reach", 0.1, DEFAULT_BLOCK_REACH, maxReach, 0.1)
    private val blocks = object : ValueRegistry<Block>(this, "Blocks", Registries.BLOCK, true, Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.ENDER_CHEST) {
        override fun filter(key: Block) = !key.defaultState.getOutlineShape(mc.world, BlockPos.ORIGIN).isEmpty
        override fun getTranslationKey(key: Any?) = (key as Block).translationKey
    }
    private val closedInventory = ValueBoolean(this, "Closed inventory", false)
    private val autoCloseScreens = ValueBoolean(this, "Auto close screens", false)
    private val throughWalls = ValueBoolean(this, "Through walls", true)
    private val interactOnce = object : ValueBoolean(this, "Interact once", true) {
        override fun onChange(oldValue: Boolean?, newValue: Boolean) {
            interactedBlocks = HashSet()
        }
    }
    private val maxInteractions = ValueNumber(this, "Max interactions", 1.0, 1.0, maxReach.pow(3.0), 1.0)
    private val shuffledOrder = ValueBoolean(this, "Shuffled order", true)
    private val notDuringSneaking = ValueBoolean(this, "Not during sneaking", true)

    private var interactedBlocks = HashSet<BlockPos>()
    private val timeUtil = TimeUtil()

    private var focusedBlocks = HashMap<BlockPos, BlockHitResult>()

    private val moduleBlockESP by lazy { ManagerModule.get(ModuleBlockESP::class.java) }

    override fun onDisable() {
        interactedBlocks = HashSet()
        focusedBlocks = HashMap()
    }

    init {
        registerEvent(EventRotation::class.java) { event ->
            focusedBlocks.clear()

            if (notDuringSneaking.value && mc.player?.isSneaking == true)
                return@registerEvent

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
                        if (!hitResult.isBlockHitResult())
                            continue
                        if (!throughWalls.value && hitResult.blockPos != pos)
                            continue
                        list.add(Pair(blockPos, hitResult))
                    }
                }
            }

            for(block in list
                .distinct()
                .filter { !interactedBlocks.contains(it.first) }
                .let { if(shuffledOrder.value) it.shuffled() else it }
                .let { it.subList(0, min(maxInteractions.value.toInt(), it.size)) }
                .sortedBy { it.second.pos.distanceTo(mc.player?.eyePos) }) {
                val hitResult = block.second as BlockHitResult

                focusedBlocks[block.first] = hitResult
                event.rotation = RotationUtil.getRotations(mc.player?.eyePos!!, hitResult.pos).correctSensitivity()
            }
        }
        registerEvent(EventAttack::class.java, 1001) {
            if (closedInventory.value && mc.currentScreen != null) return@registerEvent

            for ((blockPos, hitResult) in focusedBlocks) {
                PlayerUtil.interact(hitResult.withBlockPos(blockPos))
                if (interactOnce.value) {
                    interactedBlocks.add(blockPos)
                    if (moduleBlockESP.enabled.value)
                        moduleBlockESP.list.removeIf { it.first == blockPos }
                }
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