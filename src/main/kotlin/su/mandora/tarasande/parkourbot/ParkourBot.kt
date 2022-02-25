package su.mandora.tarasande.parkourbot

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.input.KeyboardInput
import net.minecraft.client.render.BufferRenderer
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexFormat
import net.minecraft.client.render.VertexFormats
import org.lwjgl.opengl.GL11
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.event.EventPollEvents
import su.mandora.tarasande.event.EventRender3D
import su.mandora.tarasande.event.EventUpdate
import su.mandora.tarasande.parkourbot.pathbuilder.Goal
import su.mandora.tarasande.parkourbot.pathbuilder.PathBuilder
import su.mandora.tarasande.parkourbot.traverser.Movement
import su.mandora.tarasande.parkourbot.traverser.Traverser
import su.mandora.tarasande.util.math.rotation.Rotation
import su.mandora.tarasande.util.math.rotation.RotationUtil
import java.util.concurrent.CopyOnWriteArrayList


class ParkourBot {

    private var pathBuilder: PathBuilder? = null
    private val tasks = CopyOnWriteArrayList<Runnable>()
    private var asynchronousTaskCompleter: Thread? = null
    private var active = false
    private var traverser: Traverser? = null
    private var movement: Movement? = null

    var startRotation: Rotation? = null

    init {
        TarasandeMain.get().managerEvent?.add { event ->
            if(event is EventRender3D) {
                if(active && pathBuilder != null) {
                    GL11.glEnable(GL11.GL_BLEND)
                    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
                    GL11.glDisable(GL11.GL_CULL_FACE)
                    GL11.glEnable(GL11.GL_LINE_SMOOTH)
                    GL11.glDisable(GL11.GL_DEPTH_TEST)
                    GL11.glDepthMask(false)
                    event.matrices.push()
                    val vec3d = MinecraftClient.getInstance().gameRenderer.camera.pos
                    event.matrices.translate(-vec3d.x, -vec3d.y, -vec3d.z)
                    val bufferBuilder = Tessellator.getInstance().buffer
                    RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
                    bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR)
                    val matrix = event.matrices.peek()?.positionMatrix!!
                    for (vec in pathBuilder?.path!!) {
                        bufferBuilder.vertex(matrix, vec.x.toFloat() + 0.5f, vec.y.toFloat() + 1.0f, vec.z.toFloat() + 0.5f).color(1f,1f,1f,1f).next()
                    }
                    bufferBuilder.end()
                    BufferRenderer.draw(bufferBuilder)
                    event.matrices.pop()
                    GL11.glDisable(GL11.GL_BLEND)
                    GL11.glDisable(GL11.GL_LINE_SMOOTH)
                    GL11.glEnable(GL11.GL_DEPTH_TEST)
                    GL11.glDepthMask(true)
                }
            } else if(event is EventUpdate && event.state == EventUpdate.State.PRE) {
                if(active) {
                    movement = traverser?.updateMovement()
                    MinecraftClient.getInstance().player?.input = movement?.input
                }
            } else if(event is EventPollEvents) {
                if(active && movement != null && movement?.aimPoint != null) {
                    event.rotation = RotationUtil.getRotations(MinecraftClient.getInstance().player?.eyePos!!, movement?.aimPoint!!).correctSensitivity()
                    MinecraftClient.getInstance().player?.yaw = event.rotation.yaw
                    MinecraftClient.getInstance().player?.pitch = event.rotation.pitch
                }
            }
        }
    }

    fun start(goal: Goal) {
        if(active) {
            stop()
        }
        startRotation = Rotation(MinecraftClient.getInstance().player!!)
        active = true
        var currentPos = MinecraftClient.getInstance().player?.blockPos?.add(0, -1, 0)!!
        var found = true
        if(MinecraftClient.getInstance().world?.isAir(currentPos)!!) {
            found = false
            for(x in -1..1) {
                for(y in -1..1) {
                    for(z in -1..1) {
                        if(!found && !MinecraftClient.getInstance().world?.isAir(currentPos.add(x, y, z))!!) {
                            currentPos = currentPos.add(x, y, z)
                            found = true
                        }
                    }
                }
            }
        }
        if(!found) {
            return
        }
        pathBuilder = PathBuilder(currentPos, goal)
        traverser = Traverser(pathBuilder?.path!!)
        tasks.add(Runnable {
            while (!pathBuilder?.computePath(1)!!) {
                Thread.sleep(10) // Slow down a bit
            }
        })
        asynchronousTaskCompleter = Thread( {
            while(true) {
                if(tasks.isNotEmpty()) {
                    tasks.forEach {
                        it.run()
                    }
                    tasks.clear()
                }
            }
        }, "Parkour-Bot-AsynchronousTaskCompleter")
        asynchronousTaskCompleter?.start()
    }

    fun stop() {
        if(active) {
            active = false
            tasks.clear()
            if(asynchronousTaskCompleter != null)
                asynchronousTaskCompleter?.stop()
            MinecraftClient.getInstance().player?.input = KeyboardInput(MinecraftClient.getInstance().options)
        }
    }

}