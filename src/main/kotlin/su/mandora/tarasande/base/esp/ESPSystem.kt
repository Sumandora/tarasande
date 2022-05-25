package su.mandora.tarasande.base.esp

import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.Entity
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.base.Manager
import su.mandora.tarasande.esp.ESPElementBox
import su.mandora.tarasande.esp.ESPElementName
import su.mandora.tarasande.module.render.ModuleESP

class ManagerESP : Manager<ESPElement>() {

    init {
        add(
            ESPElementBox(),
            ESPElementName()
        )
    }

    fun renderBox(matrices: MatrixStack, entity: Entity, rectangle: ModuleESP.Rectangle) {
        list.forEach {
            when (it) {
                is ESPElementRotatable -> {

                }
                else -> it.draw(matrices, entity, rectangle)
            }
        }
    }
}

abstract class ESPElement(val name: String) {
    var enabled = true
    var orientation: Orientation = Orientation.values()[0]

    abstract fun draw(matrices: MatrixStack, entity: Entity, rectangle: ModuleESP.Rectangle)
}

abstract class ESPElementRotatable(name: String, val forbiddenOrientations: Array<Orientation>, val rotate: Boolean = true, val height: Double) : ESPElement(name) {
    init {
        Orientation.values().first { !forbiddenOrientations.contains(it) }
    }

    abstract fun draw(matrices: MatrixStack, entity: Entity, sideBegin: Double, sideEnd: Double)

    override fun draw(matrices: MatrixStack, entity: Entity, rectangle: ModuleESP.Rectangle) {
        val sideBegin = when (orientation) {
            Orientation.TOP -> rectangle.x
            Orientation.LEFT -> rectangle.y
            Orientation.BOTTOM -> rectangle.z
            Orientation.RIGHT -> rectangle.w
        }
        val sideEnd = when (orientation) {
            Orientation.TOP -> rectangle.z
            Orientation.LEFT -> rectangle.w
            Orientation.BOTTOM -> rectangle.x
            Orientation.RIGHT -> rectangle.y
        }
        matrices.push()
        var padding = 0.0
        for (espElement in TarasandeMain.get().managerESP?.list!!) {
            if (espElement.enabled && espElement is ESPElementRotatable && espElement.orientation == orientation)
                padding += espElement.height
        }
        matrices.translate(0.0, -padding, 0.0)
        draw(matrices, entity, sideBegin, sideEnd)
        matrices.pop()
    }
}

enum class Orientation {
    TOP, LEFT, BOTTOM, RIGHT
}