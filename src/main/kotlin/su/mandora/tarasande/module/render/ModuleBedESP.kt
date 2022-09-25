package su.mandora.tarasande.module.render

import net.minecraft.block.BedBlock
import net.minecraft.block.Blocks
import net.minecraft.block.enums.BedPart
import net.minecraft.client.MinecraftClient
import net.minecraft.util.math.BlockPos
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventRender3D
import su.mandora.tarasande.event.EventUpdate
import su.mandora.tarasande.util.pathfinder.Node
import su.mandora.tarasande.util.pathfinder.PathFinder
import su.mandora.tarasande.util.render.RenderUtil
import su.mandora.tarasande.value.ValueBoolean
import su.mandora.tarasande.value.ValueColor
import su.mandora.tarasande.value.ValueNumber
import java.util.function.BiFunction
import java.util.function.Consumer

/**
 * This module is pretty cool, but can be extremely processing intensive
 * In theory you should be able to go from the inside to the outside, but trying that resulted in not getting perfect results anymore
 */
class ModuleBedESP : Module("Bed ESP", "Highlights all beds", ModuleCategory.RENDER) {

    private val searchRadius = ValueNumber(this, "Search radius", 1.0, 10.0, 50.0, 1.0)

    internal val calculateBestWay = ValueBoolean(this, "Calculate best way", true)

    private val depth = object : ValueNumber(this, "Depth", 1.0, 512.0, 1024.0, 8.0) {
        override fun isEnabled() = calculateBestWay.value
    }

    private val bedColor = object : ValueColor(this, "Bed color", 0.0f, 1.0f, 1.0f, 1.0f) {
        override fun isEnabled() = calculateBestWay.value
    }
    private val defenderColor = object : ValueColor(this, "Defender color", 0.0f, 1.0f, 1.0f, 0.0f) {
        override fun isEnabled() = calculateBestWay.value
    }
    private val solutionColor = object : ValueColor(this, "Solution color", 0.0f, 1.0f, 1.0f, 1.0f) {
        override fun isEnabled() = calculateBestWay.value
    }

    // Going full polak
    private val refreshRate = object : ValueNumber(this, "Refresh rate", 1.0, 5.0, 20.0, 1.0) {
        override fun isEnabled() = calculateBestWay.value
    }
    private val maxProcessingTime = object : ValueNumber(this, "Max processing time", 1.0, 10.0, 100.0, 1.0) {
        override fun isEnabled() = calculateBestWay.value
    }

    internal var bedDatas = ArrayList<BedData>()

