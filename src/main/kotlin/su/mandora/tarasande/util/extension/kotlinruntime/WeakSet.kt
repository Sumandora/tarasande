package su.mandora.tarasande.util.extension.kotlinruntime

import java.util.*

fun <T> WeakSet(): MutableSet<T> = Collections.newSetFromMap(WeakHashMap<T, Boolean>())