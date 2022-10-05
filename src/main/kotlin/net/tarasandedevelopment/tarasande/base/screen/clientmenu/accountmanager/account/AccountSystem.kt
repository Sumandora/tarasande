package net.tarasandedevelopment.tarasande.base.screen.clientmenu.accountmanager.account

import com.google.gson.JsonArray
import com.mojang.authlib.Environment
import com.mojang.authlib.minecraft.MinecraftSessionService
import net.minecraft.client.util.Session
import net.tarasandedevelopment.tarasande.base.Manager
import net.tarasandedevelopment.tarasande.screen.clientmenu.accountmanager.account.*
import net.tarasandedevelopment.tarasande.screen.list.accountmanager.account.*

class ManagerAccount : Manager<Class<out Account>>() {
    init {
        add(
            AccountSession::class.java,
            AccountYggdrasil::class.java,
            AccountMicrosoft::class.java,
            AccountRefreshToken::class.java,
            AccountToken::class.java
        )
    }
}

abstract class Account {
    var environment: Environment? = null
    var session: Session? = null

    abstract fun logIn()
    abstract fun getDisplayName(): String
    abstract fun getSessionService(): MinecraftSessionService?

    abstract fun save(): JsonArray
    abstract fun load(jsonArray: JsonArray): Account

    abstract fun create(credentials: List<String>): Account

    fun isSuitableAsMain() = (javaClass.declaredAnnotations[0] as AccountInfo).suitableAsMain
}