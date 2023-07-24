package su.mandora.tarasande.system.screen.accountmanager.account

import com.google.gson.JsonArray
import com.mojang.authlib.Environment
import com.mojang.authlib.minecraft.MinecraftSessionService
import com.mojang.authlib.yggdrasil.YggdrasilEnvironment
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.Session
import su.mandora.tarasande.Manager
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.screen.accountmanager.account.api.ExtraInfo
import su.mandora.tarasande.system.screen.accountmanager.account.impl.AccountSession
import su.mandora.tarasande.system.screen.accountmanager.account.impl.AccountYggdrasil
import su.mandora.tarasande.system.screen.accountmanager.account.impl.microsoft.AccountMicrosoft
import su.mandora.tarasande.system.screen.accountmanager.account.impl.microsoft.AccountMicrosoftRefreshToken
import su.mandora.tarasande.system.screen.screenextensionsystem.impl.multiplayer.accountmanager.subscreen.ScreenBetterEnvironment
import su.mandora.tarasande.util.render.skin.SkinRenderer

object ManagerAccount : Manager<Class<out Account>>() {
    init {
        add(
            AccountSession::class.java,
            AccountYggdrasil::class.java,
            AccountMicrosoft::class.java,
            AccountMicrosoftRefreshToken::class.java
        )
    }
}

abstract class Account {
    var environment: Environment = YggdrasilEnvironment.PROD.environment
    var session: Session? = null
        set(value) {
            if (value != null)
                skin = SkinRenderer(value.uuidOrNull, value.username)
            field = value
        }
    var service: MinecraftSessionService? = null
    var status: String? = null
    var skin: SkinRenderer? = null

    abstract fun logIn()
    abstract fun getDisplayName(): String

    abstract fun save(): JsonArray?
    abstract fun load(jsonArray: JsonArray): Account

    abstract fun create(credentials: List<String>)

    @ExtraInfo("Environment")
    val environmentExtra: (Screen, Runnable) -> Unit = { screen, _ ->
        mc.setScreen(ScreenBetterEnvironment(screen, environment) { newEnvironment ->
            environment = newEnvironment
        })
    }

    fun ready() = session != null && service != null
}
