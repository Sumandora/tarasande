package su.mandora.tarasande_nbt_edit

import com.mcf.davidee.nbtedit.NBTEdit
import com.mcf.davidee.nbtedit.gui.GuiEditNBTTree
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.fabricmc.api.ClientModInitializer
import net.minecraft.command.CommandSource
import net.minecraft.command.argument.NbtCompoundArgumentType
import net.minecraft.nbt.NbtCompound
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventSuccessfulLoad
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.feature.commandsystem.Command
import su.mandora.tarasande.system.feature.commandsystem.ManagerCommand

class TarasandeNBTEdit : ClientModInitializer {

    private fun editNBT(tag: NbtCompound) {
        RenderSystem.recordRenderCall {
            mc.setScreen(GuiEditNBTTree(mc.player!!.id, tag))
        }
    }

    override fun onInitializeClient() {
        EventDispatcher.add(EventSuccessfulLoad::class.java) {
            NBTEdit.init()

            ManagerCommand.add(object : Command("nbtedit") {
                override fun builder(builder: LiteralArgumentBuilder<CommandSource>): LiteralArgumentBuilder<CommandSource> {
                    builder.then(literal("self").executes {
                        val nbt = NbtCompound()
                        mc.player!!.writeNbt(nbt)
                        editNBT(nbt)
                        return@executes SUCCESS
                    })

                    builder.then(literal("nbt").then(argument("nbt", NbtCompoundArgumentType.nbtCompound())!!.executes {
                        editNBT(it.getArgument("nbt", NbtCompound::class.java))
                        return@executes SUCCESS
                    }))

                    return builder.executes {
                        mc.player!!.inventory.mainHandStack?.apply {
                            if (this.nbt != null) editNBT(this.nbt!!)
                        }
                        editNBT(NbtCompound())
                        return@executes SUCCESS
                    }
                }
            })
        }
    }
}
