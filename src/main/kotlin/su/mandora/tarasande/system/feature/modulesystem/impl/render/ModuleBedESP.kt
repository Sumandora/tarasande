package su.mandora.tarasande.system.feature.modulesystem.impl.render

import net.minecraft.block.BedBlock
import net.minecraft.block.enums.BedPart
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import su.mandora.tarasande.event.impl.EventRender3D
import su.mandora.tarasande.event.impl.EventUpdate
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueColor
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.system.screen.informationsystem.Information
import su.mandora.tarasande.system.screen.informationsystem.ManagerInformation
import su.mandora.tarasande.util.extension.javaruntime.clearAndGC
import su.mandora.tarasande.util.extension.minecraft.math.BlockPos
import su.mandora.tarasande.util.extension.minecraft.math.boundingBox
import su.mandora.tarasande.util.extension.minecraft.math.div
import su.mandora.tarasande.util.extension.minecraft.math.plus
import su.mandora.tarasande.util.math.pathfinder.Node
import su.mandora.tarasande.util.math.pathfinder.PathFinder
import su.mandora.tarasande.util.player.PlayerUtil
import su.mandora.tarasande.util.render.RenderUtil

class ModuleBedESP : Module("Bed ESP", "Highlights all beds", ModuleCategory.RENDER) {

    private val searchRadius = ValueNumber(this, "Search radius", 1.0, 10.0, 50.0, 1.0)

    private val maxDefenders = ValueNumber(this, "Max defenders", 1.0, 128.0, 512.0, 64.0)

    private val bedColor = ValueColor(this, "Bed color", 0.0, 1.0, 1.0, 1.0)
    private val defenderColor = ValueColor(this, "Defender color", 0.0, 1.0, 1.0, 0.0)
    private val solutionColor = ValueColor(this, "Solution color", 0.0, 1.0, 1.0, 1.0)

    // Going full polak
    private val refreshRate = ValueNumber(this, "Refresh rate", 1.0, 5.0, 20.0, 1.0)
    private val maxProcessingTime = ValueNumber(this, "Max processing time", 1.0, 10.0, 100.0, 1.0)

    var bedDatas = ArrayList<BedData>()

    init {
        ManagerInformation.add(object : Information("Bed ESP", "Beds") {
            override fun getMessage(): String? {
                if (enabled.value && bedDatas.isNotEmpty()) {
                    return "\n" + bedDatas.sortedBy {
                        mc.player?.squaredDistanceTo(it.bedParts.let { bedPart ->
                            var vec = Vec3d.ZERO
                            bedPart.forEach { bedPos -> vec += Vec3d.ofCenter(bedPos) }
                            vec / bedPart.size
                        })
                    }.joinToString("\n") { it.toString() }.let { it.substring(0, it.length - 1) }
                }

                return null
            }
        })
    }

    private fun allSurroundings(blockPos: BlockPos): ArrayList<BlockPos> {
        val list = ArrayList<BlockPos>()
        for (x in -1..1 step 2) list.add(blockPos.add(x, 0, 0))
        for (y in 0..1) list.add(blockPos.add(0, y, 0))
        for (z in -1..1 step 2) list.add(blockPos.add(0, 0, z))
        return list
    }

    private fun calculateDefenses(beds: Array<BlockPos>): List<BlockPos>? {
        val list = HashSet<BlockPos>()
        val open = ArrayList<BlockPos>()
        open.addAll(beds)

        while (open.isNotEmpty()) {
            val block = open.removeFirst()

            list.add(block)
            if (list.size > maxDefenders.value)
                return null

            for (newBlock in allSurroundings(block)) {
                if (!list.contains(newBlock) && !mc.world?.getBlockState(newBlock)?.isAir!!) {
                    open.add(newBlock)
                }
            }
        }

        return list.toList()
    }

    override fun onDisable() {
        bedDatas.clearAndGC()
    }

