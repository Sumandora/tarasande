package net.tarasandedevelopment.tarasande.module.render

import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.Matrix4f
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vector4f
import net.minecraft.util.registry.Registry
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventRender2D
import net.tarasandedevelopment.tarasande.event.EventRender3D
import net.tarasandedevelopment.tarasande.module.combat.ModuleAntiBot
import net.tarasandedevelopment.tarasande.screen.base.ScreenBetterParentPopupSettings
import net.tarasandedevelopment.tarasande.util.extension.minus
import net.tarasandedevelopment.tarasande.util.extension.plus
import net.tarasandedevelopment.tarasande.util.extension.times
import net.tarasandedevelopment.tarasande.util.extension.unaryMinus
import net.tarasandedevelopment.tarasande.util.player.entitycolor.EntityColor
import net.tarasandedevelopment.tarasande.value.ValueBoolean
import net.tarasandedevelopment.tarasande.value.ValueButton
import net.tarasandedevelopment.tarasande.value.ValueMode
import net.tarasandedevelopment.tarasande.value.ValueRegistry

class ModuleESP : Module("ESP", "Makes entities visible behind walls", ModuleCategory.RENDER) {

    val mode = ValueMode(this, "Mode", true, "Shader", "2D")
    val entities = object : ValueRegistry<EntityType<*>>(this, "Entities", Registry.ENTITY_TYPE, EntityType.PLAYER) {
        override fun getTranslationKey(key: Any?) = (key as EntityType<*>).translationKey
    }
    private val hideBots = object : ValueBoolean(this, "Hide bots", false) {
        override fun isEnabled() = entities.list.contains(EntityType.PLAYER)
    }

    init {
        object : ValueButton(this, "Entity colors") {
            override fun onChange() {
                MinecraftClient.getInstance().setScreen(ScreenBetterParentPopupSettings(MinecraftClient.getInstance().currentScreen!!, name, entityColor))
            }
        }
        object : ValueButton(this, "2D ESP settings") {
            override fun onChange() {
                MinecraftClient.getInstance().setScreen(ScreenBetterParentPopupSettings(MinecraftClient.getInstance().currentScreen!!, name, TarasandeMain.get().managerESP))
            }

            override fun isEnabled(): Boolean {
                return mode.isSelected(1)
            }
        }
    }

    val entityColor = EntityColor(this)

    fun filter(entity: Entity) =
        entities.list.contains(entity.type) &&
                (!hideBots.value || entity !is PlayerEntity || entity == mc.player || !TarasandeMain.get().managerModule.get(ModuleAntiBot::class.java).isBot(entity))

    private val hashMap = HashMap<Entity, Rectangle>()

    private fun project(modelView: Matrix4f, projection: Matrix4f, vector: Vec3d): Vec3d? {
        val camPos = -mc.gameRenderer.camera.pos + vector
        val vec1 = matrixVectorMultiply(modelView, Vector4f(camPos.x.toFloat(), camPos.y.toFloat(), camPos.z.toFloat(), 1.0f))
        val screenPos = matrixVectorMultiply(projection, vec1)

        if (screenPos.w <= 0.0) return null

        val newW = 1.0 / screenPos.w * 0.5

        screenPos.set(
            (screenPos.x * newW + 0.5).toFloat(),
            (screenPos.y * newW + 0.5).toFloat(),
            (screenPos.z * newW + 0.5).toFloat(),
            newW.toFloat()
        )

        return Vec3d(
            screenPos.x * mc.window?.framebufferWidth!! / mc.window?.scaleFactor!!,
            (mc.window?.framebufferHeight!! - (screenPos.y * mc.window?.framebufferHeight!!)) / mc.window?.scaleFactor!!,
            screenPos.z.toDouble()
        )
    }

    private fun matrixVectorMultiply(matrix4f: Matrix4f, vector: Vector4f): Vector4f {
        return Vector4f(
            matrix4f.a00 * vector.x + matrix4f.a01 * vector.y + matrix4f.a02 * vector.z + matrix4f.a03 * vector.w,
            matrix4f.a10 * vector.x + matrix4f.a11 * vector.y + matrix4f.a12 * vector.z + matrix4f.a13 * vector.w,
            matrix4f.a20 * vector.x + matrix4f.a21 * vector.y + matrix4f.a22 * vector.z + matrix4f.a23 * vector.w,
            matrix4f.a30 * vector.x + matrix4f.a31 * vector.y + matrix4f.a32 * vector.z + matrix4f.a33 * vector.w
        )
    }

    init {
        registerEvent(EventRender3D::class.java) { event ->
            hashMap.clear()
            if (!mode.isSelected(1))
                return@registerEvent
            for (entity in mc.world?.entities!!) {
                if (!filter(entity)) continue

                if (mc.options.perspective.isFirstPerson && entity == mc.player) continue

                val prevPos = Vec3d(entity.lastRenderX, entity.lastRenderY, entity.lastRenderZ)
                val interp = prevPos + (entity.pos - prevPos) * mc.tickDelta.toDouble()
                val boundingBox = entity.boundingBox.offset(interp - entity.pos)

                if (!mc.worldRenderer.frustum.isVisible(boundingBox)) continue

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

                for (corner in corners) {
                    val projected = project(event.matrices.peek().positionMatrix, event.positionMatrix, corner) ?: continue
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

                if (rectangle != null)
                    hashMap[entity] = rectangle
            }
        }

        registerEvent(EventRender2D::class.java, 999) { event ->
            for (entry in hashMap.entries) {
                TarasandeMain.get().managerESP.renderBox(event.matrices, entry.key, entry.value)
            }
        }
    }

    inner class Rectangle(var x: Double, var y: Double, var z: Double, var w: Double)
}