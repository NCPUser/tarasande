package net.tarasandedevelopment.tarasande.module.misc

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.screen.AbstractFurnaceScreenHandler
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.event.Event
import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.base.screen.cheatmenu.valuecomponent.ValueComponent
import net.tarasandedevelopment.tarasande.event.EventChildren
import net.tarasandedevelopment.tarasande.screen.cheatmenu.panel.impl.elements.PanelElements
import net.tarasandedevelopment.tarasande.screen.widget.panel.ClickableWidgetPanel
import net.tarasandedevelopment.tarasande.value.ValueColor
import net.tarasandedevelopment.tarasande.value.ValueSpacer
import java.util.function.Consumer

class ModuleFurnaceProgress : Module("Furnace progress", "Indicates the progress in the furnace.", ModuleCategory.MISC) {

    val eventConsumer = Consumer<Event> {
        when (it) {
            is EventChildren -> {
                if (it.screen !is HandledScreen<*>) return@Consumer
                if (it.screen.screenHandler !is AbstractFurnaceScreenHandler) return@Consumer

                val screenHandler = it.screen.screenHandler as AbstractFurnaceScreenHandler
                val font = MinecraftClient.getInstance().textRenderer

                it.add(ClickableWidgetPanel(object : PanelElements<ValueComponent>("Furnace Progress", 0.0, 0.0, 0.0, 0.0) {
                    override fun init() {
                        super.init()
                        x = 5.0
                        panelWidth = 100.0
                    }

                    private fun addText(input: String) = elementList.add(0, TarasandeMain.get().screenCheatMenu.managerValueComponent.newInstance(ValueSpacer(this, input))!!)

                    override fun renderContent(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
                        super.renderContent(matrices, mouseX, mouseY, delta)
                        elementList.clear()
                        y = MinecraftClient.getInstance().window.scaledHeight / 2f - panelHeight / 2f

                        if (screenHandler.isBurning) {
                            val progress = 23 /* max */ - screenHandler.cookProgress

                            addText("Item smelting finished in: " + (progress / 2 + 1) + " seconds")
                            addText("Fuel Power ends in: " + (screenHandler.fuelProgress + 1) + " seconds")

                            return
                        }

                        addText("Waiting...")
                    }
                }, true))
            }
        }
    }
}
