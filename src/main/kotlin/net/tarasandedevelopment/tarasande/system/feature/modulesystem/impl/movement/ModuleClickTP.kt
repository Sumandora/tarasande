package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.movement

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.minecraft.command.CommandSource
import net.minecraft.command.argument.BlockPosArgumentType
import net.minecraft.command.argument.PosArgument
import net.minecraft.command.argument.Vec3ArgumentType
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.event.EventMouse
import net.tarasandedevelopment.tarasande.event.EventRender3D
import net.tarasandedevelopment.tarasande.system.feature.commandsystem.Command
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory
import net.tarasandedevelopment.tarasande.util.extension.javaruntime.withAlpha
import net.tarasandedevelopment.tarasande.util.extension.mc
import net.tarasandedevelopment.tarasande.util.extension.minecraft.Box
import net.tarasandedevelopment.tarasande.util.math.pathfinder.PathFinder
import net.tarasandedevelopment.tarasande.util.math.rotation.Rotation
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import org.lwjgl.glfw.GLFW
import java.awt.Color

class ModuleClickTP : Module("Click tp", "Teleports you to the position you click at", ModuleCategory.MOVEMENT) {

    private fun isPassable(pos: BlockPos): Boolean {
        return mc.world?.getBlockState(pos)?.getCollisionShape(mc.world, pos).let { it == null || it.isEmpty }
    }

    private val pathFinder = PathFinder({ _, node -> isPassable(BlockPos(node.x, node.y, node.z)) && isPassable(BlockPos(node.x, node.y + 1, node.z)) })

    private var path: List<Vec3d>? = null
    private var goal: BlockPos? = null

    init {
        TarasandeMain.managerCommand().apply {
            add(object : Command("teleport", "tp") {
                override fun builder(builder: LiteralArgumentBuilder<CommandSource>): LiteralArgumentBuilder<CommandSource> {
                    return builder.then(argument("position", BlockPosArgumentType.blockPos())?.executes {
                        teleportToPosition(it.getArgument("position", PosArgument::class.java).toAbsoluteBlockPos(createServerCommandSource()))
                        return@executes SUCCESS
                    })
                }
            })
            add(object : Command("clip") {
                override fun builder(builder: LiteralArgumentBuilder<CommandSource>): LiteralArgumentBuilder<CommandSource> {
                    return builder.then(argument("position", Vec3ArgumentType.vec3())?.executes {
                        mc.player?.setPosition(it.getArgument("position", PosArgument::class.java).toAbsolutePos(createServerCommandSource()))
                        return@executes SUCCESS
                    })
                }
            })
        }
    }

    override fun onDisable() {
        path = null
        goal = null
    }

    init {
        registerEvent(EventMouse::class.java) { event ->
            if (event.action == GLFW.GLFW_PRESS && event.button == 1 && mc.currentScreen == null) {
                val hitResult = PlayerUtil.getTargetedEntity(mc.options.viewDistance.value * 16.0, Rotation(mc.player!!), false)
                if (hitResult != null) {
                    val blockPos =
                        if (hitResult is BlockHitResult)
                            hitResult.blockPos
                        else
                            BlockPos(hitResult.pos)
                    if (blockPos == null)
                        return@registerEvent

                    path = teleportToPosition(blockPos)
                    goal = blockPos
                }
            }
        }

        registerEvent(EventRender3D::class.java) { event ->
            if (goal != null) {
                val goal = goal!!
                RenderUtil.blockOutline(event.matrices, Box().offset(goal.x.toDouble(), goal.y.toDouble(), goal.z.toDouble()), Color.white.withAlpha(50).rgb)
            }
            RenderUtil.renderPath(event.matrices, path ?: return@registerEvent, -1)
        }
    }

    fun teleportToPosition(blockPos: BlockPos, timeout: Long = 1000L): List<Vec3d>? {
        @Suppress("NAME_SHADOWING")
        var blockPos = blockPos

        while (!isPassable(blockPos))
            blockPos = blockPos.add(0, 1, 0)

        val path = pathFinder.findPath(mc.player?.pos!!, Vec3d.of(blockPos), timeout) ?: return null

        optimizePath(path)

        for (vec in path) {
            mc.networkHandler?.sendPacket(PositionAndOnGround(vec.x, vec.y, vec.z, false))
            mc.player?.setPosition(vec)
        }
        return path
    }

    private fun optimizePath(path: ArrayList<Vec3d>) {
        val iterator = path.iterator()
        var previous: Vec3d? = null
        while(iterator.hasNext()) {
            val current = iterator.next()
            if(!iterator.hasNext())
                return
            if(previous != null) {
                if(PlayerUtil.canVectorBeSeen(previous, current) && previous.squaredDistanceTo(current) < 6.0 * 6.0) {
                    iterator.remove()
                    continue
                }
            }
            previous = current
        }
    }

}