package net.tarasandedevelopment.tarasande.feature.clientvalue.impl.debug.camera

import net.minecraft.util.Arm
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueMode
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.util.string.StringUtil

class ViewModel {

    val x = ValueNumber(this, "X", -4.0, 0.0, 4.0, 0.1)
    val y = ValueNumber(this, "Y", -4.0, 0.0, 4.0, 0.1)
    val z = ValueNumber(this, "Z", -4.0, 0.0, 4.0, 0.1)

    val rotateX = ValueNumber(this, "Rotate X", -90.0, 0.0, 90.0, 1.0)
    val rotateY = ValueNumber(this, "Rotate Y", -90.0, 0.0, 90.0, 1.0)
    val rotateZ = ValueNumber(this, "Rotate Z", -90.0, 0.0, 90.0, 1.0)

    val scaleX = ValueNumber(this, "Scale X", 0.0, 1.0, 2.0, 0.1)
    val scaleY = ValueNumber(this, "Scale Y", 0.0, 1.0, 2.0, 0.1)
    val scaleZ = ValueNumber(this, "Scale Z", 0.0, 1.0, 2.0, 0.1)

    val arms = ValueMode(this, "Arms", true, *Arm.values().map { StringUtil.formatEnumTypes(it.name) }.toTypedArray())
    val applyToHand = ValueBoolean(this, "Apply to hand", true)

}