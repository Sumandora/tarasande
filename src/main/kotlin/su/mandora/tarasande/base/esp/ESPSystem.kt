package su.mandora.tarasande.base.esp

import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.Entity
import net.minecraft.util.math.Vec3f
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.base.Manager
import su.mandora.tarasande.esp.ESPElementBox
import su.mandora.tarasande.esp.ESPElementHealthBar
import su.mandora.tarasande.esp.ESPElementName
import su.mandora.tarasande.module.render.ModuleESP
import kotlin.math.abs

class ManagerESP : Manager<ESPElement>() {

    init {
        add(
            ESPElementBox(),
            ESPElementName(),
            ESPElementHealthBar()
        )
    }

    fun renderBox(matrices: MatrixStack, entity: Entity, rectangle: ModuleESP.Rectangle) {
        list.forEach { it.draw(matrices, entity, rectangle) }
    }
}

abstract class ESPElement(val name: String) {
    var enabled = true
    var orientation = Orientation.values()[0]

    abstract fun draw(matrices: MatrixStack, entity: Entity, rectangle: ModuleESP.Rectangle)
}

abstract class ESPElementRotatable(name: String, private val forbiddenOrientations: Array<Orientation> = arrayOf(), private val rotate: Boolean = true) : ESPElement(name) {
    init {
        orientation = Orientation.values().first { !forbiddenOrientations.contains(it) }
    }

    abstract fun draw(matrices: MatrixStack, entity: Entity, sideWidth: Double)

    override fun draw(matrices: MatrixStack, entity: Entity, rectangle: ModuleESP.Rectangle) {
        val sideWidth = when (orientation) {
            Orientation.TOP, Orientation.BOTTOM -> abs(rectangle.z - rectangle.x)
            Orientation.LEFT, Orientation.RIGHT -> abs(rectangle.w - rectangle.y)
        }
        matrices.push()
        var padding = 2.0
        for (espElement in TarasandeMain.get().managerESP?.list!!) {
            if (espElement == this) break
            if (espElement.enabled && espElement is ESPElementRotatable && espElement.orientation == orientation) padding += espElement.getHeight(entity, sideWidth)
        }
        matrices.translate(rectangle.x, rectangle.y, 0.0)
        if (rotate) {
            if (orientation == Orientation.BOTTOM) {
                matrices.translate((rectangle.z - rectangle.x) * 0.5, -(rectangle.y - rectangle.w) * 0.5, 0.0)
                matrices.scale(-1.0f, -1.0f, 1.0f)
                matrices.translate(-(rectangle.z - rectangle.x) * 0.5, (rectangle.y - rectangle.w) * 0.5, 0.0)
            } else if (orientation == Orientation.LEFT || orientation == Orientation.RIGHT) {
                matrices.translate(-(rectangle.y - rectangle.w) * 0.5, -(rectangle.y - rectangle.w) * 0.5, 0.0)
                matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(-90.0f))
                matrices.translate((rectangle.y - rectangle.w) * 0.5, (rectangle.y - rectangle.w) * 0.5, 0.0)
            }

            if (orientation == Orientation.RIGHT) {
                matrices.translate(-(rectangle.y - rectangle.w) * 0.5, (rectangle.z - rectangle.x) * 0.5, 0.0)
                matrices.scale(-1.0f, -1.0f, 1.0f)
                matrices.translate((rectangle.y - rectangle.w) * 0.5, -(rectangle.z - rectangle.x) * 0.5, 0.0)
            }
        }
        matrices.translate(0.0, -padding, 0.0)
        matrices.translate(0.0, -getHeight(entity, sideWidth), 0.0)
        draw(matrices, entity, sideWidth)
        matrices.pop()
    }

    abstract fun getHeight(entity: Entity, sideWidth: Double): Double
}

enum class Orientation {
    TOP, LEFT, BOTTOM, RIGHT
}