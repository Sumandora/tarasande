package su.mandora.tarasande.util.math.pathfinder

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.util.extension.minecraft.math.BlockPos
import su.mandora.tarasande.util.player.PlayerUtil

class Teleporter(owner: Any) {

    private val method = ValueMode(owner, "Method", false, "Path", "Clip")
    private val maxDistance = ValueNumber(owner, "Max distance", 1.0, 8.0, 10.0, 0.1, isEnabled = { method.isSelected(0) })
    private val ground = ValueMode(owner, "Ground", false, "Always on ground", "Always off ground", "Approximate ground")

    companion object {
        private fun isPassable(pos: BlockPos): Boolean {
            return mc.world?.getBlockState(pos)?.getCollisionShape(mc.world, pos).let { it == null || it.isEmpty }
        }

        fun canStand(pos: BlockPos): Boolean {
            return isPassable(pos) && isPassable(pos.add(0, 1, 0))
        }

        private val pathFinder = PathFinder({ _, node -> canStand(BlockPos(node.x, node.y, node.z)) })
    }

    enum class Method {
        PATH, CLIP
    }

    fun teleportToPosition(blockPos: BlockPos, timeout: Long = 1000L, methodOverride: Method = Method.entries[method.values.indexOf(method.getSelected())]): List<Vec3d>? {
        @Suppress("NAME_SHADOWING")

        fun moveUp(pos: BlockPos): BlockPos {
            var pos = pos
            while (!canStand(pos))
                pos = pos.add(0, 1, 0)
            return pos
        }

        val start = moveUp(mc.player!!.blockPos)
        val target = moveUp(blockPos)

        val path =
            when (methodOverride) {
                Method.PATH -> pathFinder.findPath(Vec3d.of(start), Vec3d.of(target), timeout)?.also { optimizePath(it) }
                Method.CLIP -> listOf(target.toCenterPos().add(0.0, -0.5, 0.0))
            } ?: return null

        for (vec in path) {
            mc.networkHandler?.sendPacket(PlayerMoveC2SPacket.PositionAndOnGround(vec.x, vec.y, vec.z, isOnGround(BlockPos(vec))))
            mc.player?.setPosition(vec)
        }
        PlayerUtil.updateLastPosition()
        return path
    }

    private fun optimizePath(path: ArrayList<Vec3d>) {
        val iterator = path.iterator()

        if (iterator.hasNext()) {
            iterator.next()
            iterator.remove() // Remove the first position, because that is what we are standing on.
        }

        var previous: Vec3d? = null
        while (iterator.hasNext()) {
            val current = iterator.next()
            if (!iterator.hasNext())
                return // Leave the last position standing no matter what, we want to get there and not just a position which is really close to it

            if (previous != null) {
                if (PlayerUtil.canVectorBeSeen(previous, current) && previous.squaredDistanceTo(current) < maxDistance.value * maxDistance.value) {
                    iterator.remove()
                    continue
                }
            }
            previous = current
        }
    }

    private fun isOnGround(blockPos: BlockPos): Boolean {
        return when {
            ground.isSelected(0) -> true
            ground.isSelected(1) -> false
            ground.isSelected(2) -> {
                val blockState = mc.world?.getBlockState(blockPos)
                val collisionShape = blockState?.getCollisionShape(mc.world, blockPos)
                collisionShape == null || collisionShape.isEmpty
            }
            else -> error("Invalid state")
        }
    }

}