package de.florianmichael.tarasande_protocol_hack.util.inventory

import com.viaversion.viaversion.api.Via
import com.viaversion.viaversion.api.connection.UserConnection
import com.viaversion.viaversion.api.minecraft.item.Item
import com.viaversion.viaversion.api.protocol.ProtocolPathEntry
import com.viaversion.viaversion.api.protocol.packet.Direction
import com.viaversion.viaversion.api.protocol.packet.State
import com.viaversion.viaversion.protocol.packet.PacketWrapperImpl
import de.florianmichael.clampclient.injection.mixininterface.IPacketWrapperImpl_Protocol
import io.netty.buffer.Unpooled
import net.minecraft.SharedConstants
import net.minecraft.item.ItemStack
import net.minecraft.network.NetworkSide
import net.minecraft.network.NetworkState
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket

object MinecraftViaItemRewriter {

    fun minecraftToViaItem(info: UserConnection, stack: ItemStack, targetVersion: Int): Item? {
        // Generate protocol path
        val path: List<ProtocolPathEntry> = Via.getManager().protocolManager.getProtocolPath(SharedConstants.getProtocolVersion(), targetVersion) ?: return null

        // Create a fake creative inventory action
        val packet = CreativeInventoryActionC2SPacket(36, stack)
        val buf = PacketByteBuf(Unpooled.buffer())
        packet.write(buf)

        val id = NetworkState.PLAY.getPacketId(NetworkSide.SERVERBOUND, packet) ?: return null
        val wrapper = PacketWrapperImpl(id, buf, info)
        wrapper.apply(Direction.SERVERBOUND, State.PLAY, 0, path.map { it.protocol() })

        // Hack: get the first Item from the packet wrapper, sadly there is no method for that
        @Suppress("KotlinConstantConditions")
        return (wrapper as IPacketWrapperImpl_Protocol).protocolhack_getReadableObjects().first { Item::class.java.equals(it.key()!!.outputClass) }.value() as Item?
    }
}
