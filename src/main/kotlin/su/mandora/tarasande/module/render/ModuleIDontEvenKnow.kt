package su.mandora.tarasande.module.render

import net.minecraft.block.BedBlock
import net.minecraft.block.Blocks
import net.minecraft.block.enums.BedPart
import net.minecraft.client.MinecraftClient
import net.minecraft.client.world.ClientWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.event.EventRender2D
import su.mandora.tarasande.event.EventRender3D
import su.mandora.tarasande.event.EventUpdate
import su.mandora.tarasande.util.pathfinder.Node
import su.mandora.tarasande.util.pathfinder.PathFinder
import su.mandora.tarasande.value.ValueNumber
import java.util.*
import java.util.function.Consumer
import kotlin.math.ceil

class ModuleIDontEvenKnow : Module("I don't even know", "Calculates the best way to break a bed", ModuleCategory.RENDER) {

    private val searchRadius = ValueNumber(this, "Search radius", 0.0, 10.0, 20.0, 1.0)

    private val beds = arrayOf(
        Blocks.WHITE_BED,
        Blocks.ORANGE_BED,
        Blocks.MAGENTA_BED,
        Blocks.LIGHT_BLUE_BED,
        Blocks.YELLOW_BED,
        Blocks.LIME_BED,
        Blocks.PINK_BED,
        Blocks.GRAY_BED,
        Blocks.LIGHT_GRAY_BED,
        Blocks.CYAN_BED,
        Blocks.PURPLE_BED,
        Blocks.BLUE_BED,
        Blocks.BROWN_BED,
        Blocks.GREEN_BED,
        Blocks.RED_BED,
        Blocks.BLACK_BED
    )

    private var bedData: BedData? = null
    private val cardinals = arrayOf(
        BlockPos(1, 0, 0),
        BlockPos(0, 0, 1),
        BlockPos(-1, 0, 0),
        BlockPos(0, 0, -1),
        BlockPos(0, 1, 0)
    )

    private fun allSurroundings(blockPos: BlockPos): ArrayList<BlockPos> {
        val list = ArrayList<BlockPos>()
        for (cardinal in cardinals) {
            list.add(blockPos.add(cardinal))
        }
        return list
    }

    private fun addDefender(blockPos: BlockPos, prevList: ArrayList<BlockPos>): ArrayList<BlockPos> {
        val surroundings = allSurroundings(blockPos)
        surroundings.removeIf { prevList.contains(it) }
        surroundings.removeIf { mc.world?.getBlockState(it)?.block == Blocks.AIR }
        for (block in surroundings) {
            if(!mc.world?.getBlockState(block)?.isAir!!) {
                prevList.add(block)
                addDefender(block, prevList)
            }
        }
        return prevList
    }

    private fun calculateDefenses(blockPos: Array<BlockPos>): List<BlockPos> {
        val list = ArrayList<BlockPos>()

        for (pos in blockPos) {
            addDefender(pos, list)
        }

        return list.distinct()
    }

    private val textElements = ArrayList<Pair<Vec2f, String>>()

