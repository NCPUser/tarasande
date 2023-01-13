package net.tarasandedevelopment.tarasande_chat_features.module

import net.minecraft.network.encryption.PlayerKeyPair
import net.minecraft.text.Text
import net.tarasandedevelopment.tarasande.event.EventDisconnect
import net.tarasandedevelopment.tarasande.event.EventTick
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory
import net.tarasandedevelopment.tarasande.system.screen.informationsystem.Information
import net.tarasandedevelopment.tarasande.system.screen.informationsystem.ManagerInformation
import net.tarasandedevelopment.tarasande.util.player.chat.CustomChat.printChatMessage
import net.tarasandedevelopment.tarasande.util.string.StringUtil
import net.tarasandedevelopment.tarasande_chat_features.gatekeep.GatekeepTracker
import java.time.Instant
import java.util.function.Consumer

class ModulePublicKeyKicker : Module("Public key kicker", "Kicks players using outdated key signatures", ModuleCategory.EXPLOIT) {

    private var gatekeepTracker: GatekeepTracker? = null
    private var hasNotified = false

    init {
        gatekeepTracker = GatekeepTracker(mc.userApiService, mc.session.uuidOrNull, mc.runDirectory.toPath())
        gatekeepTracker?.getOldestValidKey()?.run {
            mc.profileKeys = this
        }

        registerEvent(EventDisconnect::class.java) {
            hasNotified = false
        }

        registerEvent(EventTick::class.java) {
            if (it.state != EventTick.State.PRE) return@registerEvent

            mc.profileKeys.fetchKeyPair().get().ifPresent(Consumer { key: PlayerKeyPair ->
                if (key.isExpired) {
                    if (!hasNotified) {
                        printChatMessage(Text.literal(
                                "Your public key has now expired! Anyone who joins after this message will be disconnected when you chat"
                        ))
                        hasNotified = true
                    }
                }
            })
        }

        ManagerInformation.add(object : Information(name, "Expiration time") {
            override fun getMessage(): String? {
                if (!enabled.value) return null

                val keyData = mc.profileKeys.fetchKeyPair().get()
                if (keyData.isPresent) {
                    return StringUtil.formatTime(keyData.get().refreshedAfter.toEpochMilli() - Instant.now().toEpochMilli())
                }
                return null
            }
        })
    }
}
