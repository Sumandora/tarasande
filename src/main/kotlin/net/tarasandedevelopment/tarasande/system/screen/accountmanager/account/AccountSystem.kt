package net.tarasandedevelopment.tarasande.system.screen.accountmanager.account

import com.google.gson.JsonArray
import com.mojang.authlib.Environment
import com.mojang.authlib.minecraft.MinecraftSessionService
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.Session
import net.tarasandedevelopment.tarasande.Manager
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.screen.accountmanager.account.api.ExtraInfo
import net.tarasandedevelopment.tarasande.system.screen.accountmanager.account.impl.AccountSession
import net.tarasandedevelopment.tarasande.system.screen.accountmanager.account.impl.AccountToken
import net.tarasandedevelopment.tarasande.system.screen.accountmanager.account.impl.AccountYggdrasil
import net.tarasandedevelopment.tarasande.system.screen.accountmanager.account.impl.microsoft.AccountMicrosoft
import net.tarasandedevelopment.tarasande.system.screen.accountmanager.account.impl.microsoft.AccountMicrosoftRefreshToken
import net.tarasandedevelopment.tarasande.system.screen.accountmanager.environment.ManagerEnvironment
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.multiplayer.accountmanager.subscreen.ScreenBetterEnvironment

object ManagerAccount : Manager<Class<out Account>>() {
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
    var environment: Environment = ManagerEnvironment.list.first().create()
    var session: Session? = null

    abstract fun logIn()
    abstract fun getDisplayName(): String
    abstract fun getSessionService(): MinecraftSessionService?

    abstract fun save(): JsonArray
    abstract fun load(jsonArray: JsonArray): Account

    abstract fun create(credentials: List<String>)

    @ExtraInfo("Environment")
    open val environmentExtra: (Screen) -> Unit = {
        mc.setScreen(ScreenBetterEnvironment(it, environment) { newEnvironment ->
            environment = newEnvironment
        })
    }
}
