package su.mandora.tarasande.util.extension.minecraft

import net.minecraft.network.PacketByteBuf

fun PacketByteBuf.asByteArray(): ByteArray {
    val arr = ByteArray(readableBytes())
    repeat(arr.size) {
        arr[it] = readByte()
    }
    return arr
}