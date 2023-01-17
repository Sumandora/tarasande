package de.florianmichael.tarasande_protocol_hack.fix.chatsession.v1_19_2

import com.viaversion.viaversion.api.platform.providers.Provider

open class CommandArgumentsProvider : Provider {

    open fun getSignedArguments(command: String): List<Pair<String, String>> {
        return listOf()
    }
}
