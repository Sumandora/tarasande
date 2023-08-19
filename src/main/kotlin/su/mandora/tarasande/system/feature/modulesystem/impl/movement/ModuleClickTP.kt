package su.mandora.tarasande.system.feature.modulesystem.impl.movement

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.minecraft.command.CommandSource
import net.minecraft.command.argument.BlockPosArgumentType
import net.minecraft.command.argument.PosArgument
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import org.lwjgl.glfw.GLFW
import su.mandora.tarasande.event.impl.EventRender3D
import su.mandora.tarasande.event.impl.EventUpdate
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBind
import su.mandora.tarasande.system.feature.commandsystem.Command
import su.mandora.tarasande.system.feature.commandsystem.ManagerCommand
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.util.extension.javaruntime.withAlpha
import su.mandora.tarasande.util.extension.minecraft.math.BlockPos
import su.mandora.tarasande.util.math.pathfinder.Teleporter
import su.mandora.tarasande.util.math.rotation.Rotation
import su.mandora.tarasande.util.player.PlayerUtil
import su.mandora.tarasande.util.player.chat.CustomChat
import su.mandora.tarasande.util.render.RenderUtil
import java.awt.Color

class ModuleClickTP : Module("Click tp", "Teleports you to the position you click at", ModuleCategory.MOVEMENT) {

    private val teleportKey = ValueBind(this, "Teleport key", ValueBind.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_2)

    private val teleporter = Teleporter(this)

    private var path: List<Vec3d>? = null
    private var goal: BlockPos? = null

    init {
        ManagerCommand.apply {
            add(object : Command("teleport", "tp") {
                override fun builder(builder: LiteralArgumentBuilder<CommandSource>) {
                    builder.then(argument("position", BlockPosArgumentType.blockPos())!!.executes {
                        if(teleporter.teleportToPosition(it.getArgument("position", PosArgument::class.java).toAbsoluteBlockPos(createServerCommandSource()), methodOverride = Teleporter.Method.PATH) == null) {
                            CustomChat.printChatMessage("Could not find a path to that position")
                            return@executes ERROR
                        }
                        return@executes SUCCESS
                    })
                }
            })
            add(object : Command("clip") {
                override fun builder(builder: LiteralArgumentBuilder<CommandSource>) {
                    builder.then(argument("position", BlockPosArgumentType.blockPos())?.executes {
                        teleporter.teleportToPosition(it.getArgument("position", PosArgument::class.java).toAbsoluteBlockPos(createServerCommandSource()), methodOverride = Teleporter.Method.CLIP)
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
        registerEvent(EventUpdate::class.java) { event ->
            if (event.state == EventUpdate.State.PRE) {
                repeat(teleportKey.wasPressed()) {
                    val hitResult = PlayerUtil.getTargetedEntity(mc.gameRenderer.farPlaneDistance.toDouble(), Rotation(mc.player!!), false)
                    if (hitResult != null) {
                        val blockPos =
                            if (hitResult is BlockHitResult)
                                hitResult.blockPos
                            else
                                BlockPos(hitResult.pos)
                        if (blockPos == null)
                            return@registerEvent

                        path = teleporter.teleportToPosition(blockPos)
                        goal = blockPos
                    }
                }
            }
        }

        registerEvent(EventRender3D::class.java) { event ->
            if(event.state != EventRender3D.State.POST) return@registerEvent

            if (goal != null) {
                RenderUtil.blockOutline(event.matrices, Box(goal!!), Color.white.withAlpha(50).rgb)
            }
            RenderUtil.renderPath(event.matrices, path ?: return@registerEvent, -1)
        }
    }

}