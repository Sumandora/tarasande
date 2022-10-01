package net.tarasandedevelopment.tarasande.util.threading

class ThreadRunnableExposed(target: Runnable) : Thread(target) {
    val runnable = target
}