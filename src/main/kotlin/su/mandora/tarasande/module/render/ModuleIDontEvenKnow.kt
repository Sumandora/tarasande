package su.mandora.tarasande.module.render

import net.minecraft.block.BedBlock
import net.minecraft.block.Blocks
import net.minecraft.block.enums.BedPart
import net.minecraft.client.MinecraftClient
import net.minecraft.client.world.ClientWorld
import net.minecraft.util.math.BlockPos
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventRender3D
import su.mandora.tarasande.event.EventUpdate
import su.mandora.tarasande.util.pathfinder.Node
import su.mandora.tarasande.util.pathfinder.PathFinder
import su.mandora.tarasande.util.render.RenderUtil
import su.mandora.tarasande.value.ValueColor
import su.mandora.tarasande.value.ValueNumber
import java.util.function.Consumer

class ModuleIDontEvenKnow : Module("I don't even know", "Calculates the best way to break a bed", ModuleCategory.RENDER) {

    private val searchRadius = ValueNumber(this, "Search radius", 1.0, 10.0, 50.0, 1.0)
    private val depth = ValueNumber(this, "Depth", 1.0, 512.0, 1024.0, 8.0)

    private val bedColor = ValueColor(this, "Bed color", 0.0f, 1.0f, 1.0f, 1.0f)
    private val solutionColor = ValueColor(this, "Solution color", 0.0f, 1.0f, 1.0f, 1.0f)

    private var bedDatas = ArrayList<BedData>()
    private val beds = ArrayList<Pair<BlockPos, BlockPos>>()

    private fun allSurroundings(blockPos: BlockPos): ArrayList<BlockPos> {
        val list = ArrayList<BlockPos>()
        for (x in -1..1 step 2)
            list.add(blockPos.add(x, 0, 0))
        for (y in 0..1 step 2)
            list.add(blockPos.add(0, y, 0))
        for (z in -1..1 step 2)
            list.add(blockPos.add(0, 0, z))
        return list
    }

    private fun addDefender(depth: Int, blockPos: BlockPos, prevList: ArrayList<BlockPos>): ArrayList<BlockPos>? {
        if (depth > this.depth.value) return null
        val surroundings = allSurroundings(blockPos)
        surroundings.removeIf { prevList.contains(it) }
        surroundings.removeIf { mc.world?.getBlockState(it)?.block == Blocks.AIR }
        for (block in surroundings) {
            if (!mc.world?.getBlockState(block)?.isAir!!) {
                prevList.add(block)
                addDefender(depth + 1, block, prevList) ?: return null
            }
        }
        return prevList
    }

    private fun calculateDefenses(blockPos: Array<BlockPos>): List<BlockPos>? {
        val list = ArrayList<BlockPos>()

        for (pos in blockPos) {
            addDefender(0, pos, list) ?: return null
        }

        return list.distinct()
    }

