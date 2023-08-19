package su.mandora.tarasande.util.extension.kotlinruntime

fun Int.ignoreAlpha(): Int { // Intended for hex colors (argb)
    return this or 0xFF000000.toInt()
}