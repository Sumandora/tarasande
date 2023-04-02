package su.mandora.tarasande_creative_features.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.minecraft.block.entity.CommandBlockBlockEntity
import net.minecraft.command.CommandSource
import net.minecraft.network.packet.c2s.play.UpdateCommandBlockC2SPacket
import net.minecraft.util.math.BlockPos
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.feature.commandsystem.Command

class CommandCheckCMDBlock : Command("checkcmdblock") {

    override fun builder(builder: LiteralArgumentBuilder<CommandSource>): LiteralArgumentBuilder<CommandSource> {
        return builder.executes {
            mc.networkHandler!!.sendPacket(UpdateCommandBlockC2SPacket(BlockPos.ORIGIN, "help", CommandBlockBlockEntity.Type.AUTO, false, false, false))
            return@executes SUCCESS
        }
    }
}
