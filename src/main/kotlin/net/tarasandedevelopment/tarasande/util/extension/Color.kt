package net.tarasandedevelopment.tarasande.util.extension

import java.awt.Color

fun Color.withAlpha(alpha: Int): Color {
    return Color(this.red, this.green, this.blue, alpha)
}