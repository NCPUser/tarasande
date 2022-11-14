package net.tarasandedevelopment.tarasande.systems.feature.screenextensionsystem.impl.accountmanager.screenextension

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen
import net.tarasandedevelopment.tarasande.systems.feature.screenextensionsystem.ScreenExtension
import net.tarasandedevelopment.tarasande.systems.feature.screenextensionsystem.impl.accountmanager.ScreenBetterSlotListAccountManager

class ScreenExtensionAccountManager : ScreenExtension("Account Manager", MultiplayerScreen::class.java) {

    val screenBetterSlotListAccountManager = ScreenBetterSlotListAccountManager()

    override fun onClick(current: Screen) {
        MinecraftClient.getInstance().setScreen(this.screenBetterSlotListAccountManager.apply { prevScreen = MinecraftClient.getInstance().currentScreen })
    }
}
