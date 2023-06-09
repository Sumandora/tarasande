package su.mandora.tarasande.system.feature.espsystem

import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.Entity
import net.minecraft.util.math.RotationAxis
import su.mandora.tarasande.Manager
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode
import su.mandora.tarasande.system.base.valuesystem.impl.meta.abstracted.ValueButtonOwnerValues
import su.mandora.tarasande.system.feature.espsystem.impl.ESPElementBox
import su.mandora.tarasande.system.feature.espsystem.impl.ESPElementRotatableHealthBar
import su.mandora.tarasande.system.feature.espsystem.impl.ESPElementRotatableName
import su.mandora.tarasande.system.feature.modulesystem.impl.render.ModuleESP
import kotlin.math.abs

object ManagerESP : Manager<ESPElement>() {

    init {
        add(
            ESPElementBox(),
            ESPElementRotatableName(),
            ESPElementRotatableHealthBar()
        )

        for (element in list)
            ValueButtonOwnerValues(this, element.name, element)
    }

    fun renderBox(matrices: MatrixStack, entity: Entity, rectangle: ModuleESP.Rectangle) {
        list.forEach { if (it.enabled.value) it.draw(matrices, entity, rectangle) }
    }
}

abstract class ESPElement(val name: String) {
    @Suppress("LeakingThis")
    var enabled = ValueBoolean(this, name, false)

    abstract fun draw(matrices: MatrixStack, entity: Entity, rectangle: ModuleESP.Rectangle)
}

abstract class ESPElementRotatable(name: String, private val forbiddenOrientations: Array<Orientation> = arrayOf()) : ESPElement(name) {
    val orientations = Orientation.values().filter { !forbiddenOrientations.contains(it) }

    @Suppress("LeakingThis")
    var orientation: ValueMode? = if (orientations.size > 1)
        ValueMode(this, "$name: Orientation", false, *orientations.map { it.name.substring(0, 1).uppercase() + it.name.substring(1).lowercase() }.toTypedArray())
    else
        null

    abstract fun draw(matrices: MatrixStack, entity: Entity, sideWidth: Double, orientation: Orientation)

    override fun draw(matrices: MatrixStack, entity: Entity, rectangle: ModuleESP.Rectangle) {
        val orientation = if (this.orientation != null)
            orientations[this.orientation!!.values.indexOf(this.orientation!!.getSelected())]
        else
            orientations[0]
        val sideWidth = when (orientation) {
            Orientation.TOP, Orientation.BOTTOM -> abs(rectangle.z - rectangle.x)
            Orientation.LEFT, Orientation.RIGHT -> abs(rectangle.w - rectangle.y)
        }
        matrices.push()
        var padding = 2.0
        for (espElement in ManagerESP.list) {
            if (espElement == this) break
            if (espElement.enabled.value && espElement is ESPElementRotatable && espElement.orientations[espElement.orientation?.values?.indexOf(espElement.orientation!!.getSelected()) ?: 0] == orientation)
                padding += espElement.getHeight(entity, sideWidth)
        }
        matrices.translate(rectangle.x, rectangle.y, 0.0)
        if (orientation == Orientation.BOTTOM) {
            matrices.translate((rectangle.z - rectangle.x) * 0.5, -(rectangle.y - rectangle.w) * 0.5, 0.0)
            matrices.scale(-1.0F, -1.0F, 1.0F)
            matrices.translate(-(rectangle.z - rectangle.x) * 0.5, (rectangle.y - rectangle.w) * 0.5, 0.0)
        } else if (orientation == Orientation.LEFT || orientation == Orientation.RIGHT) {
            matrices.translate(-(rectangle.y - rectangle.w) * 0.5, -(rectangle.y - rectangle.w) * 0.5, 0.0)
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-90.0F))
            matrices.translate((rectangle.y - rectangle.w) * 0.5, (rectangle.y - rectangle.w) * 0.5, 0.0)

            if (orientation == Orientation.RIGHT) {
                matrices.translate(-(rectangle.y - rectangle.w) * 0.5, (rectangle.z - rectangle.x) * 0.5, 0.0)
                matrices.scale(1.0F, -1.0F, 1.0F)
                matrices.translate((rectangle.y - rectangle.w) * 0.5, -(rectangle.z - rectangle.x) * 0.5, 0.0)
            }
        }
        matrices.translate(0.0, -padding, 0.0)
        matrices.translate(0.0, -getHeight(entity, sideWidth), 0.0)
        draw(matrices, entity, sideWidth, orientation)
        matrices.pop()
    }

    abstract fun getHeight(entity: Entity, sideWidth: Double): Double
}

enum class Orientation {
    TOP, LEFT, BOTTOM, RIGHT
}