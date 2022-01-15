package su.mandora.tarasande.parkourbot.pathbuilder

import net.minecraft.client.MinecraftClient
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.util.math.rotation.RotationUtil
import su.mandora.tarasande.util.pathfinder.Movement
import su.mandora.tarasande.util.pathfinder.PathFinder
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

class PathBuilder(begin: BlockPos, private val goal: Goal) {

    val path = CopyOnWriteArrayList<BlockPos>()

    init {
        path.add(begin)
    }

    private fun compute(): BlockPos? {
        val last = path[path.size - 1]
        val possibilities = CopyOnWriteArrayList<BlockPos>()
        var depth = 1
        while(possibilities.isEmpty()) {
            for(y in 1 downTo -depth) {
                for(x in -4..4) {
                    for(z in -4..4) {
                        if(x == 0 && y == 0 && z == 0) continue
                        val pos = BlockPos(last.x + x, last.y + y, last.z + z)
                        if(path.any { PathFinder.findPath(Vec3d.ofCenter(it).add(0.0, 0.5, 0.0), Vec3d.ofCenter(pos).add(0.0, 0.5, 0.0), Movement.WALKABLE) != null }) continue
                        if(path.contains(pos)) continue
                        val blockState = MinecraftClient.getInstance().world?.getBlockState(pos)
                        val collision = blockState?.getCollisionShape(MinecraftClient.getInstance().world, pos)
                        if(collision?.isEmpty!!) continue
                        if(collision.boundingBox?.maxY!! > 1.0) continue
                        if(!MinecraftClient.getInstance().world?.isAir(pos)!!) {
                            var possiblePosition = true
                            for(yOffset in 1..2) {
                                if(!MinecraftClient.getInstance().world?.isAir(pos.add(0, yOffset, 0))!!) {
                                    possiblePosition = false
                                    break
                                }
                            }

                            if(!possiblePosition)
                                continue

                            possibilities.add(pos)
                        }
                    }
                }
            }

            depth++
        }

        possibilities.sortBy { Vec3d.of(it.subtract(Vec3i(last.x, last.y, last.z))).lengthSquared() }

        return possibilities.minWithOrNull(goal.comparator)
    }

    fun computePath(steps: Int): Boolean {
        for(step in 1..steps) {
            var blockPos: BlockPos?
            if(compute().also { blockPos = it } != null) {
                path.add(blockPos!!)
            } else {
                for((nodeIndex, node) in path.withIndex()) {
                    for((goalIndex, goal) in path.reversed().withIndex()) {
                        if(goalIndex > nodeIndex) {
                            val astarPath = PathFinder.findPath(Vec3d.ofCenter(node).add(0.0, 0.5, 0.0), Vec3d.ofCenter(goal).add(0.0, 0.5, 0.0), Movement.WALKABLE)
                            if(astarPath != null) {
                                for(pos in nodeIndex..goalIndex) {
                                    path.removeAt(pos)
                                }
                                path.addAll(nodeIndex, astarPath.map { vec3d -> BlockPos(vec3d.x, vec3d.y, vec3d.z) })
                            }
                        }
                    }
                }
                return true
            }
        }
        return false
    }

}

enum class Goal(val comparator: Comparator<BlockPos>) {
    FORWARDS(Comparator.comparing { MathHelper.wrapDegrees( RotationUtil.getRotations(MinecraftClient.getInstance().player?.eyePos!!, Vec3d.ofCenter(it)).yaw - TarasandeMain.get().parkourBot?.startRotation?.yaw!!) }),
    UPWARDS(Comparator.comparing<BlockPos?, Int?> { it.y }.reversed()),
    DOWNWARDS(Comparator.comparing { it.y })
}
