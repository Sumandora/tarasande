package su.mandora.tarasande.util.pathfinder

import net.minecraft.client.MinecraftClient
import net.minecraft.client.world.ClientWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import kotlin.math.abs
import kotlin.math.round

/**
 * A* Path Finding algorithm
 */

object PathFinder {

	fun findPath(start: Vec3d, target: Vec3d, movement: Movement): List<Vec3d>? {
		val mappedPath = ArrayList<Vec3d>()
		val path = findPath(Node(round(start.x).toInt(), round(start.y).toInt(), round(start.z).toInt()), Node(round(target.x).toInt(), round(target.y).toInt(), round(target.z).toInt()), movement) ?: return null
		for (vec in path)
			mappedPath.add(Vec3d(vec.x + 0.5, vec.y + 0.5, vec.z + 0.5))
		return mappedPath
	}

	private fun findPath(start: Node, target: Node, movement: Movement): List<Node>? {
		val open = ArrayList<Node>()
		val closed = ArrayList<Node>()
		open.add(start)

		if (start == target) {
			return open
		}

		var current: Node? = null
		val startTime = System.currentTimeMillis()
		while (System.currentTimeMillis() - startTime <= 20 && open.isNotEmpty()) {
			var best = Double.POSITIVE_INFINITY // hacky but works
			current = null
			for (move in open) {
				if (current == null || move.f < best) {
					best = move.f
					current = move
				}
			}

			if (current == null || current == target) {
				break
			}

			open.remove(current)
			closed.add(current)

			for (movementPossibility in generateMovementPossibilities(current, MinecraftClient.getInstance().world, movement)) {
				if (closed.contains(movementPossibility)) {
					continue
				}
				var movementPossibility = movementPossibility
				// hacky fix because we don't have a set grid size
				for (movementPossibility2 in open) {
					if (movementPossibility2 == movementPossibility)
						movementPossibility = movementPossibility2
				}

				val tempG = current.g + 1

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
					movementPossibility.h = manhattanDistance(movementPossibility, target)
					movementPossibility.f = movementPossibility.g + movementPossibility.h
					movementPossibility.parent = current
				}
			}
		}

		return if (current != target) null else reconstructPath(current)
	}

	private fun manhattanDistance(start: Node, target: Node): Double {
		val delta = target.subtract(start)
		return (abs(delta.x) + abs(delta.y) + abs(delta.z)).toDouble()
	}

	private fun reconstructPath(current: Node): List<Node> {
		var current: Node? = current
		val path = ArrayList<Node>()
		path.clear()
		while (current?.parent != null) {
			path.add(current)
			current = current.parent
		}
		return path.reversed()
	}

	private fun generateMovementPossibilities(position: Node, world: ClientWorld?, movement: Movement): List<Node> {
		val list = ArrayList<Node>()
		when (movement) {
			Movement.NEIGHBORS -> {
				for (x in -1..1)
					for (y in -1..1)
						for (z in -1..1) {
							val pos = position.add(x, y, z)
							var valid = true
							for (yOffset in 0..1)
								if (!world?.isAir(BlockPos(pos.x, pos.y + yOffset, pos.z))!!)
									valid = false

							if (valid)
								list.add(pos)
						}
			}
			Movement.WALKABLE -> {
				for (x in -1..1)
					for (y in -1..1)
						for (z in -1..1) {
							val pos = position.add(x, y, z)
							var valid = true
							for (yOffset in 0..1)
								if (!world?.isAir(BlockPos(pos.x, pos.y + yOffset, pos.z))!!)
									valid = false

							if (world?.isAir(BlockPos(pos.x, pos.y - 1, pos.z))!!)
								valid = false

							if (valid)
								list.add(pos)
						}
			}
		}
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
		return Node(
			other.x - x,
			other.y - y,
			other.z - z
		)
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

enum class Movement {
	NEIGHBORS, WALKABLE
}