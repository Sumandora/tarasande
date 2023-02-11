package de.florianmichael.tarasande_rejected_features.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.minecraft.command.CommandSource
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.feature.commandsystem.Command

class CommandDeadByDaylightEscape : Command("deadbydaylightescape") {
    override fun builder(builder: LiteralArgumentBuilder<CommandSource>): LiteralArgumentBuilder<CommandSource> {
        return builder.executes {
            for (i in 0..150) mc.networkHandler?.sendPacket(PlayerInputC2SPacket(if (i % 2 == 0) 1.0f else -1.0f, 0.0f, false, false))
            return@executes SUCCESS
        }
    }
}