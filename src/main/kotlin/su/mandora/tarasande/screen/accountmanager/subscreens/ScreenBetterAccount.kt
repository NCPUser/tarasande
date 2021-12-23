package su.mandora.tarasande.screen.accountmanager.subscreens

import com.mojang.authlib.Environment
import com.mojang.authlib.yggdrasil.YggdrasilEnvironment
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import org.lwjgl.glfw.GLFW
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.base.screen.accountmanager.account.Account
import su.mandora.tarasande.base.screen.accountmanager.account.AccountInfo
import su.mandora.tarasande.base.screen.accountmanager.account.TextFieldInfo
import su.mandora.tarasande.mixin.accessor.IScreen
import su.mandora.tarasande.screen.accountmanager.elements.TextFieldWidgetPassword
import su.mandora.tarasande.screen.accountmanager.elements.TextFieldWidgetPlaceholder
import su.mandora.tarasande.util.render.screen.ScreenBetter
import java.lang.reflect.Constructor
import java.util.function.Consumer


class ScreenBetterAccount(
	prevScreen: Screen,
	val name: String,
	private val accountConsumer: Consumer<Account>,
) : ScreenBetter(prevScreen) {

	private val textFields: ArrayList<TextFieldWidget> = ArrayList()
	private var implementationClass: Class<out Account> = TarasandeMain.get().screens?.betterScreenAccountManager?.managerAccount?.list?.get(0)!!

	private var environment: Environment? = null

	private var submitButton: ButtonWidget? = null

	override fun init() {
		textFields.clear()
		children().clear()
		(this as IScreen).drawables.clear()
		(this as IScreen).selectables.clear()

		addDrawableChild(ButtonWidget(width - 100, 0, 100, 20, Text.of((implementationClass.annotations[0] as AccountInfo).name)) { button ->
			implementationClass = TarasandeMain.get().screens?.betterScreenAccountManager?.managerAccount?.list!![(TarasandeMain.get().screens?.betterScreenAccountManager?.managerAccount?.list!!.indexOf(implementationClass) + 1) % TarasandeMain.get().screens?.betterScreenAccountManager?.managerAccount?.list!!.size]
			init()
			button.message = Text.of(implementationClass.name)
		})

		addDrawableChild(ButtonWidget(width - 200, 0, 100, 20, Text.of("Environment")) { client?.setScreen(ScreenBetterEnvironment(this, environment) { environment = it }) })

		var constructor: Constructor<*>? = null
		for (c in implementationClass.constructors) {
			if (constructor == null || c.parameters.size > constructor.parameters.size) {
				constructor = c
			}
		}
		val parameters = constructor!!.parameters
		for (i in parameters.indices) {
			val parameterType = parameters[i]
			if (parameterType.isAnnotationPresent(TextFieldInfo::class.java)) {
				val textFieldInfo: TextFieldInfo = parameterType.getAnnotation(TextFieldInfo::class.java)
				if (textFieldInfo.hidden) {
					textFields.add(addDrawableChild(TextFieldWidgetPassword(textRenderer, width / 2 - 75, (height * 0.25f + i * 25).toInt(), 150, 20, Text.of(textFieldInfo.name)).also { it.setMaxLength(Integer.MAX_VALUE) }))
				} else {
					textFields.add(addDrawableChild(TextFieldWidgetPlaceholder(textRenderer, width / 2 - 75, (height * 0.25f + i * 25).toInt(), 150, 20, Text.of(textFieldInfo.name)).also { it.setMaxLength(Integer.MAX_VALUE) }))
				}
			}
		}

		addDrawableChild(ButtonWidget(width / 2 - 50, (height * 0.75f - 25).toInt(), 100, 20, Text.of(name)) {
			val account = (implementationClass.newInstance() as Account).create(textFields.map { it.text })
			account.environment = environment ?: YggdrasilEnvironment.PROD.environment
			accountConsumer.accept(account)
			onClose()
		}.also { submitButton = it })

		addDrawableChild(ButtonWidget(width / 2 - 50, (height * 0.75f).toInt(), 100, 20, Text.of("Back")) { onClose() })

		super.init()
	}

	override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
		super.render(matrices, mouseX, mouseY, delta)
		drawCenteredText(matrices, textRenderer, name, width / 2, 8 - textRenderer.fontHeight / 2, -1)
	}

	override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
		var focused = false
		for (textField in textFields)
			if (textField.isFocused)
				focused = true
		if (hasControlDown() && keyCode == GLFW.GLFW_KEY_V && !focused) {
			val clipboardContent = GLFW.glfwGetClipboardString(client?.window?.handle!!)
			if (clipboardContent != null) {
				val parts = clipboardContent.split(":")
				if (parts.size == textFields.size)
					for ((index, textField) in textFields.withIndex())
						textField.text = parts[index]
			}
		}
		if (keyCode == GLFW.GLFW_KEY_ENTER)
			submitButton?.onPress()
		return super.keyPressed(keyCode, scanCode, modifiers)
	}

}