    private fun allSurroundings(blockPos: BlockPos): ArrayList<BlockPos> {
        val list = ArrayList<BlockPos>()
        for (x in -1..1 step 2) list.add(blockPos.add(x, 0, 0))
        for (y in 0..1) list.add(blockPos.add(0, y, 0))
        for (z in -1..1 step 2) list.add(blockPos.add(0, 0, z))
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

    override fun onDisable() {
        bedDatas.clear()
    }

    val eventConsumer = Consumer<Event> { event ->
        when (event) {
            is EventUpdate -> {
                if (event.state == EventUpdate.State.PRE) {
                    if (mc.player?.age?.mod(refreshRate.value.toInt()) != 0) return@Consumer

                    val rad = searchRadius.value.toInt()

                    bedDatas.clear()

                    val beds = ArrayList<BlockPos>()

                    for (x in -rad..rad) for (y in -rad..rad) for (z in -rad..rad) {
                        val blockPos = BlockPos(mc.gameRenderer.camera.pos).add(x, y, z)
                        val blockState = mc.world?.getBlockState(blockPos)

                        if (blockState?.block is BedBlock)
                            beds.add(blockPos)
                    }

                    for (bed in beds) {
                        val state = mc.world?.getBlockState(bed)!!
                        val part = state.get(BedBlock.PART)
                        val facing = state.get(BedBlock.FACING)?.let { if (part == BedPart.FOOT) it else it.opposite }
                        val otherPartOfBed = bed.offset(facing)
                        val bedParts = arrayOf(bed, otherPartOfBed)

                        if (bedDatas.any { it.bedParts.any { part1 -> bedParts.any { part2 -> part1 == part2 } } }) continue // we already processed these

                        if (!calculateBestWay.value || bedParts.any { allSurroundings(it).any { mc.world?.isAir(it)!! } }) {
                            bedDatas.add(BedData(bedParts, null, null))
                            continue // this is pointless
                        }

                        val defenders = calculateDefenses(bedParts)?.let { ArrayList(it) }
                        var solution: List<Node>? = null
                        if (defenders != null) {
                            val outstanders = defenders.filter {
                                allSurroundings(it).any {
                                    mc.world?.getBlockState(it)?.let { state ->
                                        state.isAir || state.getCollisionShape(MinecraftClient.getInstance().world, it).isEmpty
                                    }!!
                                }
                            }
                            defenders.removeIf { bedParts.contains(it) }

                            if (outstanders.any { mc.world?.getBlockState(it)?.block is BedBlock }) continue // not a bedwars bed

                            solution = Breaker.findSolution(outstanders, defenders, bedParts, maxProcessingTime.value.toLong())
                        }
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
                    for (node in bedData.defenders ?: continue) {
                        val blockPos = BlockPos(node.x, node.y, node.z)
                        val blockState = mc.world?.getBlockState(blockPos)
                        RenderUtil.blockOutline(event.matrices, blockState?.getOutlineShape(mc.world, blockPos)?.offset(blockPos.x.toDouble(), blockPos.y.toDouble(), blockPos.z.toDouble())!!, defenderColor.getColor().rgb)
                    }
                    for (node in bedData.solution ?: continue) {
                        val blockPos = BlockPos(node.x, node.y, node.z)
                        val blockState = mc.world?.getBlockState(blockPos)
                        RenderUtil.blockOutline(event.matrices, blockState?.getOutlineShape(mc.world, blockPos)?.offset(blockPos.x.toDouble(), blockPos.y.toDouble(), blockPos.z.toDouble())!!, solutionColor.getColor().rgb)
                    }
                }
            }
        }
    }

    internal inner class BedData(val bedParts: Array<BlockPos>, val defenders: List<BlockPos>?, val solution: List<Node>?) {
        override fun toString(): String {
            val stringBuilder = StringBuilder()

            stringBuilder.append("Bed at: " + bedParts.joinToString("; ") { "[" + it.toShortString() + "]" } + "\n")
            if (defenders != null)
                stringBuilder.append("Defenders: " + defenders.count() + "\n")
            if (solution != null) {
                stringBuilder.append("Solution length: " + solution.count() + "\n")
                stringBuilder.append("Used blocks: " + solution.map { mc.world?.getBlockState(BlockPos(it.x, it.y, it.z))?.block?.name?.string }.distinct().joinToString() + "\n")
            }

            return stringBuilder.toString()
        }
    }

    object Breaker {

        private val breakSpeed = BiFunction<Node, Node, Double> { _, movement -> getBreakSpeed(BlockPos(movement.x, movement.y, movement.z)).first }
        private var defenders: List<BlockPos>? = null

        private val pathFinder = PathFinder({ _, node -> defenders?.contains(BlockPos(node.x, node.y, node.z)) == true }, cost = breakSpeed)

        fun findSolution(outstanders: List<BlockPos>, defenders: List<BlockPos>, beds: Array<BlockPos>, maxProcessingTime: Long): List<Node>? {
            this.defenders = defenders
            var bestWay: List<Node>? = null
            val begin = System.currentTimeMillis()
            for (outstander in outstanders) {
                if (System.currentTimeMillis() - begin > maxProcessingTime) break

                val bestBed = beds.minByOrNull { it.getSquaredDistance(outstander) } ?: continue
                val beginNode = Node(outstander.x, outstander.y, outstander.z)
                val endNode = Node(bestBed.x, bestBed.y, bestBed.z)
                val way = pathFinder.findPath(beginNode, endNode, maxProcessingTime) ?: break // timeout
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
                            if (MinecraftClient.getInstance().player?.squaredDistanceTo(bestPos.x.toDouble(), bestPos.y.toDouble(), bestPos.z.toDouble())!! > MinecraftClient.getInstance().player?.squaredDistanceTo(newPos.x.toDouble(), newPos.y.toDouble(), newPos.z.toDouble())!!)
                                bestWay = way
                        }
                    }
                }
            }
            return bestWay
        }

        fun getBreakSpeed(blockPos: BlockPos): Pair<Double, Int> {
            val origSlot = MinecraftClient.getInstance().player?.inventory?.selectedSlot ?: return Pair(1.0, -1)
            var bestMult = 1.0
            var bestTool = -1
            for (i in 0..8) {
                val mult = getBreakSpeed(blockPos, i)
                if (bestMult > mult) {
                    bestTool = i
                    bestMult = mult
                }
            }
            MinecraftClient.getInstance().player?.inventory?.selectedSlot = origSlot
            return Pair(bestMult, bestTool)
        }

        fun getBreakSpeed(blockPos: BlockPos, item: Int): Double {
            val state = MinecraftClient.getInstance().world?.getBlockState(blockPos)
            if (state?.isAir!! || state.getOutlineShape(MinecraftClient.getInstance().world, blockPos).isEmpty) return 1.0
            val hardness = state.getHardness(MinecraftClient.getInstance().world, blockPos)
            if (hardness <= 0.0f) return 1.0
            MinecraftClient.getInstance().player?.inventory?.selectedSlot = item
            var mult = MinecraftClient.getInstance().player?.getBlockBreakingSpeed(state)!!
            if (!MinecraftClient.getInstance().player?.isOnGround!!) {
                mult *= 5.0f // bruh
            }
            return 1.0 - mult / hardness / 30.0
        }
    }

}