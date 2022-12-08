package net.tarasandedevelopment.tarasande.util.extension.javaruntime

import java.awt.Color

fun Color.withAlpha(alpha: Int): Color {
    return Color(this.red, this.green, this.blue, alpha)
}