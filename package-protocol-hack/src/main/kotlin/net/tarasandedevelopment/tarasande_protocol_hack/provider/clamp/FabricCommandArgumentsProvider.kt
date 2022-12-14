package net.tarasandedevelopment.tarasande_protocol_hack.provider.clamp

import net.minecraft.client.MinecraftClient
import net.minecraft.command.argument.SignedArgumentList
import net.tarasandedevelopment.tarasande_protocol_hack.fix.chatsession.v1_19_2.CommandArgumentsProvider

class FabricCommandArgumentsProvider : CommandArgumentsProvider() {

    override fun getSignedArguments(command: String): List<Pair<String, String>> {
        val clientPlayNetworkHandler = MinecraftClient.getInstance().networkHandler
        if (clientPlayNetworkHandler != null) {
            return SignedArgumentList.of(
                clientPlayNetworkHandler.commandDispatcher.parse(
                    command, clientPlayNetworkHandler.commandSource
                )
            ).arguments().map { Pair(it.nodeName, it.value) }
        }
        return super.getSignedArguments(command)
    }
}
