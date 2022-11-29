package net.tarasandedevelopment.tarasande.system.screen.accountmanager.account

import com.google.gson.JsonArray
import com.mojang.authlib.Environment
import com.mojang.authlib.minecraft.MinecraftSessionService
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.Session
import net.tarasandedevelopment.tarasande.Manager
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.system.screen.accountmanager.account.api.ExtraInfo
import net.tarasandedevelopment.tarasande.system.screen.accountmanager.account.impl.AccountSession
import net.tarasandedevelopment.tarasande.system.screen.accountmanager.account.impl.AccountToken
import net.tarasandedevelopment.tarasande.system.screen.accountmanager.account.impl.AccountYggdrasil
import net.tarasandedevelopment.tarasande.system.screen.accountmanager.account.impl.microsoft.AccountMicrosoft
import net.tarasandedevelopment.tarasande.system.screen.accountmanager.account.impl.microsoft.AccountMicrosoftRefreshToken
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.ScreenExtensionSidebarMultiplayerScreen
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.accountmanager.subscreen.ScreenBetterEnvironment
import net.tarasandedevelopment.tarasande.util.render.SkinRenderer

class ManagerAccount : Manager<Class<out Account>>() {
    init {
        add(
            AccountSession::class.java,
            AccountYggdrasil::class.java,
            AccountMicrosoft::class.java,
            AccountMicrosoftRefreshToken::class.java,
            AccountToken::class.java
        )
    }
}

abstract class Account {
    var environment: Environment = TarasandeMain.managerScreenExtension().get(ScreenExtensionSidebarMultiplayerScreen::class.java).screenBetterSlotListAccountManager.managerEnvironment.list.first().create()
    var session: Session? = null
        set(value) {
            if (value != null)
                skinRenderer = SkinRenderer(value.profile)
            field = value
        }
    var skinRenderer: SkinRenderer? = null

    abstract fun logIn()
    abstract fun getDisplayName(): String
    abstract fun getSessionService(): MinecraftSessionService?

    abstract fun save(): JsonArray
    abstract fun load(jsonArray: JsonArray): Account

    abstract fun create(credentials: List<String>)

    @ExtraInfo("Environment")
    open val environmentExtra: (Screen) -> Unit = {
        MinecraftClient.getInstance().setScreen(ScreenBetterEnvironment(it, environment) {
            environment = it
        })
    }
}
