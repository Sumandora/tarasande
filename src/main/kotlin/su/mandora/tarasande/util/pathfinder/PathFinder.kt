package su.mandora.tarasande.util.pathfinder

import net.minecraft.client.MinecraftClient
import net.minecraft.client.world.ClientWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import kotlin.math.abs
import kotlin.math.round
import java.util.function.Function as JavaFunction

/**
 * A* Path Finding algorithm
 */

object PathFinder {

    private val manhattan = object : Function2<Node, Node, Double> {
        override fun invoke(current: Node, target: Node): Double {
            val delta = target.subtract(current)
            return (abs(delta.x) + abs(delta.y) + abs(delta.z)).toDouble()
        }
    }

    private val oneCost = object : Function2<Node, Node, Double> {
        override fun invoke(current: Node, movement: Node): Double {
            return 1.0
        }
    }

    private val allowedBlock = object : Function2<ClientWorld?, Node, Boolean> {
        override fun invoke(world: ClientWorld?, node: Node): Boolean {
            return world?.isAir(BlockPos(node.x, node.y, node.z))!! && !world.isAir(BlockPos(node.x, node.y - 1, node.z))
        }
    }

    private val never = JavaFunction<Node, Boolean> { false }

    fun findPath(start: Vec3d, target: Vec3d, allowedBlock: Function2<ClientWorld?, Node, Boolean> = this.allowedBlock, heuristic: Function2<Node, Node, Double> = manhattan, cost: Function2<Node, Node, Double> = oneCost, maxTime: Long = 0L, abort: JavaFunction<Node, Boolean> = never): ArrayList<Vec3d>? {
        val mappedPath = ArrayList<Vec3d>()
        val path = findPath(Node(round(start.x).toInt(), round(start.y).toInt(), round(start.z).toInt()), Node(round(target.x).toInt(), round(target.y).toInt(), round(target.z).toInt()), allowedBlock, heuristic, cost, maxTime, abort) ?: return null
        for (vec in path) mappedPath.add(Vec3d(vec.x + 0.5, vec.y + 0.5, vec.z + 0.5))
        return mappedPath
    }

    fun findPath(start: Node, target: Node, allowedBlock: Function2<ClientWorld?, Node, Boolean> = this.allowedBlock, heuristic: Function2<Node, Node, Double> = manhattan, cost: Function2<Node, Node, Double> = oneCost, maxTime: Long = 0L, abort: JavaFunction<Node, Boolean> = never): ArrayList<Node>? {
        start.g = cost(start, start)
        start.h = heuristic(start, start)
        start.f = start.g + start.h
        val open = ArrayList<Node>()
        val closed = ArrayList<Node>()
        open.add(start)

        if (start == target || abort.apply(start)) {
            return open
        }

        var current: Node? = null
        val begin = System.currentTimeMillis()
        while ((maxTime == 0L || System.currentTimeMillis() - begin <= maxTime) && open.isNotEmpty()) {
            current = null
            for (move in open) {
                if (current == null || move.f < current.f) {
                    current = move
                }
            }

            if (current == null || current == target || abort.apply(current)) {
                break
            }

            open.remove(current)
            closed.add(current)

            for (movementPossibility in generateMovementPossibilities(current, MinecraftClient.getInstance().world, allowedBlock)) {
                if (closed.contains(movementPossibility)) {
                    continue
                }
                var movementPossibility = movementPossibility
                // hacky fix because we don't have a set grid size
                for (movementPossibility2 in open) {
                    if (movementPossibility2 == movementPossibility) movementPossibility = movementPossibility2
                }

                val tempG = current.g + cost.invoke(current, movementPossibility)

                var newPath = false
                if (open.contains(movementPossibility)) {
                    if (tempG < movementPossibility.g) {
                        newPath = true
                    }
                } else {
                    newPath = true
                    open.add(movementPossibility)
                }

                if (newPath) {
                    movementPossibility.g = tempG
                    movementPossibility.h = heuristic.invoke(movementPossibility, target)
                    movementPossibility.f = movementPossibility.g + movementPossibility.h
                    movementPossibility.parent = current
                }
            }
        }

        return /*if (current == null) null else*/ reconstructPath(current ?: return null)
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

    private fun generateMovementPossibilities(position: Node, world: ClientWorld?, allowedBlock: Function2<ClientWorld?, Node, Boolean>): List<Node> {
        val list = ArrayList<Node>()
        for (x in -1..1 step 2) list.add(position.add(x, 0, 0))
        for (y in -1..1 step 2) list.add(position.add(0, y, 0))
        for (z in -1..1 step 2) list.add(position.add(0, 0, z))
        list.removeIf { !allowedBlock.invoke(world, it) }
        return list
    }
}


class Node(var x: Int, var y: Int, var z: Int) {
    var g = 0.0
    var h = 0.0
    var f = 0.0
    var parent: Node? = null

    fun add(x: Int, y: Int, z: Int): Node {
        return Node(this.x + x, this.y + y, this.z + z)
    }

    fun subtract(other: Node): Node {
        return Node(other.x - x, other.y - y, other.z - z)
    }

    override fun equals(other: Any?): Boolean {
        return other is Node && other.x == x && other.y == y && other.z == z
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + z.hashCode()
        return result
    }
}