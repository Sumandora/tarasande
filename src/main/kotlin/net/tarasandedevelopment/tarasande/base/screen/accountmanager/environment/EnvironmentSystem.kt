package net.tarasandedevelopment.tarasande.base.screen.accountmanager.environment

import net.tarasandedevelopment.tarasande.base.Manager
import net.tarasandedevelopment.tarasande.screen.list.accountmanager.environment.EnvironmentPresetEasyMC
import net.tarasandedevelopment.tarasande.screen.list.accountmanager.environment.EnvironmentPresetMojang
import net.tarasandedevelopment.tarasande.screen.list.accountmanager.environment.EnvironmentPresetTheAltening

class ManagerEnvironment : Manager<EnvironmentPreset>() {
    init {
        add(
            EnvironmentPresetMojang(),
            EnvironmentPresetTheAltening(),
            EnvironmentPresetEasyMC()
        )

        this.finishLoading()
    }
}

abstract class EnvironmentPreset(
    val name: String,
    val authHost: String,
    val accountsHost: String,
    val sessionHost: String,
    val servicesHost: String
)