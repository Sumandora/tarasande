package net.tarasandedevelopment.tarasande.protocolhack.fixes

import java.util.concurrent.ConcurrentLinkedDeque

object InputTracker1_12_2 {

    val mouse = ConcurrentLinkedDeque<Runnable>()
    val keyboard = ConcurrentLinkedDeque<Runnable>()
}