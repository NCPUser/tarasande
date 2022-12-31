package net.tarasandedevelopment.tarasande_protocol_hack.util.values

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import net.tarasandedevelopment.tarasande_protocol_hack.util.extension.compareTo
import net.tarasandedevelopment.tarasande_protocol_hack.util.extension.getSpecialName

fun formatRange(vararg version: ProtocolRange) = version.joinToString(", ") { it.toString() }

class ProtocolRange(private val lowerBound: ProtocolVersion?, private val upperBound: ProtocolVersion?) {

    init {
        if (lowerBound == null && upperBound == null)
            error("Invalid protocol range")
    }

    operator fun contains(protocolVersion: ProtocolVersion): Boolean {
        if (lowerBound != null && lowerBound < protocolVersion)
            return false
        if (upperBound != null && upperBound > protocolVersion)
            return false
        return true
    }

    override fun toString(): String {
        return when {
            lowerBound == null -> upperBound!!.getSpecialName() + "+"
            upperBound == null -> lowerBound.getSpecialName() + "-"
            lowerBound == upperBound -> lowerBound.getSpecialName()
            else -> lowerBound.getSpecialName() + " - " + upperBound.getSpecialName()
        }
    }
}
