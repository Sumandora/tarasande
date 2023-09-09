package su.mandora.tarasande.system.feature.modulesystem.impl.render

import com.mojang.blaze3d.platform.GlConst
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.render.*
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.registry.Registries
import net.minecraft.util.math.ColorHelper
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d
import org.lwjgl.opengl.GL11
import su.mandora.tarasande.event.impl.EventRender2D
import su.mandora.tarasande.event.impl.EventRender3D
import su.mandora.tarasande.feature.entitycolor.EntityColor
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.base.valuesystem.impl.ValueRegistry
import su.mandora.tarasande.system.base.valuesystem.impl.meta.abstracted.ValueButtonOwnerValues
import su.mandora.tarasande.system.feature.espsystem.ManagerESP
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.system.feature.modulesystem.impl.combat.ModuleAntiBot
import su.mandora.tarasande.util.extension.minecraft.math.minus
import su.mandora.tarasande.util.extension.minecraft.math.plus
import su.mandora.tarasande.util.extension.minecraft.math.times
import su.mandora.tarasande.util.render.RenderUtil
import kotlin.math.max


class ModuleESP : Module("ESP", "Makes entities visible behind walls", ModuleCategory.RENDER) {

    val mode = ValueMode(this, "Mode", true, "Spectral", "2D", "Tracers")
    val entities = object : ValueRegistry<EntityType<*>>(this, "Entities", Registries.ENTITY_TYPE, true, EntityType.PLAYER) {
        override fun getTranslationKey(key: Any?) = (key as EntityType<*>).translationKey
    }
    private val tracerWidth = ValueNumber(this, "Tracer width", 0.1, 1.0, 5.0, 0.1, isEnabled = { mode.isSelected(2) })

    private val limitDistance = ValueBoolean(this, "Limit distance", false)
    private val maxDistance = ValueNumber(this, "Max distance", 0.0, 256.0, 4096.0, 64.0, isEnabled = { limitDistance.value })

    val entityColor = EntityColor()

    init {
        ValueButtonOwnerValues(this, "Entity colors", entityColor)
        ValueButtonOwnerValues(this, "2D ESP values", ManagerESP, isEnabled = { mode.isSelected(1) })
    }

    private val moduleAntiBot by lazy { ManagerModule.get(ModuleAntiBot::class.java) }

    fun shouldRender(entity: Entity) =
        (!limitDistance.value || entity.distanceTo(mc.player) <= maxDistance.value) &&
                entities.isSelected(entity.type) &&
                (entity !is PlayerEntity || entity == mc.player || !moduleAntiBot.isBot(entity))

    private val hashMap = HashMap<Entity, Pair<Rectangle, Boolean>>()


    init {
        registerEvent(EventRender3D::class.java) { event ->
            if (event.state != EventRender3D.State.POST)
                return@registerEvent
            hashMap.clear()
            if (!mode.isSelected(1))
                return@registerEvent
            for (entity in mc.world!!.entities) {
                if (!shouldRender(entity)) continue

                if (mc.options.perspective.isFirstPerson && entity == mc.player) continue

                val prevPos = Vec3d(entity.lastRenderX, entity.lastRenderY, entity.lastRenderZ)
                val interp = prevPos + (entity.pos - prevPos) * mc.tickDelta.toDouble()
                val boundingBox = entity.boundingBox.offset(interp - entity.pos)

                if (!mode.isSelected(2) /* Tracers go off-screen aswell */ && !mc.worldRenderer.frustum.isVisible(boundingBox)) continue

                val corners = arrayOf(
                    Vec3d(boundingBox.minX, boundingBox.minY, boundingBox.minZ),
                    Vec3d(boundingBox.maxX, boundingBox.minY, boundingBox.minZ),
                    Vec3d(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ),
                    Vec3d(boundingBox.minX, boundingBox.minY, boundingBox.maxZ),

                    Vec3d(boundingBox.minX, boundingBox.maxY, boundingBox.minZ),
                    Vec3d(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ),
                    Vec3d(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ),
                    Vec3d(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ)
                )

                var rectangle: Rectangle? = null

                var visible = false

                for (corner in corners) {
                    val projection = RenderUtil.project(event.matrices.peek().positionMatrix, event.projectionMatrix, corner)
                    if (projection.second) visible = true
                    val projected = projection.first
                    if (rectangle == null)
                        rectangle = Rectangle(projected.x, projected.y, projected.x, projected.y)
                    else {
                        if (rectangle.x > projected.x)
                            rectangle.x = projected.x

                        if (rectangle.y > projected.y)
                            rectangle.y = projected.y

                        if (rectangle.z < projected.x)
                            rectangle.z = projected.x

                        if (rectangle.w < projected.y)
                            rectangle.w = projected.y
                    }
                }

                hashMap[entity] = rectangle!! to visible
            }
        }

        registerEvent(EventRender2D::class.java, 998) { event ->
            for ((entity, pair) in hashMap.entries) {
                if (pair.second)
                    ManagerESP.renderBox(event.context, entity, pair.first)
            }

            if (mode.isSelected(2)) {
                val hashMap = hashMap.filterNot { it.key == mc.player }
                if (hashMap.isNotEmpty()) {
                    RenderSystem.enableBlend()
                    RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA)
                    GL11.glEnable(GL11.GL_LINE_SMOOTH)
                    val lineWidth = GL11.glGetFloat(GL11.GL_LINE_WIDTH)
                    GL11.glLineWidth(tracerWidth.value.toFloat())

                    RenderSystem.setShaderColor(1F, 1F, 1F, 1F)

                    val bufferBuilder = Tessellator.getInstance().buffer

                    RenderSystem.setShader { GameRenderer.getPositionColorProgram() }
                    bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR)

                    val matrix = event.context.matrices.peek().positionMatrix

                    val center = Vec2f(mc.window.scaledWidth / 2F, mc.window.scaledHeight / 2F)
                    val screenSize = max(mc.window.scaledWidth, mc.window.scaledHeight)

                    for ((entity, pair) in hashMap.entries) {
                        val color = entity.teamColorValue
                        bufferBuilder.fixedColor(ColorHelper.Argb.getRed(color), ColorHelper.Argb.getGreen(color), ColorHelper.Argb.getBlue(color), 255)
                        bufferBuilder.vertex(matrix, center.x, center.y, 0F).next()

                        var vec2f = pair.first.center()
                        if (!pair.second) { // Those are linear functions, so we can extend them
                            vec2f = (vec2f - center) * screenSize
                        }
                        bufferBuilder.vertex(matrix, vec2f.x, vec2f.y, 0F).next()
                        bufferBuilder.unfixColor()
                    }

                    BufferRenderer.drawWithGlobalProgram(bufferBuilder.end())
                    GL11.glLineWidth(lineWidth)
                    GL11.glDisable(GL11.GL_LINE_SMOOTH)
                    RenderSystem.disableBlend()
                }
            }
        }
    }

    inner class Rectangle(var x: Double, var y: Double, var z: Double, var w: Double) {
        fun center() = Vec2f((x + z).toFloat() / 2F, (y + w).toFloat() / 2F)
    }
}