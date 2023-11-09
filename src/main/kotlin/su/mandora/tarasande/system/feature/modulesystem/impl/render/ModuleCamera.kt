package su.mandora.tarasande.system.feature.modulesystem.impl.render

import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Arm
import org.joml.Quaternionf
import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory
import su.mandora.tarasande.util.DEFAULT_THIRD_PERSON_DISTANCE
import su.mandora.tarasande.util.math.MathUtil
import su.mandora.tarasande.util.string.StringUtil

class ModuleCamera : Module("Camera", "Changes the view settings", ModuleCategory.RENDER) {


    private val x = ValueNumber(this, "X", -4.0, 0.0, 4.0, 0.1)
    private val y = ValueNumber(this, "Y", -4.0, 0.0, 4.0, 0.1)
    private val z = ValueNumber(this, "Z", -4.0, 0.0, 4.0, 0.1)

    private val rotateX = ValueNumber(this, "Rotate X", -90.0, 0.0, 90.0, 1.0)
    private val rotateY = ValueNumber(this, "Rotate Y", -90.0, 0.0, 90.0, 1.0)
    private val rotateZ = ValueNumber(this, "Rotate Z", -90.0, 0.0, 90.0, 1.0)

    private val scaleX = ValueNumber(this, "Scale X", 0.0, 1.0, 2.0, 0.1)
    private val scaleY = ValueNumber(this, "Scale Y", 0.0, 1.0, 2.0, 0.1)
    private val scaleZ = ValueNumber(this, "Scale Z", 0.0, 1.0, 2.0, 0.1)

    private val arms = ValueMode(this, "Arms", true, *Arm.entries.map { StringUtil.uncoverTranslation(it.translationKey) }.toTypedArray())

    init {
        arms.select(0)
        arms.select(1)
    }

    val forceAspectRatio = ValueBoolean(this, "Force aspect ratio", false)
    val aspectRatio = ValueNumber(this, "Aspect ratio", 0.1, 1.0, 4.0, 0.1, isEnabled = { forceAspectRatio.value })

    val changeThirdPersonDistance = ValueBoolean(this, "Change third person distance", false)
    val thirdPersonDistance = ValueNumber(this, "Third person distance", 0.1, DEFAULT_THIRD_PERSON_DISTANCE, 10.0, 0.1, isEnabled = { changeThirdPersonDistance.value })

    val thirdPersonNoClip = ValueBoolean(this, "Third person no clip", false)


    fun applyTransform(matrices: MatrixStack, arm: Arm) {
        if (arms.isSelected(arm.ordinal)) {
            val handOffset = -MathUtil.roundAwayFromZero(arm.opposite.ordinal - 0.5) /* rofl */
            matrices.multiply(
                Quaternionf()
                    .rotateX(Math.toRadians(rotateX.value).toFloat())
                    .rotateY(Math.toRadians(rotateY.value * handOffset).toFloat())
                    .rotateZ(Math.toRadians(rotateZ.value).toFloat())
            )
            matrices.translate(x.value * handOffset, y.value, z.value)
            matrices.scale(scaleX.value.toFloat(), scaleY.value.toFloat(), scaleZ.value.toFloat())
        }
    }

}
