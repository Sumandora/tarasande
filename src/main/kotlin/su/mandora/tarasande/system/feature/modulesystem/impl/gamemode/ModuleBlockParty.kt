package su.mandora.tarasande.system.feature.modulesystem.impl.gamemode

import net.minecraft.block.Block
import net.minecraft.item.BlockItem
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket
import net.minecraft.registry.Registries
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import su.mandora.tarasande.event.impl.*
import su.mandora.tarasande.feature.rotation.Rotations
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.*
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.util.INVENTORY_SYNC_ID
import su.mandora.tarasande.util.extension.minecraft.packet.isNewWorld
import su.mandora.tarasande.util.math.rotation.RotationUtil
import su.mandora.tarasande.util.player.container.ContainerUtil
import su.mandora.tarasande.util.player.prediction.Input
import su.mandora.tarasande.util.player.prediction.PredictionEngine
import su.mandora.tarasande.util.player.prediction.with
import su.mandora.tarasande.util.render.RenderUtil
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.sqrt

class ModuleBlockParty : Module("Block party", "Automatically plays block party", ModuleCategory.GAMEMODE) {

    private val blocks = object : ValueRegistry<Block>(this, "Blocks", Registries.BLOCK, true, *Registries.BLOCK.filter { it.name.string.endsWith("_concrete") }.toTypedArray()) {
        override fun filter(key: Block) = !key.defaultState.getCollisionShape(mc.world, BlockPos.ORIGIN).isEmpty
        override fun getTranslationKey(key: Any?) = (key as Block).translationKey
    }
    private val hotbarSlot = ValueNumber(this, "Hotbar slot", 1.0, 1.0, 9.0, 1.0)

    private val moveAutomatically = ValueBoolean(this, "Move automatically", true)
    private val mode = ValueMode(this, "Mode", true, "Show dance area", "Show best block", "Show best preparation block", isEnabled = { moveAutomatically.value })
    private val danceAreaColor = ValueColor(this, "Dance area color", 0.0, 1.0, 1.0, 1.0, isEnabled = { mode.isSelected(0) })
    private val bestBlockColor = ValueColor(this, "Best block color", 0.0, 1.0, 1.0, 1.0, isEnabled = { mode.isSelected(1) })
    private val heatSpotsColor = ValueColor(this, "Heat spots color", 0.0, 1.0, 1.0, 1.0, isEnabled = { mode.isSelected(2) })

    private var danceArea: Box? = null
    private var best: Vec3d? = null

    private var move = false
    private var wasFilled = false

    private var heatSpots = CopyOnWriteArrayList<Vec3d>()

    private var preparing = false
    private var queueReset = false

    private fun allBlocksInDanceArea(): List<BlockPos> {
        val blocks = ArrayList<BlockPos>()
        for (x in danceArea!!.minX.toInt() until danceArea!!.maxX.toInt()) for (z in danceArea!!.minZ.toInt() until danceArea!!.maxZ.toInt()) {
            blocks.add(BlockPos(x, danceArea!!.minY.toInt(), z))
        }
        return blocks
    }

    private fun calculateBestBlock(block: Block): Vec3d? {
        return allBlocksInDanceArea()
            .filter { mc.world!!.getBlockState(it).block == block }
            .minByOrNull { mc.player!!.pos.distanceTo(it.toCenterPos()) }
            ?.let { it.toCenterPos().withAxis(Direction.Axis.Y, it.y + 1.0) }
    }

    private fun calculateBestPreparation(): Vec3d? {
        val colorIndex = HashMap<Block, Int>()
        val image = HashMap<Int, HashSet<Pair<Int, Int>>>()

        allBlocksInDanceArea().forEach {
            val block = mc.world!!.getBlockState(it).block
            image.computeIfAbsent(colorIndex.computeIfAbsent(block) { colorIndex.size }) { HashSet() }.add(Pair((it.x - danceArea!!.minX).toInt(), (it.z - danceArea!!.minZ).toInt()))
        }

        if (colorIndex.values.isEmpty())
            return null

        val map = HashMap<Pair<Int, Int>, CompletableFuture<Double>>()

        fun distance(x: Int, y: Int): Double {
            @Suppress("NAME_SHADOWING")
            val x = x.toDouble()

            @Suppress("NAME_SHADOWING")
            val y = y.toDouble()
            return sqrt(x * x + y * y)
        }

        image.forEach { (color, list) ->
            list.forEach { pos ->
                map[pos] = CompletableFuture.supplyAsync {
                    return@supplyAsync colorIndex.values
                        .filterNot { it == color }
                        .map { color ->
                            image[color]!!
                                .minOfOrNull { pos2 -> distance(pos2.first - pos.first, pos2.second - pos.second) }
                                ?: return@supplyAsync Double.MAX_VALUE // Might be a race condition, not sure?
                        }.sum()
                }
            }
        }

        if (map.isEmpty())
            return null

        val bestOnes = map.let {
            var bestScore = Double.POSITIVE_INFINITY
            val best = HashSet<Pair<Int, Int>>()
            for((pos, score) in it) {
                @Suppress("NAME_SHADOWING")
                val score = score.get()
                if(bestScore > score) {
                    best.clear()
                    bestScore = score
                }
                if(bestScore == score)
                    best.add(pos)
            }
            best
        }

        return bestOnes
            .map { Vec3d(danceArea!!.minX + it.first + 0.5, danceArea!!.maxY + 1, danceArea!!.minZ + it.second + 0.5) }
            .also { heatSpots.clear(); heatSpots.addAll(it) }
            .minBy { mc.player!!.pos.distanceTo(it) }
    }

