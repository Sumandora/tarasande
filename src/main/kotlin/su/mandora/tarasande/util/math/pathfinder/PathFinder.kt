package su.mandora.tarasande.util.math.pathfinder

import net.minecraft.client.world.ClientWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import su.mandora.tarasande.mc
import su.mandora.tarasande.util.extension.minecraft.BlockPos
import java.util.*
import kotlin.math.abs

/**
 * A* Path Finding algorithm
 */

private val manhattan: (Node, Node) -> Double = { current, target ->
    val delta = target - current
    // Ok, so I have no clue where igs found that 2.5, but it makes this way faster, so.... Thank you, I guess ^^
    // Me from the future here: On one side it makes this way faster, on the other side a star seems to work less optimal because of it
    2.5 * (abs(delta.x) + abs(delta.y) + abs(delta.z)).toDouble()
}

private val oneCost: (Node, Node) -> Double = { _, _ ->
    1.0
}

private val walkable: (ClientWorld?, Node) -> Boolean = { world, node ->
    world!!.isAir(BlockPos(node.x, node.y, node.z)) && !world.isAir(BlockPos(node.x, node.y - 1, node.z))
}

private val cardinalDirections = arrayOf(
    Node(1, 0, 0),
    Node(-1, 0, 0),
    Node(0, 0, 1),
    Node(0, 0, -1),
    Node(0, 1, 0),
    Node(0, -1, 0)
)

class PathFinder(
    private val allowedBlock: (ClientWorld?, Node) -> Boolean = walkable,
    private val heuristic: (Node, Node) -> Double = manhattan,
    private val cost: (Node, Node) -> Double = oneCost,
    private val directions: Array<Node> = cardinalDirections
) {

    fun findPath(start: Vec3d, target: Vec3d, maxTime: Long? = null): ArrayList<Vec3d>? {
        val mappedPath = ArrayList<Vec3d>()

        @Suppress("NAME_SHADOWING")
        val start = BlockPos(start)

        @Suppress("NAME_SHADOWING")
        val target = BlockPos(target)
        val path = findPath(Node(start.x, start.y, start.z), Node(target.x, target.y, target.z), maxTime) ?: return null
        for (vec in path)
            mappedPath.add(Vec3d(vec.x + 0.5, vec.y.toDouble(), vec.z + 0.5))
        return mappedPath
    }

    fun findPath(start: Node, target: Node, maxTime: Long? = null): List<Node>? {
        val open = HashMap<Int, Node>()
        val closed = HashSet<Int>()
        open[start.hashCode()] = start
        start.g = cost(start, start)
        start.h = heuristic(start, target)
        start.f = start.g + start.h

        if (start == target || !allowedBlock(mc.world, start) || !allowedBlock(mc.world, target)) {
            return open.values.toList()
        }

        var bestNode: Node? = null
        var current: Node?
        val begin = System.currentTimeMillis()
        while ((maxTime == null || System.currentTimeMillis() - begin <= maxTime) && open.isNotEmpty()) {
            val lowest = open.minBy { entry -> entry.value.f }
            open.remove(lowest.key)

            current = lowest.value
            if (current == target) {
                break
            }

            closed.add(current.hashCode())

            for (movementPossibility in directions) {
                var movement = current + movementPossibility

                if (closed.contains(movement.hashCode()))
                    continue

                var contains = true
                movement = open[movement.hashCode()] ?: run { contains = false; movement }

                if (!allowedBlock(mc.world, movement))
                    continue

                val newCost = current.g + cost(start, movement)

                if (!contains)
                    open[movement.hashCode()] = movement
                else if (movement.g <= newCost)
                    continue

                movement.g = newCost
                movement.h = heuristic(movement, target)

                if (bestNode == null || bestNode.h > movement.h)
                    bestNode = movement

                movement.f = movement.g + movement.h
                movement.parent = current
            }
        }

        return reconstructPath(bestNode ?: return null)
    }

    private fun reconstructPath(current: Node): ArrayList<Node> {
        @Suppress("NAME_SHADOWING")
        var current: Node? = current
        val path = ArrayList<Node>()
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
        return Node(other.x - x, other.y - y, other.z - z)
    }

    override fun compareTo(other: Node) = f.compareTo(other.f)

    override fun equals(other: Any?): Boolean {
        return other is Node && other.x == x && other.y == y && other.z == z
    }

    override fun hashCode(): Int {
        val xComp = x and 4095
        val yComp = y and 255 shl 12
        val zComp = z and 4095 shl 20

        return xComp or yComp or zComp
    }
}