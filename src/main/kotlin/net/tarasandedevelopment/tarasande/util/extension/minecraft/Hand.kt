package net.tarasandedevelopment.tarasande.util.extension.minecraft

import net.minecraft.util.Arm
import net.minecraft.util.Hand
import net.tarasandedevelopment.tarasande.mc

fun Hand.toArm(): Arm {
    return when(this) {
        Hand.MAIN_HAND -> mc.options.mainArm.value
        Hand.OFF_HAND -> mc.options.mainArm.value.opposite
    }
}