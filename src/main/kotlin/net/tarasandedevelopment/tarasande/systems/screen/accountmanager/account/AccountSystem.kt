package net.tarasandedevelopment.tarasande.systems.screen.accountmanager.account

import com.google.gson.JsonArray
import com.mojang.authlib.Environment
import com.mojang.authlib.minecraft.MinecraftSessionService
import com.mojang.authlib.yggdrasil.YggdrasilEnvironment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.Session
import net.tarasandedevelopment.tarasande.Manager
import net.tarasandedevelopment.tarasande.systems.screen.accountmanager.account.api.ExtraInfo
import net.tarasandedevelopment.tarasande.systems.screen.accountmanager.account.impl.AccountSession
import net.tarasandedevelopment.tarasande.systems.screen.accountmanager.account.impl.AccountToken
import net.tarasandedevelopment.tarasande.systems.screen.accountmanager.account.impl.AccountYggdrasil
import net.tarasandedevelopment.tarasande.systems.screen.accountmanager.account.impl.microsoft.AccountMicrosoft
import net.tarasandedevelopment.tarasande.systems.screen.accountmanager.account.impl.microsoft.AccountMicrosoftRefreshToken
import net.tarasandedevelopment.tarasande.systems.screen.clientmenu.clientmenu.accountmanager.subscreen.ScreenBetterEnvironment
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
    var environment: Environment? = null
    var session: Session? = null
        set(value) {
            if (value != null)
                skinRenderer = SkinRenderer(value.profile)
            field = value
        }
    var skinRenderer: SkinRenderer? = null

    open fun defaultEnvironment(): Environment = YggdrasilEnvironment.PROD.environment

    abstract fun logIn()
    abstract fun getDisplayName(): String
    abstract fun getSessionService(): MinecraftSessionService?

    abstract fun save(): JsonArray
    abstract fun load(jsonArray: JsonArray): Account

    abstract fun create(credentials: List<String>)

    @ExtraInfo("Environment")
    open val environmentExtra = object : Extra {
        override fun click(prevScreen: Screen) {
            MinecraftClient.getInstance().setScreen(ScreenBetterEnvironment(prevScreen, environment) {
                environment = it
            })
        }
    }

    interface Extra {
        fun click(prevScreen: Screen)
    }
}