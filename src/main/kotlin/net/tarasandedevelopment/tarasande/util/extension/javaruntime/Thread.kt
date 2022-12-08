package net.tarasandedevelopment.tarasande.util.extension.javaruntime

// Hack: constructor extension (this is a function)
fun Thread(name: String, runnable: Runnable) = Thread(runnable, name)