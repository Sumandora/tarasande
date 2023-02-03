package de.florianmichael.tarasande_protocol_hack.tarasande.account

import com.mojang.authlib.minecraft.MinecraftSessionService
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
import de.florianmichael.tarasande_protocol_hack.xbox.XboxLiveSession
import net.tarasandedevelopment.tarasande.system.screen.accountmanager.account.api.AccountInfo
import net.tarasandedevelopment.tarasande.system.screen.accountmanager.account.impl.microsoft.*
import java.net.*
import java.util.*

@AccountInfo("Bedrock")
/**
 * @see de.florianmichael.tarasande_protocol_hack.injection.mixin.tarasande.account.MixinAccountMicrosoft
 */
class AccountBedrock : AccountMicrosoft() {

    private var service: MinecraftSessionService? = null

    override fun logIn() {
        if(email.isNotEmpty() && password.isEmpty()) {
            session = XboxLiveSession(email, UUID.randomUUID())
            service = YggdrasilAuthenticationService(Proxy.NO_PROXY, "", environment).createMinecraftSessionService()
            return
        }
        super.logIn()
    }

    override fun getSessionService() = service ?: super.getSessionService()

    override fun getDisplayName() = if (session == null && email.isEmpty()) "Unnamed Bedrock-account" else super.getDisplayName()
}
