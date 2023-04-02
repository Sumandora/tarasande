package su.mandora.tarasande_rejected_features.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import io.netty.buffer.Unpooled
import net.minecraft.command.CommandSource
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket
import net.minecraft.util.Identifier
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.feature.commandsystem.Command
import su.mandora.tarasande.util.player.chat.CustomChat

class CommandSparkyHeal : Command("sparkyheal") {
    private val channel = Identifier("40413eb1")
    private val data = PacketByteBuf(Unpooled.wrappedBuffer(channel.toString().toByteArray()))

    override fun builder(builder: LiteralArgumentBuilder<CommandSource>): LiteralArgumentBuilder<CommandSource> {
        return builder.executes {
            mc.networkHandler!!.sendPacket(CustomPayloadC2SPacket(channel, data))
            CustomChat.printChatMessage("Executed code successfully!")
            return@executes SUCCESS
        }
    }
}