    val eventConsumer = Consumer<Event> { event ->
        when (event) {
            is EventUpdate -> {
                if (event.state == EventUpdate.State.PRE) {
                    val rad = ceil(searchRadius.value).toInt()

                    bedData = null

                    val beds = ArrayList<BlockPos>()

                    for (x in -rad..rad)
                        for (y in -rad..rad)
                            for (z in -rad..rad) {
                                val blockPos = BlockPos(mc.player?.eyePos).add(x, y, z)
                                val blockState = mc.world?.getBlockState(blockPos)

                                if (this.beds.contains(blockState?.block))
                                    beds.add(blockPos)
                            }

                    if (beds.isEmpty())
                        return@Consumer
                    val bed = beds.minByOrNull { mc.player?.pos?.squaredDistanceTo(Vec3d.of(it))!! } ?: return@Consumer
                    val state = mc.world?.getBlockState(bed)!!
                    val part = state.get(BedBlock.PART)
                    val facing = state.get(BedBlock.FACING)?.let { if (part == BedPart.FOOT) it else it.opposite }
                    val otherPartOfBed = bed.offset(facing)
                    val bedParts = arrayOf(bed, otherPartOfBed)

                    val defenders = ArrayList(calculateDefenses(bedParts))
                    val outstanders = defenders.filter { allSurroundings(it).any { mc.world?.getBlockState(it)?.isAir!! } }
                    defenders.removeIf { bedParts.contains(it) }

                    val solution = Breaker.findSolution(outstanders, defenders, bedParts)
                    bedData = BedData(bedParts, outstanders, defenders.filter { !outstanders.contains(it) }, solution)
                }
            }
            is EventRender3D -> {
                textElements.clear()
                if (bedData == null) return@Consumer
                val moduleESP = TarasandeMain.get().managerModule?.get(ModuleESP::class.java)!!
//                for (bed in bedData?.blockPos!!) {
//                    val pos = moduleESP.project(event.matrices.peek().positionMatrix, event.positionMatrix, Vec3d.ofCenter(bed))
//                    if (pos != null) {
//                        textElements.add(Pair(Vec2f(pos.x.toFloat(), pos.y.toFloat()), "Bed\n" + Breaker.getBreakSpeed(bed)))
//                    }
//                }
//                for (outstander in bedData?.outstanders!!) {
//                    val pos = moduleESP.project(event.matrices.peek().positionMatrix, event.positionMatrix, Vec3d.ofCenter(outstander))
//                    if (pos != null) {
//                        textElements.add(Pair(Vec2f(pos.x.toFloat(), pos.y.toFloat()), "Outstander\n" + Breaker.getBreakSpeed(outstander)))
//                    }
//                }
//                for (defender in bedData?.defenders!!) {
//                    val pos = moduleESP.project(event.matrices.peek().positionMatrix, event.positionMatrix, Vec3d.ofCenter(defender))
//                    if (pos != null) {
//                        textElements.add(Pair(Vec2f(pos.x.toFloat(), pos.y.toFloat()), "Defender\n" + Breaker.getBreakSpeed(defender)))
//                    }
//                }
                for (solutioners in bedData?.solution ?: return@Consumer) {
                    val pos = moduleESP.project(event.matrices.peek().positionMatrix, event.positionMatrix, Vec3d.ofCenter(Vec3i(solutioners.x, solutioners.y, solutioners.z)))
                    if (pos != null) {
                        textElements.add(Pair(Vec2f(pos.x.toFloat(), pos.y.toFloat()), "§eSolutioners\n" + solutioners.g))
                    }
                }
            }
            is EventRender2D -> {
                for (textElement in textElements) {
                    event.matrices.push()
                    event.matrices.translate(textElement.first.x.toDouble(), textElement.first.y.toDouble(), 0.0)
                    event.matrices.scale(0.5f, 0.5f, 1.0f)
                    event.matrices.translate(-textElement.first.x.toDouble(), -textElement.first.y.toDouble(), 0.0)
                    val array = textElement.second.split("\n")
                    for((index, str) in array.withIndex())
                        mc.textRenderer.drawWithShadow(event.matrices, str, textElement.first.x - mc.textRenderer.getWidth(str) / 2, (textElement.first.y + mc.textRenderer.fontHeight * (index - array.size/2 - 0.5)).toFloat(), -1)
                    event.matrices.pop()
                }
            }
        }
    }

}

class BedData(val blockPos: Array<BlockPos>, val outstanders: List<BlockPos>, val defenders: List<BlockPos>, val solution: List<Node>?)

object Breaker {

    private val costCalc = object : Function2<Node, Node, Double> {
        override fun invoke(current: Node, movement: Node): Double {
            return getBreakSpeed(BlockPos(movement.x, movement.y, movement.z))
        }
    }

    fun findSolution(outstanders: List<BlockPos>, defense: List<BlockPos>, beds: Array<BlockPos>): List<Node>? {
        var bestWay: ArrayList<Node>? = null
        for(outstander in outstanders) {
            val bestBed = beds.minByOrNull { it.getSquaredDistance(outstander) } ?: continue
            val beginNode = Node(outstander.x, outstander.y, outstander.z)
            val endNode = Node(bestBed.x, bestBed.y, bestBed.z)
            val way = PathFinder.findPath(beginNode, endNode, object : Function2<ClientWorld?, Node, Boolean> {
                override fun invoke(world: ClientWorld?, node: Node) = true
            }, cost = costCalc) ?: continue
            if(bestWay == null)
                bestWay = way
            else {
                val bestG = bestWay[bestWay.size - 1].g
                val newG = way[way.size - 1].g
                when {
                    bestG > newG -> bestWay = way
                    bestG == newG -> {
                        val bestPos = bestWay[0]
                        val newPos = way[0]
                        if(MinecraftClient.getInstance().player?.squaredDistanceTo(bestPos.x.toDouble(), bestPos.y.toDouble(), bestPos.z.toDouble())!! >
                            MinecraftClient.getInstance().player?.squaredDistanceTo(newPos.x.toDouble(), newPos.y.toDouble(), newPos.z.toDouble())!!)
                            bestWay = way
                    }
                }
            }
        }
        return bestWay
    }

    fun getBreakSpeed(blockPos: BlockPos): Double {
        if(MinecraftClient.getInstance().world?.isAir(blockPos) == true) return 1.0
        val origSlot = MinecraftClient.getInstance().player?.inventory?.selectedSlot ?: return 1.0
        var bestMult = 0.0f
        val state = MinecraftClient.getInstance().world?.getBlockState(blockPos)
        for (i in 0..8) {
            MinecraftClient.getInstance().player?.inventory?.selectedSlot = i
            var mult = MinecraftClient.getInstance().player?.getBlockBreakingSpeed(state)!!
            if(MinecraftClient.getInstance().player?.isOnGround == false) {
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