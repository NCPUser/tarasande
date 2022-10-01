package de.florianmichael.tarasande.screen

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import de.florianmichael.tarasande.util.render.RenderUtil
import de.florianmichael.tarasande.screen.element.ScreenBetterSlotList
import de.florianmichael.tarasande.screen.element.ScreenBetterSlotListEntry
import de.florianmichael.tarasande.screen.element.ScreenBetterSlotListWidget
import de.florianmichael.viaprotocolhack.util.VersionList
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.minecraft.text.TextColor
import net.minecraft.util.Formatting
import su.mandora.tarasande.TarasandeMain
import java.awt.Color

class ScreenBetterProtocolHack(parent: Screen) : ScreenBetterSlotList(parent, 46, 12, object : ScreenBetterSlotListWidget.ListProvider {

        override fun get() = VersionList.getProtocols().map { p -> ProtocolEntry(p) }
}) {

    override fun init() {
        super.init()

        this.addDrawableChild(ButtonWidget(5, this.height - 25, 20, 20, Text.literal("<-")) {
            this.close()
        })

        this.addDrawableChild(ButtonWidget(5, 5, 98, 20, this.generateAutoDetectText()) {
            TarasandeMain.get().protocolHack.toggleAuto()
            it.message = this.generateAutoDetectText()
        })
    }

    private fun generateAutoDetectText() = Text.literal("Auto Detect").styled { it.withColor(TextColor.fromRgb((if (TarasandeMain.get().protocolHack.isAuto()) Color.green.rgb else Color.red.rgb))) }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(matrices, mouseX, mouseY, delta)

        this.renderTitle("Protocol Hack")
    }

    class ProtocolEntry(val protocol: ProtocolVersion) : ScreenBetterSlotListEntry() {

        override fun isSelected(): Boolean {
            return TarasandeMain.get().protocolHack.clientsideVersion() == this.protocol.version
        }

        private fun colorShift(input: Color): Int {
            return if (TarasandeMain.get().protocolHack.isAuto()) input.darker().darker().darker().rgb else input.rgb
        }

        override fun renderEntry(matrices: MatrixStack, index: Int, entryWidth: Int, entryHeight: Int, mouseX: Int, mouseY: Int, hovered: Boolean) {
            RenderUtil.useMyStack(matrices)
            RenderUtil.textCenter(Text.literal(this.protocol.name), entryWidth.toFloat(), 0F, if (this.isSelected()) colorShift(Color.green) else colorShift(Color.red))
            RenderUtil.ourStack()
        }

        override fun onClickEntry(mouseX: Double, mouseY: Double, mouseButton: Int): Boolean {
            TarasandeMain.get().protocolHack.setVersion(this.protocol.version)
            return true
        }
    }
}
