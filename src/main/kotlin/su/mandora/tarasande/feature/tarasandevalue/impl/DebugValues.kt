package su.mandora.tarasande.feature.tarasandevalue.impl

import com.mojang.blaze3d.platform.GlConst
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventPacket
import su.mandora.tarasande.event.impl.EventRender3D
import su.mandora.tarasande.feature.tarasandevalue.impl.debug.Chat
import su.mandora.tarasande.feature.tarasandevalue.impl.debug.MinecraftDebugger
import su.mandora.tarasande.feature.tarasandevalue.impl.debug.PlayerMovementPrediction
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.base.valuesystem.impl.meta.abstracted.ValueButtonOwnerValues
import su.mandora.tarasande.util.opLevels

object DebugValues {

    init {
        ValueButtonOwnerValues(this, "Minecraft debugger", MinecraftDebugger)
        ValueButtonOwnerValues(this, "Player movement prediction", PlayerMovementPrediction)
    }

    val openGLErrorDebugger = ValueBoolean(this, "OpenGL error debugger", true)
    val disableInterpolation = ValueBoolean(this, "Disable interpolation", false)
    private val ignoreResourcePackHash = ValueBoolean(this, "Ignore resource pack hash", false)
    val eliminateHitDelay = ValueBoolean(this, "Eliminate hit delay", false)
    val chat = ValueButtonOwnerValues(this, "Chat", Chat)
    val forcePermissionLevel = ValueBoolean(this, "Force permission level", false)

    val permissionLevel = ValueNumber(this, "Permission level", opLevels.first.toDouble(), opLevels.last.toDouble(), opLevels.last.toDouble(), 1.0, isEnabled = { forcePermissionLevel.value })
    val alwaysAllowToOpenCommandBlocks = ValueBoolean(this, "Always allow to open command blocks", false)
    val visualizeSlotIds = ValueBoolean(this, "Visualize slot ids", false)
    val forceCreativeInventory = ValueBoolean(this, "Force creative inventory", false)
    private val wireframe = ValueBoolean(this, "Wireframe", false)
    val showAllPlayerEntries = ValueBoolean(this, "Show all player entries", false)

    init {
        EventDispatcher.apply {
            var enabledWireframe = false
            add(EventRender3D::class.java) { event ->
                when(event.state) {
                    EventRender3D.State.PRE -> {
                        if(wireframe.value) {
                            RenderSystem.polygonMode(GlConst.GL_FRONT_AND_BACK, GlConst.GL_LINE)
                            enabledWireframe = true
                        }
                    }

                    EventRender3D.State.POST -> {
                        if(enabledWireframe) {
                            RenderSystem.polygonMode(GlConst.GL_FRONT_AND_BACK, GlConst.GL_FILL)
                            enabledWireframe = false
                        }
                    }
                }
            }
            add(EventPacket::class.java) { event ->
                if (!ignoreResourcePackHash.value) return@add

                if (event.type == EventPacket.Type.RECEIVE && event.packet is ResourcePackSendS2CPacket) {
                    event.packet.hash = "" // The client ignores the hash if it is not 40 characters long
                }
            }
        }
    }
}
