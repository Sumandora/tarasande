package su.mandora.tarasande.util.threading

class ThreadRunnableExposed<T : Runnable>(target: T) : Thread(target) {
    val runnable = target
}