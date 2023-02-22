package net.tarasandedevelopment.tarasande.system.screen.graphsystem.impl.rotation

import net.tarasandedevelopment.tarasande.event.EventDispatcher
import net.tarasandedevelopment.tarasande.event.impl.EventDisconnect
import net.tarasandedevelopment.tarasande.event.impl.EventUpdate
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.screen.graphsystem.Graph

class GraphPitchDelta : Graph("Player", "Pitch Delta", 200, false) {
    private var lastPitch: Float? = null

    init {
        EventDispatcher.apply {
            add(EventUpdate::class.java) { event ->
                if (event.state == EventUpdate.State.POST) {
                    val lastPitch = mc.player?.lastPitch ?: return@add
                    if (this@GraphPitchDelta.lastPitch != null)
                        add(lastPitch - this@GraphPitchDelta.lastPitch!!)
                    this@GraphPitchDelta.lastPitch = lastPitch
                }
            }
            add(EventDisconnect::class.java) { event ->
                if (event.connection == mc.networkHandler?.connection) {
                    lastPitch = null
                    clear()
                }
            }
        }
    }
}