    override fun onEnable() {
        if (mc.player != null) {
            val ground = mc.player!!.blockPos.add(0, -1, 0)
            if (!blocks.isSelected(mc.world!!.getBlockState(ground).block))
                return
            danceArea = Box(ground)
            for (direction in Direction.entries) {
                if (direction.offsetY == 0) {
                    var pos = ground
                    while (blocks.isSelected(mc.world!!.getBlockState(pos).block)) {
                        danceArea = danceArea!!.union(Box(pos))
                        pos = pos.add(direction.offsetX, 0, direction.offsetZ)
                    }
                }
            }
            danceArea = danceArea!!.withMaxY(danceArea!!.minY)
            wasFilled = false
        }
    }

    override fun onDisable() {
        danceArea = null
        best = null
        move = false
        wasFilled = false
        heatSpots = CopyOnWriteArrayList()
        preparing = false
        queueReset = false
    }

    init {
        registerEvent(EventUpdate::class.java) { event ->
            if (event.state == EventUpdate.State.PRE) {
                if (danceArea == null)
                    onEnable()
                if (danceArea != null) {
                    if (allBlocksInDanceArea().isEmpty()) {
                        onDisable()
                        return@registerEvent
                    }
                    val filled = allBlocksInDanceArea().none { mc.world!!.isAir(it) }

                    if (!wasFilled && filled) {
                        best = calculateBestPreparation()
                        preparing = true
                    } else if (!moveAutomatically.value) {
                        val item = mc.player!!.inventory!!.main[hotbarSlot.value.toInt() - 1].item
                        if (item !is BlockItem)
                            return@registerEvent
                        if (!blocks.isSelected(item.block))
                            return@registerEvent

                        best = calculateBestBlock(item.block)
                        preparing = false
                    }

                    if (moveAutomatically.value) {
                        if (best != null && mc.player!!.isOnGround) {
                            val currDist = mc.player!!.pos.distanceTo(best!!)
                            if (currDist < 0.5)
                                if (currDist < 0.3 || mc.player!!.pos.add(mc.player!!.velocity).distanceTo(best!!) < currDist)
                                    best = null
                        }

                        if(queueReset && mc.player!!.velocity.lengthSquared() == 0.0) {
                            best = null
                            queueReset = false
                        }

                        if(mc.player!!.y < danceArea!!.minY)
                            best = null

                        move = best != null
                    }

                    wasFilled = filled
                }
            }
        }

        registerEvent(EventPacket::class.java) { event ->
            if (event.type == EventPacket.Type.RECEIVE) {
                when (event.packet) {
                    is PlayerPositionLookS2CPacket ->
                        mc.executeSync { onDisable() }

                    is PlayerRespawnS2CPacket ->
                        if (event.packet.isNewWorld())
                            mc.executeSync { onDisable() }

                    is ScreenHandlerSlotUpdateS2CPacket ->
                        if (event.packet.syncId == INVENTORY_SYNC_ID) {
                            if (event.packet.slot == 36 + hotbarSlot.value.toInt() - 1) {
                                val item = event.packet.itemStack.item
                                if (item is BlockItem && blocks.isSelected(item.block)) {
                                    preparing = false
                                    best = calculateBestBlock(item.block)
                                    heatSpots.clear()
                                } else {
                                    val oldItem = ContainerUtil.getHotbarSlots()[hotbarSlot.value.toInt() - 1].item
                                    if(oldItem is BlockItem && blocks.isSelected(oldItem.block))
                                        queueReset = true
                                }
                            }
                        }
                }
            }
        }

        registerEvent(EventRender3D::class.java) { event ->
            if (danceArea != null && mode.isSelected(0))
                RenderUtil.blockOutline(event.matrices, danceArea!!.withMaxY(danceArea!!.minY + 1), danceAreaColor.getColor().rgb)
            if (best != null && mode.isSelected(1))
                RenderUtil.blockOutline(event.matrices, Box.from(best!!).offset(-0.5, -0.5, -0.5).expand(-0.45), bestBlockColor.getColor().rgb)
            if (mode.isSelected(2))
                for (heatSpot in heatSpots)
                    RenderUtil.blockOutline(event.matrices, Box.from(heatSpot).offset(-0.5, -0.5, -0.5).expand(2.0, 0.0, 2.0), heatSpotsColor.getColor().rgb)
        }

        registerEvent(EventRotation::class.java) { event ->
            if (!move || best == null) return@registerEvent
            val rotation = RotationUtil.getRotations(mc.player!!.eyePos, best!!)
            event.rotation = rotation
        }

        registerEvent(EventJump::class.java, 1) { event ->
            if (event.state != EventJump.State.PRE) return@registerEvent
            if (move)
                event.yaw = Rotations.fakeRotation?.yaw ?: return@registerEvent
        }

        registerEvent(EventVelocityYaw::class.java, 1) { event ->
            if (move)
                event.yaw = Rotations.fakeRotation?.yaw ?: return@registerEvent
        }

        registerEvent(EventInput::class.java) { event ->
            if (event.input == mc.player?.input)
                if (move) {
                    event.movementForward = 1F
                    event.movementSideways = 0F
                }
        }

        registerEvent(EventKeyBindingIsPressed::class.java) { event ->
            if (event.keyBinding == mc.options.jumpKey) {
                if (move && best != null) {
                    if (mc.player!!.isOnGround && !preparing) {
                        val realYaw = mc.player!!.yaw
                        mc.player!!.yaw = Rotations.fakeRotation?.yaw ?: return@registerEvent
                        val prediction = PredictionEngine.predictState(20, input = Input(1F, 0F).with(jumping = true), abortWhen = { it.isOnGround })
                        mc.player!!.yaw = realYaw
                        if (prediction.first.isOnGround && prediction.first.pos.distanceTo(best!!) < 0.7)
                            event.pressed = true
                    }
                }
            }
        }
    }

}