    init {
        registerEvent(EventUpdate::class.java) { event ->
            if (event.state == EventUpdate.State.PRE) {
                if (mc.player?.age?.mod(refreshRate.value.toInt()) != 0) return@registerEvent

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

                    if (bedParts.any { allSurroundings(it).any { surrounding -> mc.world?.isAir(surrounding)!! } }) {
                        bedDatas.add(BedData(bedParts, null, null))
                        continue // this is pointless
                    }

                    val defenders = calculateDefenses(bedParts)?.let { ArrayList(it) }
                    var solution: List<Node>? = null
                    if (defenders != null) {
                        val outsiders = defenders.filter {
                            allSurroundings(it).any { surrounding ->
                                mc.world?.getBlockState(surrounding)?.let { state ->
                                    state.isAir || state.getCollisionShape(mc.world, it).isEmpty
                                }!!
                            }
                        }

                        if (outsiders.any { mc.world?.getBlockState(it)?.block is BedBlock }) continue // not a bedwars-bed

                        solution = Breaker.findSolution(outsiders, defenders, bedParts, maxProcessingTime.value.toLong())

                        defenders.removeIf { bedParts.contains(it) }
                    }
                    bedDatas.add(BedData(bedParts, defenders, solution))
                }
            }
        }

        registerEvent(EventRender3D::class.java) { event ->
            if(event.state != EventRender3D.State.POST) return@registerEvent

            for (bedData in bedDatas) {
                for (bedPart in bedData.bedParts) {
                    val blockPos = BlockPos(bedPart.x, bedPart.y, bedPart.z)
                    val blockState = mc.world?.getBlockState(blockPos)
                    RenderUtil.blockOutline(event.matrices, blockState?.getOutlineShape(mc.world, blockPos)?.boundingBox()?.offset(blockPos.x.toDouble(), blockPos.y.toDouble(), blockPos.z.toDouble())!!, bedColor.getColor().rgb)
                }
                for (node in bedData.defenders ?: continue) {
                    val blockPos = BlockPos(node.x, node.y, node.z)
                    val blockState = mc.world?.getBlockState(blockPos)
                    RenderUtil.blockOutline(event.matrices, blockState?.getOutlineShape(mc.world, blockPos)?.boundingBox()?.offset(blockPos.x.toDouble(), blockPos.y.toDouble(), blockPos.z.toDouble())!!, defenderColor.getColor().rgb)
                }
                for (node in bedData.solution ?: continue) {
                    val blockPos = BlockPos(node.x, node.y, node.z)
                    val blockState = mc.world?.getBlockState(blockPos)
                    RenderUtil.blockOutline(event.matrices, blockState?.getOutlineShape(mc.world, blockPos)?.boundingBox()?.offset(blockPos.x.toDouble(), blockPos.y.toDouble(), blockPos.z.toDouble())!!, solutionColor.getColor().rgb)
                }
            }
        }
    }

    inner class BedData(val bedParts: Array<BlockPos>, val defenders: List<BlockPos>?, val solution: List<Node>?) {
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

        private val breakSpeed: (Node, Node) -> Double = { _, movement -> PlayerUtil.getBreakSpeed(BlockPos(movement.x, movement.y, movement.z)).first.toDouble() }
        private var defenders: List<BlockPos>? = null

        private val pathFinder = PathFinder({ _, node -> defenders?.contains(BlockPos(node.x, node.y, node.z)) == true }, cost = breakSpeed)

        fun findSolution(outsiders: List<BlockPos>, defenders: List<BlockPos>, beds: Array<BlockPos>, maxProcessingTime: Long): List<Node>? {
            this.defenders = defenders
            var bestWay: List<Node>? = null
            val begin = System.currentTimeMillis()
            for (outsider in outsiders) {
                if (System.currentTimeMillis() - begin > maxProcessingTime) return null

                val bestBed = beds.minByOrNull { it.getSquaredDistance(outsider) } ?: continue
                val beginNode = Node(outsider.x, outsider.y, outsider.z)
                val endNode = Node(bestBed.x, bestBed.y, bestBed.z)
                val way = pathFinder.findPath(beginNode, endNode, maxProcessingTime) ?: return null // timeout
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
                            if (mc.player?.squaredDistanceTo(bestPos.x.toDouble(), bestPos.y.toDouble(), bestPos.z.toDouble())!! > mc.player?.squaredDistanceTo(newPos.x.toDouble(), newPos.y.toDouble(), newPos.z.toDouble())!!)
                                bestWay = way
                        }
                    }
                }
            }
            return bestWay
        }
    }

}