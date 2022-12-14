package net.tarasandedevelopment.tarasande.util.math.pathfinder

import net.minecraft.client.MinecraftClient
import net.minecraft.client.world.ClientWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import java.util.*
import java.util.function.BiFunction
import kotlin.math.abs

/**
 * A* Path Finding algorithm
 */

private val manhattan = BiFunction<Node, Node, Double> { current, target ->
    val delta = target - current
    // Ok, so I have no clue where igs found that 2.5, but it makes this way faster, so.... Thank you, I guess ^^
    // Me from the future here: This number is bullshit, on one side it makes this way faster, on the other side a star seems to work less optimal because of it
    2.5 * (abs(delta.x) + abs(delta.y) + abs(delta.z)).toDouble()
}

private val oneCost = BiFunction<Node, Node, Double> { _, _ ->
    1.0
}

private val walkable = BiFunction<ClientWorld?, Node, Boolean> { world, node ->
    world!!.isAir(BlockPos(node.x, node.y, node.z)) && !world.isAir(BlockPos(node.x, node.y - 1, node.z))
}

private val directions = arrayOf(
    Node(1, 0, 0),
    Node(-1, 0, 0),
    Node(0, 0, 1),
    Node(0, 0, -1),
    Node(0, 1, 0),
    Node(0, -1, 0)
)

class PathFinder(private val allowedBlock: BiFunction<ClientWorld?, Node, Boolean> = walkable, private val heuristic: BiFunction<Node, Node, Double> = manhattan, private val cost: BiFunction<Node, Node, Double> = oneCost) {

    fun findPath(start: Vec3d, target: Vec3d, maxTime: Long? = null): List<Vec3d>? {
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
        val open = TreeSet<Node>(Comparator.comparing { it.f })
        val closed = HashSet<Int>()
        start.g = cost.apply(start, start)
        start.h = heuristic.apply(start, target)
        start.f = start.g + start.h
        open.add(start)

        if (start == target || !allowedBlock.apply(MinecraftClient.getInstance().world, start) || !allowedBlock.apply(MinecraftClient.getInstance().world, target)) {
            return Collections.singletonList(start)
        }

        var current: Node? = null
        val begin = System.currentTimeMillis()
        while ((maxTime == null || System.currentTimeMillis() - begin <= maxTime) && open.isNotEmpty()) {
            current = open.pollFirst()
            if (current == target) {
                break
            }

            closed.add(current.hashCode())

            for (movementPossibility in directions) {
                val movement = current + movementPossibility

                if (closed.contains(movement.hashCode()))
                    continue

                if (!allowedBlock.apply(MinecraftClient.getInstance().world, movement))
                    continue

                val newCost = current.g + cost.apply(start, movement)

                movement.g = newCost
                movement.h = heuristic.apply(movement, target)
                movement.f = movement.g + movement.h
                movement.parent = current

                open.add(movement)
            }
        }

        return reconstructPath(current ?: return null)
    }

    private fun reconstructPath(current: Node): ArrayList<Node> {
        @Suppress("NAME_SHADOWING")
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
        return Node(other.x - x, other.y - y, other.z - z)
    }

    override fun compareTo(other: Node) = f.compareTo(other.f)

    override fun equals(other: Any?): Boolean {
        return other is Node && other.x == x && other.y == y && other.z == z
    }

    override fun hashCode(): Int {
        val cantor = { a: Int, b: Int ->
            (a + b + 1) * (a + b) / 2 + b
        }
        return cantor(x, cantor(y, z))
    }
}