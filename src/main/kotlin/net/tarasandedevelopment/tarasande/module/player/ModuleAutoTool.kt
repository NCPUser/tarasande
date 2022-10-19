package net.tarasandedevelopment.tarasande.module.player

import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.item.SwordItem
import net.minecraft.util.hit.BlockHitResult
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventAttackEntity
import net.tarasandedevelopment.tarasande.event.EventUpdate
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil
import net.tarasandedevelopment.tarasande.value.ValueMode

class ModuleAutoTool : Module("Auto tool", "Selects the best tool for breaking a block", ModuleCategory.PLAYER) {

    private val mode = ValueMode(this, "Mode", true, "Blocks", "Entities")

    init {
        registerEvent(EventUpdate::class.java) { event ->
            if (event.state == EventUpdate.State.PRE) {
                if (!mode.isSelected(0))
                    return@registerEvent
                if (!mc.interactionManager?.isBreakingBlock!!)
                    return@registerEvent
                if (mc.crosshairTarget !is BlockHitResult)
                    return@registerEvent
                val blockPos = (mc.crosshairTarget as BlockHitResult).blockPos
                val pair = PlayerUtil.getBreakSpeed(blockPos)

                if (pair.second == mc.player?.inventory?.selectedSlot)
                    return@registerEvent

                val currentSpeed = PlayerUtil.getBreakSpeed(blockPos, mc.player?.inventory?.selectedSlot ?: return@registerEvent)
                if (currentSpeed == pair.first)
                    return@registerEvent

                val bestTool = pair.second
                if (bestTool == -1)
                    return@registerEvent
                mc.player?.inventory?.selectedSlot = bestTool
            }
        }

        registerEvent(EventAttackEntity::class.java) { event ->
            if (event.state == EventAttackEntity.State.PRE) {
                if (!mode.isSelected(1))
                    return@registerEvent

                var best: Int? = null
                var score = 0.0f
                for (i in 0..8) {
                    val stack = mc.player?.inventory?.main?.get(i)
                    if (stack != null && !stack.isEmpty && stack.item is SwordItem) {
                        val newScore = (stack.item as SwordItem).material.attackDamage + EnchantmentHelper.get(stack).values.sum()
                        if (best == null || newScore > score) {
                            best = i
                            score = newScore
                        }
                    }
                }
                mc.player?.inventory?.selectedSlot = best
            }
        }
    }
}