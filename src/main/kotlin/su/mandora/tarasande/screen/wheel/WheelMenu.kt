package su.mandora.tarasande.screen.wheel

import net.minecraft.client.MinecraftClient
import net.minecraft.util.math.Vec2f
import org.lwjgl.glfw.GLFW
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.base.event.Event
import su.mandora.tarasande.base.screen.wheel.wheeltree.ManagerWheelTree
import su.mandora.tarasande.base.screen.wheel.wheeltree.WheelTreeEntry
import su.mandora.tarasande.base.screen.wheel.wheeltree.WheelTreeRunnable
import su.mandora.tarasande.base.screen.wheel.wheeltree.WheelTreeSubMenu
import su.mandora.tarasande.event.*
import su.mandora.tarasande.util.math.rotation.Rotation
import su.mandora.tarasande.util.render.RenderUtil
import java.util.function.Consumer
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class WheelMenu {

    var active: Boolean = false
    private var ticksOpen = 0
    private var cursorX = 0.0
    private var cursorY = 0.0

    private val managerWheelTree = ManagerWheelTree()
    private var wheelTreeEntry: WheelTreeEntry? = null
    private var wheelTreeEntries = managerWheelTree.getEntries(wheelTreeEntry)!!

    init {
        TarasandeMain.get().managerEvent?.add(Pair(1001, Consumer<Event> { event ->
            if(event is EventMouse) {
                if(ticksOpen > 0) {
                    event.setCancelled()
                    if(event.button != GLFW.GLFW_MOUSE_BUTTON_MIDDLE) {
                        onClose()
                    } else {
                        val radius = min(MinecraftClient.getInstance().window.scaledWidth, MinecraftClient.getInstance().window.scaledHeight) * 0.25

                        var closest: WheelTreeEntry? = null
                        var closestDist = 0.0
                        for((index, wheelTreeEntry) in wheelTreeEntries.withIndex()) {
                            val stringWidth = MinecraftClient.getInstance().textRenderer.getWidth(wheelTreeEntry.name)

                            val dir = index.toDouble() * 2.0 * Math.PI / wheelTreeEntries.size

                            val x = sin(dir) * (radius - stringWidth)
                            val y = cos(dir) * (radius - stringWidth)

                            val deltaX = x - cursorX
                            val deltaY = y - cursorY

                            val dist = deltaX * deltaX * deltaY * deltaY

                            if(closest == null || closestDist > dist) {
                                closest = wheelTreeEntry
                                closestDist = dist
                            }
                        }

                        if(closest != null) {
                            if(closest is WheelTreeRunnable) {
                                closest.runnable.run()
                                onClose()
                            } else if(closest is WheelTreeSubMenu) {
                                wheelTreeEntry = closest
                                wheelTreeEntries = managerWheelTree.getEntries(wheelTreeEntry)!!
                            }
                        }
                    }
                }
            } else if(event is EventKey) {
                if(event.key == GLFW.GLFW_KEY_ESCAPE) {
                    onClose()
                }
            } else if(event is EventMouseDelta) {
                if(active && MinecraftClient.getInstance().currentScreen == null) {
                    val rotationChange = Rotation.calculateRotationChange(event.deltaX, event.deltaY)
                    cursorX += rotationChange.yaw * 4
                    cursorY += rotationChange.pitch * 4
                }
            } else if(event is EventRender2D) {
                if(!active)
                    return@Consumer
                val radius = min(MinecraftClient.getInstance().window.scaledWidth, MinecraftClient.getInstance().window.scaledHeight) * 0.25
                val vec2f = Vec2f(cursorX.toFloat(), cursorY.toFloat()) // I want to use Minecraft vector math functions here, probably slower than needed but easier to understand
                if(vec2f.lengthSquared() > radius * radius) {
                    val finalPos = vec2f.normalize().multiply(radius.toFloat())
                    cursorX = finalPos.x.toDouble()
                    cursorY = finalPos.y.toDouble()
                }
                TarasandeMain.get().blur?.bind(true)
                RenderUtil.fillCircle(event.matrices, MinecraftClient.getInstance().window.scaledWidth / 2.0, MinecraftClient.getInstance().window.scaledHeight / 2.0, radius, -1)
                MinecraftClient.getInstance().framebuffer.beginWrite(true)
                RenderUtil.fillCircle(event.matrices, MinecraftClient.getInstance().window.scaledWidth / 2.0, MinecraftClient.getInstance().window.scaledHeight / 2.0, radius, Int.MIN_VALUE)
                RenderUtil.fillCircle(event.matrices, MinecraftClient.getInstance().window.scaledWidth / 2.0 + cursorX, MinecraftClient.getInstance().window.scaledHeight / 2.0 + cursorY, 1.0, -1)

                var closest: WheelTreeEntry? = null
                var closestDist = 0.0
                for((index, wheelTreeEntry) in wheelTreeEntries.withIndex()) {
                    val stringWidth = MinecraftClient.getInstance().textRenderer.getWidth(wheelTreeEntry.name)

                    val dir = index.toDouble() * 2.0 * Math.PI / wheelTreeEntries.size

                    val x = sin(dir) * (radius - stringWidth)
                    val y = cos(dir) * (radius - stringWidth)

                    val deltaX = x - cursorX
                    val deltaY = y - cursorY

                    val dist = deltaX * deltaX * deltaY * deltaY

                    if(closest == null || closestDist > dist) {
                        closest = wheelTreeEntry
                        closestDist = dist
                    }
                }

                for((index, wheelTreeEntry) in wheelTreeEntries.withIndex()) {
                    val stringWidth = MinecraftClient.getInstance().textRenderer.getWidth(wheelTreeEntry.name)

                    val dir = index.toDouble() * 2.0 * Math.PI / wheelTreeEntries.size

                    val x = MinecraftClient.getInstance().window.scaledWidth / 2.0 + sin(dir) * (radius - stringWidth)
                    val y = MinecraftClient.getInstance().window.scaledHeight / 2.0 + cos(dir) * (radius - stringWidth)

                    MinecraftClient.getInstance().textRenderer.drawWithShadow(event.matrices, wheelTreeEntry.name, x.toFloat() - stringWidth / 2.0f, y.toFloat(), if(closest == wheelTreeEntry) TarasandeMain.get().clientValues?.accentColor?.getColor()?.rgb!! else -1)
                }
            } else if(event is EventUpdate && event.state == EventUpdate.State.PRE) {
                if(active) {
                    ticksOpen++
                    if(MinecraftClient.getInstance().currentScreen != null)
                        onClose()
                }
            }
        }))
    }

    private fun onClose() {
        active = false
        cursorX = 0.0
        cursorY = 0.0
        ticksOpen = 0
        wheelTreeEntry = null
        wheelTreeEntries = managerWheelTree.getEntries(wheelTreeEntry)!!
    }

}