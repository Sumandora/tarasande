package de.florianmichael.tarasande_creative_features.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.minecraft.block.entity.CommandBlockBlockEntity
import net.minecraft.command.CommandSource
import net.minecraft.network.packet.c2s.play.UpdateCommandBlockC2SPacket
import net.minecraft.util.math.BlockPos
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.feature.commandsystem.Command

class CommandCheckCMDBlock : Command("checkcmdblock") {

    override fun builder(builder: LiteralArgumentBuilder<CommandSource>): LiteralArgumentBuilder<CommandSource> {
        return builder.executes {
            mc.networkHandler!!.sendPacket(UpdateCommandBlockC2SPacket(BlockPos.ORIGIN, "help", CommandBlockBlockEntity.Type.AUTO, false, false, false))
            return@executes SUCCESS
        }
    }
}
