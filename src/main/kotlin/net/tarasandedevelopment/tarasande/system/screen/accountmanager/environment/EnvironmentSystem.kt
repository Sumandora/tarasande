package net.tarasandedevelopment.tarasande.system.screen.accountmanager.environment

import com.mojang.authlib.Environment
import net.tarasandedevelopment.tarasande.Manager
import net.tarasandedevelopment.tarasande.system.screen.accountmanager.environment.impl.EnvironmentPresetEasyMC
import net.tarasandedevelopment.tarasande.system.screen.accountmanager.environment.impl.EnvironmentPresetMojang
import net.tarasandedevelopment.tarasande.system.screen.accountmanager.environment.impl.EnvironmentPresetShadowGen
import net.tarasandedevelopment.tarasande.system.screen.accountmanager.environment.impl.EnvironmentPresetTheAltening

class ManagerEnvironment : Manager<EnvironmentPreset>() {
    init {
        add(
            EnvironmentPresetMojang(),
            EnvironmentPresetTheAltening(),
            EnvironmentPresetEasyMC(),
            EnvironmentPresetShadowGen()
        )
    }
}

abstract class EnvironmentPreset(val name: String, val authHost: String, val accountsHost: String, val sessionHost: String, val servicesHost: String) {

    fun create(): Environment = Environment.create(this.authHost, this.accountsHost, this.sessionHost, this.servicesHost, this.name)
}