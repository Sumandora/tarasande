package net.tarasandedevelopment.tarasande.base.screen.clientmenu.accountmanager.environment

import com.mojang.authlib.Environment
import net.tarasandedevelopment.tarasande.base.Manager
import net.tarasandedevelopment.tarasande.screen.clientmenu.accountmanager.environment.EnvironmentPresetEasyMC
import net.tarasandedevelopment.tarasande.screen.clientmenu.accountmanager.environment.EnvironmentPresetMojang
import net.tarasandedevelopment.tarasande.screen.clientmenu.accountmanager.environment.EnvironmentPresetTheAltening

class ManagerEnvironment : Manager<EnvironmentPreset>() {
    init {
        add(
            EnvironmentPresetMojang(),
            EnvironmentPresetTheAltening(),
            EnvironmentPresetEasyMC()
        )
    }
}

abstract class EnvironmentPreset(val name: String, val authHost: String, val accountsHost: String, val sessionHost: String, val servicesHost: String) {

    fun create() = Environment.create(this.authHost, this.accountsHost, this.sessionHost, this.servicesHost, this.name)
}