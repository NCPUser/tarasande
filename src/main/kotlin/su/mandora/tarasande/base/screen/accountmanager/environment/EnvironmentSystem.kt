package su.mandora.tarasande.base.screen.accountmanager.environment

import su.mandora.tarasande.base.Manager
import su.mandora.tarasande.screen.accountmanager.environment.EnvironmentPresetEasyMC
import su.mandora.tarasande.screen.accountmanager.environment.EnvironmentPresetMojang
import su.mandora.tarasande.screen.accountmanager.environment.EnvironmentPresetTheAltening

class ManagerEnvironment : Manager<EnvironmentPreset>() {
    init {
        add(
            EnvironmentPresetMojang(),
            EnvironmentPresetTheAltening(),
            EnvironmentPresetEasyMC()
        )
    }
}

abstract class EnvironmentPreset(
    val name: String,
    val authHost: String,
    val accountsHost: String,
    val sessionHost: String,
    val servicesHost: String
)