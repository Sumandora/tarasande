package net.tarasandedevelopment.tarasande.feature.clientvalue.impl.debug.camera

import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Arm
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueMode
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.util.math.MathUtil.roundAwayFromZero
import net.tarasandedevelopment.tarasande.util.string.StringUtil
import org.joml.Quaternionf

object ViewModel {

    private val x = ValueNumber(this, "X", -4.0, 0.0, 4.0, 0.1)
    private val y = ValueNumber(this, "Y", -4.0, 0.0, 4.0, 0.1)
    private val z = ValueNumber(this, "Z", -4.0, 0.0, 4.0, 0.1)

    private val rotateX = ValueNumber(this, "Rotate X", -90.0, 0.0, 90.0, 1.0)
    private val rotateY = ValueNumber(this, "Rotate Y", -90.0, 0.0, 90.0, 1.0)
    private val rotateZ = ValueNumber(this, "Rotate Z", -90.0, 0.0, 90.0, 1.0)

    private val scaleX = ValueNumber(this, "Scale X", 0.0, 1.0, 2.0, 0.1)
    private val scaleY = ValueNumber(this, "Scale Y", 0.0, 1.0, 2.0, 0.1)
    private val scaleZ = ValueNumber(this, "Scale Z", 0.0, 1.0, 2.0, 0.1)

    private val hands = ValueMode(this, "Hands", true, *Arm.values().map { StringUtil.formatEnumTypes(it.name) }.toTypedArray())
    private val emptyHands = ValueMode(this, "Empty hands", true, *Arm.values().map { StringUtil.formatEnumTypes(it.name) }.toTypedArray())

    fun applyTransform(matrices: MatrixStack, emptyHand: Boolean, arm: Arm) {
        if ((!emptyHand && hands.isSelected(arm.ordinal)) || (emptyHand && emptyHands.isSelected(arm.ordinal))) {
            val handOffset = -roundAwayFromZero(arm.opposite /*Let's make the user input the main hand*/.ordinal - 0.5) /* rofl */
            matrices.translate(x.value * handOffset, y.value, z.value)
            matrices.multiply(
                Quaternionf()
                    .rotateX(Math.toRadians(rotateX.value).toFloat())
                    .rotateY(Math.toRadians(rotateY.value * handOffset).toFloat())
                    .rotateZ(Math.toRadians(rotateZ.value).toFloat())
            )
            matrices.scale(scaleX.value.toFloat(), scaleY.value.toFloat(), scaleZ.value.toFloat())
        }
    }
}