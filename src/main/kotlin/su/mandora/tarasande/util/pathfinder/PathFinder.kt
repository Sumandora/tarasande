package su.mandora.tarasande.util.pathfinder

import net.minecraft.client.MinecraftClient
import net.minecraft.client.world.ClientWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import kotlin.math.abs

/**
 * A* Path Finding algorithm
 */

private val manhattan = object : Function2<Node, Node, Double> {
    override fun invoke(current: Node, target: Node): Double {
        val delta = target - current
        return (abs(delta.x) + abs(delta.y) + abs(delta.z)).toDouble()
    }
}

private val oneCost = object : Function2<Node, Node, Double> {
    override fun invoke(start: Node, movement: Node): Double {
        //return manhattan.invoke(start, movement)
        return 1.0
    }
}

private val walkable = object : Function2<ClientWorld?, Node, Boolean> {
    override fun invoke(world: ClientWorld?, node: Node): Boolean {
        return world!!.isAir(BlockPos(node.x, node.y, node.z)) && !world.isAir(BlockPos(node.x, node.y - 1, node.z))
    }
}

private val directions = arrayOf(
    Node(1, 0, 0),
    Node(-1, 0, 0),
    Node(0, 0, 1),
    Node(0, 0, -1),
    Node(0, 1, 0),
    Node(0, -1, 0)
)

class PathFinder(private val allowedBlock: Function2<ClientWorld?, Node, Boolean> = walkable, private val heuristic: Function2<Node, Node, Double> = manhattan, private val cost: Function2<Node, Node, Double> = oneCost) {

    fun findPath(start: Vec3d, target: Vec3d, maxTime: Long? = null): List<Vec3d>? {
        val mappedPath = ArrayList<Vec3d>()
        val start = BlockPos(start)
        val target = BlockPos(target)
        val path = findPath(Node(start.x, start.y, start.z), Node(target.x, target.y, target.z), maxTime) ?: return null
        for (vec in path)
            mappedPath.add(Vec3d(vec.x + 0.5, vec.y.toDouble(), vec.z + 0.5))
        return mappedPath
    }

    fun findPath(start: Node, target: Node, maxTime: Long? = null): List<Node>? {
        val open = HashSet<Node>()
        val closed = HashSet<Node>()
        open.add(start)

        if (start == target || !allowedBlock.invoke(MinecraftClient.getInstance().world, start) || !allowedBlock.invoke(MinecraftClient.getInstance().world, target)) {
            return open.toList()
        }

        var bestNode: Node? = null
        var current: Node?
        val begin = System.currentTimeMillis()
        while ((maxTime == null || System.currentTimeMillis() - begin <= maxTime) && open.isNotEmpty()) {
            current = open.minBy(Node::f)

            if (current == target) {
                break
            }

            open.remove(current)
            closed.add(current)

            for (movementPossibility in directions) {
                val movement = current + movementPossibility
                if (closed.contains(movement))
                    continue
                if (!allowedBlock.invoke(MinecraftClient.getInstance().world, movement))
                    continue

                open.add(movement)

                movement.g = current.g + cost.invoke(start, movement)
                movement.h = heuristic.invoke(movement, target)

                if (bestNode == null || bestNode.h > movement.h)
                    bestNode = movement

                movement.f = movement.g + movement.h
                movement.parent = current
            }
        }

        return reconstructPath(bestNode ?: return null)
    }

    private fun reconstructPath(current: Node): ArrayList<Node> {
        var current: Node? = current
        val path = ArrayList<Node>()
        path.clear()
        while (current != null) {
            path.add(current)
            current = current.parent
        }
        return ArrayList(path.reversed())
    }
}


class Node(var x: Int, var y: Int, var z: Int) : Comparable<Node> {
    var g = 0.0
    var h = 0.0
    var f = 0.0
    var parent: Node? = null

    operator fun plus(other: Node): Node {
        return Node(this.x + other.x, this.y + other.y, this.z + other.z)
    }

    operator fun minus(other: Node): Node {
        return Node(x - other.x, y - other.y, z - other.z)
    }

    override fun compareTo(other: Node) = f.compareTo(other.f)

    override fun equals(other: Any?): Boolean {
        return other is Node && other.x == x && other.y == y && other.z == z
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        result = 31 * result + z
        return result
    }
}