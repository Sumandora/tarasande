package net.tarasandedevelopment.tarasande.util.extension.kotlinruntime

fun <T> T?.nullOr(block: (T) -> Boolean): Boolean = this == null || block(this)