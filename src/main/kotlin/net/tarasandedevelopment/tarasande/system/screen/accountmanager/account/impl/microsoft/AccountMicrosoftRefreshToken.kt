package net.tarasandedevelopment.tarasande.system.screen.accountmanager.account.impl.microsoft

import net.minecraft.client.gui.screen.Screen
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.screen.accountmanager.account.api.AccountInfo
import net.tarasandedevelopment.tarasande.system.screen.accountmanager.account.api.ExtraInfo
import net.tarasandedevelopment.tarasande.system.screen.accountmanager.account.api.TextFieldInfo
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.multiplayer.accountmanager.subscreen.ScreenBetterAzureApps

@AccountInfo("Refresh-Token", inherit = false)
class AccountMicrosoftRefreshToken : AccountMicrosoft() {

    @TextFieldInfo("Refresh-Token", true)
    private var token = ""

    override fun logIn() {
        msAuthProfile = buildFromRefreshToken(token)

        super.logIn()
    }

    override fun getDisplayName(): String {
        return if (session == null) "Unnamed Refresh-Token account" else super.getDisplayName()
    }

    override fun create(credentials: List<String>) {
        token = credentials[0]
    }

    @Suppress("unused")
    @ExtraInfo("Azure Apps")
    val readdAzureApps: (Screen) -> Unit = {
        mc.setScreen(ScreenBetterAzureApps(it, azureApp) { newAzureApp ->
            azureApp = newAzureApp
        })
    }
}