    val eventConsumer = Consumer<Event> { event ->
        when (event) {
            is EventUpdate -> {
                if (event.state == EventUpdate.State.PRE) {
                    val rad = searchRadius.value.toInt()

                    beds.removeIf { arrayOf(it.first, it.second).any { mc.world?.getBlockState(it)?.block !is BedBlock } }
                    bedDatas.clear()

                    val beds = ArrayList<BlockPos>()

                    for (x in -rad..rad)
                        for (y in -rad..rad)
                            for (z in -rad..rad) {
                                val blockPos = BlockPos(mc.player?.eyePos).add(x, y, z)
                                val blockState = mc.world?.getBlockState(blockPos)

                                if (blockState?.block is BedBlock)
                                    beds.add(blockPos)
                            }

                    if (beds.isEmpty())
                        return@Consumer

                    for (bed in beds) {
                        val state = mc.world?.getBlockState(bed)!!
                        val part = state.get(BedBlock.PART)
                        val facing = state.get(BedBlock.FACING)?.let { if (part == BedPart.FOOT) it else it.opposite }
                        val otherPartOfBed = bed.offset(facing)
                        val bedParts = arrayOf(bed, otherPartOfBed)

                        val defenders = calculateDefenses(bedParts)?.let { ArrayList(it) } ?: continue
                        val outstanders = defenders.filter { allSurroundings(it).any { mc.world?.getBlockState(it)?.isAir!! } }
                        defenders.removeIf { bedParts.contains(it) }

                        val solution = Breaker.findSolution(outstanders, bedParts)
                        bedDatas.add(BedData(bedParts, defenders, solution))
                    }
                }
            }
            is EventRender3D -> {
                for (bedData in bedDatas) {
                    for (bedPart in bedData.bedParts) {
                        val blockPos = BlockPos(bedPart.x, bedPart.y, bedPart.z)
                        val blockState = mc.world?.getBlockState(blockPos)
                        RenderUtil.blockOutline(event.matrices, blockState?.getOutlineShape(mc.world, blockPos)?.offset(blockPos.x.toDouble(), blockPos.y.toDouble(), blockPos.z.toDouble())!!, bedColor.getColor().rgb)
                    }
                    for (node in bedData.solution ?: return@Consumer) {
                        val blockPos = BlockPos(node.x, node.y, node.z)
                        val blockState = mc.world?.getBlockState(blockPos)
                        RenderUtil.blockOutline(event.matrices, blockState?.getOutlineShape(mc.world, blockPos)?.offset(blockPos.x.toDouble(), blockPos.y.toDouble(), blockPos.z.toDouble())!!, solutionColor.getColor().rgb)
                    }
                }
            }
        }
    }

}

class BedData(val bedParts: Array<BlockPos>, val defenders: List<BlockPos>, val solution: List<Node>?)

object Breaker {

    private val costCalc = object : Function2<Node, Node, Double> {
        override fun invoke(current: Node, movement: Node): Double {
            return getBreakSpeed(BlockPos(movement.x, movement.y, movement.z))
        }
    }

    fun findSolution(outstanders: List<BlockPos>, beds: Array<BlockPos>): List<Node>? {
        var bestWay: ArrayList<Node>? = null
        for (outstander in outstanders) {
            val bestBed = beds.minByOrNull { it.getSquaredDistance(outstander) } ?: continue
            val beginNode = Node(outstander.x, outstander.y, outstander.z)
            val endNode = Node(bestBed.x, bestBed.y, bestBed.z)
            val way = PathFinder.findPath(beginNode, endNode, object : Function2<ClientWorld?, Node, Boolean> {
                override fun invoke(world: ClientWorld?, node: Node) = true
            }, cost = costCalc) ?: continue
            if (bestWay == null)
                bestWay = way
            else {
                val bestG = bestWay[bestWay.size - 1].g
                val newG = way[way.size - 1].g
                when {
                    bestG > newG -> bestWay = way
                    bestG == newG -> {
                        val bestPos = bestWay[0]
                        val newPos = way[0]
                        if (MinecraftClient.getInstance().player?.squaredDistanceTo(bestPos.x.toDouble(), bestPos.y.toDouble(), bestPos.z.toDouble())!! >
                            MinecraftClient.getInstance().player?.squaredDistanceTo(newPos.x.toDouble(), newPos.y.toDouble(), newPos.z.toDouble())!!)
                            bestWay = way
                    }
                }
            }
        }
        return bestWay
    }

    fun getBreakSpeed(blockPos: BlockPos): Double {
        if (MinecraftClient.getInstance().world?.isAir(blockPos) == true) return 1.0
        val origSlot = MinecraftClient.getInstance().player?.inventory?.selectedSlot ?: return 1.0
        var bestMult = 0.0f
        val state = MinecraftClient.getInstance().world?.getBlockState(blockPos)
        for (i in 0..8) {
            MinecraftClient.getInstance().player?.inventory?.selectedSlot = i
            var mult = MinecraftClient.getInstance().player?.getBlockBreakingSpeed(state)!!
            if (MinecraftClient.getInstance().player?.isOnGround == false) {
                mult *= 5.0f // bruh
            }
            if (bestMult < mult) {
                bestMult = mult
            }
        }
        MinecraftClient.getInstance().player?.inventory?.selectedSlot = origSlot
        return 1.0 - bestMult / state?.getHardness(MinecraftClient.getInstance().world, blockPos)!! / 30.0
    }
}