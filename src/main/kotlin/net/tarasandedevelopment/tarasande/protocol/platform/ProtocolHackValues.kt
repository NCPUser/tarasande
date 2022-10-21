@file:Suppress("ClassName")

package net.tarasandedevelopment.tarasande.protocol.platform

import com.mojang.bridge.game.PackType
import net.minecraft.SharedConstants
import net.tarasandedevelopment.tarasande.value.ValueBoolean
import net.tarasandedevelopment.tarasande.value.ValueNumber
import net.tarasandedevelopment.tarasande.value.ValueText

object ProtocolHackValues {

    // General
    val filterItemGroups = ValueBoolean(this, "Filter item groups", true)
    val changeResourcePackDownloadHeaders = ValueBoolean(this, "Change resource pack download headers", false)
    val versionName = object : ValueText(this, "Version name", SharedConstants.getGameVersion().name) {
        override fun isEnabled() = changeResourcePackDownloadHeaders.value
    }
    val packFormat = object : ValueNumber(this, "Pack format", 0.0, SharedConstants.getGameVersion().getPackVersion(PackType.RESOURCE).toDouble(), SharedConstants.getGameVersion().getPackVersion(PackType.RESOURCE).toDouble(), 1.0) {
        override fun isEnabled() = changeResourcePackDownloadHeaders.value
    }

    // 1.14 -> 1.13.2
    val smoothOutMerchantScreens = ValueBoolean(this, "Smooth out merchant screens (1.14 -> 1.13.2)", true)
}