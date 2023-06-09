package su.mandora.tarasande.system.feature.modulesystem.impl.render

import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.registry.Registries
import net.minecraft.util.math.Vec3d
import su.mandora.tarasande.event.impl.EventRender2D
import su.mandora.tarasande.event.impl.EventRender3D
import su.mandora.tarasande.feature.entitycolor.EntityColor
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode
import su.mandora.tarasande.system.base.valuesystem.impl.ValueRegistry
import su.mandora.tarasande.system.base.valuesystem.impl.meta.abstracted.ValueButtonOwnerValues
import su.mandora.tarasande.system.feature.espsystem.ManagerESP
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.system.feature.modulesystem.impl.combat.ModuleAntiBot
import su.mandora.tarasande.util.extension.minecraft.minus
import su.mandora.tarasande.util.extension.minecraft.plus
import su.mandora.tarasande.util.extension.minecraft.times
import su.mandora.tarasande.util.render.RenderUtil

class ModuleESP : Module("ESP", "Makes entities visible behind walls", ModuleCategory.RENDER) {

    val mode = ValueMode(this, "Mode", true, "Shader", "2D")
    val entities = object : ValueRegistry<EntityType<*>>(this, "Entities", Registries.ENTITY_TYPE, true, EntityType.PLAYER) {
        override fun getTranslationKey(key: Any?) = (key as EntityType<*>).translationKey
    }

    val entityColor = EntityColor()

    init {
        ValueButtonOwnerValues(this, "Entity colors", entityColor)
        ValueButtonOwnerValues(this, "2D ESP values", ManagerESP, isEnabled = { mode.isSelected(1) })
    }

    fun shouldRender(entity: Entity) =
        entities.isSelected(entity.type) &&
                (entity !is PlayerEntity || entity == mc.player || !ManagerModule.get(ModuleAntiBot::class.java).isBot(entity))

    private val hashMap = HashMap<Entity, Rectangle>()

    init {
        registerEvent(EventRender3D::class.java) { event ->
            hashMap.clear()
            if (!mode.isSelected(1))
                return@registerEvent
            for (entity in mc.world?.entities!!) {
                if (!shouldRender(entity)) continue

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
                    val projected = RenderUtil.project(event.matrices.peek().positionMatrix, event.positionMatrix, corner) ?: continue
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

        registerEvent(EventRender2D::class.java, 998) { event ->
            for (entry in hashMap.entries) {
                ManagerESP.renderBox(event.matrices, entry.key, entry.value)
            }
        }
    }

    inner class Rectangle(var x: Double, var y: Double, var z: Double, var w: Double)
}