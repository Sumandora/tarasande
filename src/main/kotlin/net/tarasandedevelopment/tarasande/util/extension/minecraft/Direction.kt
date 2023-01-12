package net.tarasandedevelopment.tarasande.util.extension.minecraft

import net.minecraft.util.math.Direction

fun Direction.hitResultSide(): Direction {
    return if (offsetY == 0) opposite else this
}