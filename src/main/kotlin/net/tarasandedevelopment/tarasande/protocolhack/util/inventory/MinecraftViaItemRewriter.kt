package net.tarasandedevelopment.tarasande.protocolhack.util.inventory

import com.viaversion.viaversion.api.Via
import com.viaversion.viaversion.api.minecraft.item.Item
import com.viaversion.viaversion.api.protocol.packet.Direction
import com.viaversion.viaversion.api.protocol.packet.State
import com.viaversion.viaversion.connection.UserConnectionImpl
import com.viaversion.viaversion.exception.CancelException
import com.viaversion.viaversion.protocol.packet.PacketWrapperImpl
import de.florianmichael.clampclient.injection.mixininterface.IPacketWrapperImpl_Protocol
import io.netty.buffer.Unpooled
import net.minecraft.SharedConstants
import net.minecraft.item.ItemStack
import net.minecraft.network.NetworkSide
import net.minecraft.network.NetworkState
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket
import net.tarasandedevelopment.tarasande.TarasandeMain

object MinecraftViaItemRewriter {

    private val userConnection = UserConnectionImpl(null, true)

    fun minecraftToViaItem(stack: ItemStack) = minecraftToViaItem(stack, TarasandeMain.get().protocolHack.targetVersion())

    fun minecraftToViaItem(stack: ItemStack, targetVersion: Int): Item? {
        // Generate protocol path
        val path = Via.getManager().protocolManager.getProtocolPath(SharedConstants.getProtocolVersion(), targetVersion) ?: return null

        // Create a fake creative inventory action
        val packet = CreativeInventoryActionC2SPacket(36, stack)
        val buf = PacketByteBuf(Unpooled.buffer())

        packet.write(buf)

        val id = NetworkState.PLAY.getPacketId(NetworkSide.SERVERBOUND, packet) ?: return null

        val wrapper = PacketWrapperImpl(id, buf, userConnection)

        try {
            // Transform all packets according to UserConnectionImpl#transform
            path.forEach {
                it.protocol().transform(Direction.SERVERBOUND, State.PLAY, wrapper)
                wrapper.resetReader()
            }
        } catch (exception: CancelException) {
            exception.printStackTrace()
            // The item no longer exists?
            return null
        }

        // Hack: get the first Item from the packet wrapper, sadly there is no method for that
        return (wrapper as IPacketWrapperImpl_Protocol).protocolhack_getReadableObjects().first { Item::class.java.equals(it.key()!!.outputClass) }.value() as Item?
    }
}
