package su.mandora.tarasande.system.screen.accountmanager.environment

import com.mojang.authlib.Environment
import su.mandora.tarasande.Manager
import su.mandora.tarasande.system.screen.accountmanager.environment.impl.EnvironmentPresetEasyMC
import su.mandora.tarasande.system.screen.accountmanager.environment.impl.EnvironmentPresetMojang
import su.mandora.tarasande.system.screen.accountmanager.environment.impl.EnvironmentPresetShadowGen
import su.mandora.tarasande.system.screen.accountmanager.environment.impl.EnvironmentPresetTheAltening

object ManagerEnvironment : Manager<EnvironmentPreset>() {
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