package su.mandora.tarasande.base.screen.accountmanager.account

import com.google.gson.JsonArray
import com.mojang.authlib.Environment
import com.mojang.authlib.minecraft.MinecraftSessionService
import net.minecraft.client.util.Session
import su.mandora.tarasande.base.Manager
import su.mandora.tarasande.screen.accountmanager.account.AccountMicrosoft
import su.mandora.tarasande.screen.accountmanager.account.AccountSession
import su.mandora.tarasande.screen.accountmanager.account.AccountToken
import su.mandora.tarasande.screen.accountmanager.account.AccountYggdrasil

class ManagerAccount : Manager<Class<out Account>>() {
    init {
        add(
            AccountSession::class.java,
            AccountYggdrasil::class.java,
            AccountMicrosoft::class.java,
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