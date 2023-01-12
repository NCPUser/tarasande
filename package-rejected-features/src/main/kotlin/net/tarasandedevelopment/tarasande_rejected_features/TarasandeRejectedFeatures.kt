package net.tarasandedevelopment.tarasande_rejected_features

import net.fabricmc.api.ClientModInitializer
import net.tarasandedevelopment.tarasande.event.EventSuccessfulLoad
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ManagerModule
import net.tarasandedevelopment.tarasande.system.screen.informationsystem.ManagerInformation
import net.tarasandedevelopment.tarasande_rejected_features.information.*
import net.tarasandedevelopment.tarasande_rejected_features.module.ModuleAutoRescuePlatform
import net.tarasandedevelopment.tarasande_rejected_features.module.ModuleDeadByDaylightEscape
import net.tarasandedevelopment.tarasande_rejected_features.module.ModuleFurnaceProgress
import net.tarasandedevelopment.tarasande_rejected_features.module.ModuleRoundedMovement
import su.mandora.event.EventDispatcher

class TarasandeRejectedFeatures : ClientModInitializer {

    override fun onInitializeClient() {
        EventDispatcher.add(EventSuccessfulLoad::class.java) {
            ManagerModule.add(
                ModuleDeadByDaylightEscape(),
                ModuleRoundedMovement(),
                ModuleFurnaceProgress(),
                ModuleAutoRescuePlatform()
            )

            ManagerInformation.add(
                // Time
                InformationDate(),
                InformationTime(),

                // Features
                InformationFeaturesModules(),
                InformationFeaturesValues(),
                InformationFeaturesGraphs(),
                InformationFeaturesPackagesForTarasande(),

                // KeyBinds
                InformationKeyBinds()
            )
        }
    }
}
