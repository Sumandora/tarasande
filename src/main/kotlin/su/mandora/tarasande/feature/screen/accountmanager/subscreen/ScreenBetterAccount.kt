package su.mandora.tarasande.feature.screen.accountmanager.subscreen

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.text.Text
import org.lwjgl.glfw.GLFW
import su.mandora.tarasande.feature.screen.ScreenBetter
import su.mandora.tarasande.system.screen.accountmanager.account.Account
import su.mandora.tarasande.system.screen.accountmanager.account.ManagerAccount
import su.mandora.tarasande.system.screen.accountmanager.account.api.AccountInfo
import su.mandora.tarasande.system.screen.accountmanager.account.api.ExtraInfo
import su.mandora.tarasande.system.screen.accountmanager.account.api.TextFieldInfo
import su.mandora.tarasande.util.BUTTON_PADDING
import su.mandora.tarasande.util.DEFAULT_BUTTON_HEIGHT
import su.mandora.tarasande.util.DEFAULT_BUTTON_WIDTH
import su.mandora.tarasande.util.TEXTFIELD_WIDTH
import su.mandora.tarasande.util.extension.minecraft.render.widget.ButtonWidget
import su.mandora.tarasande.util.render.font.FontWrapper
import su.mandora.tarasande.util.screen.widget.TextFieldWidgetPlaceholder
import su.mandora.tarasande.util.screen.widget.TextFieldWidgetPlaceholderPassword
import java.util.function.Consumer

class ScreenBetterAccount(
    name: String,
    prevScreen: Screen,
    private val accountConsumer: Consumer<Account>,
) : ScreenBetter(name, prevScreen) {

    private val textFields = ArrayList<TextFieldWidget>()

    private var implementationClass: Class<out Account> = ManagerAccount.list.first()
    private var accountImplementation = implementationClass.getDeclaredConstructor().newInstance()

    private var submitButton: ButtonWidget? = null

    override fun init() {
        textFields.clear()

        addDrawableChild(ButtonWidget(BUTTON_PADDING, BUTTON_PADDING, DEFAULT_BUTTON_WIDTH, DEFAULT_BUTTON_HEIGHT, Text.of((implementationClass.getAnnotation(AccountInfo::class.java)).name)) { button ->
            implementationClass = ManagerAccount.let { it.list[(it.list.indexOf(implementationClass) + 1) % it.list.size] }
            accountImplementation = implementationClass.getDeclaredConstructor().newInstance()
            clearAndInit()
            button.message = Text.of(implementationClass.name)
        })

        val fields = implementationClass.declaredFields.toMutableList()

        var myClass = implementationClass
        if (implementationClass.getAnnotation(AccountInfo::class.java).inherit) {
            while (myClass.superclass != null) {
                fields.addAll(myClass.superclass.declaredFields)
                @Suppress("UNCHECKED_CAST")
                myClass = myClass.superclass as Class<out Account>
            }
        }

        var buttonIndex = 0
        var textFieldIndex = 0
        for (field in fields) {
            field.isAccessible = true
            if (field.isAnnotationPresent(ExtraInfo::class.java)) {
                @Suppress("UNCHECKED_CAST")
                val anyField = field.get(accountImplementation) as Function2<Screen, Runnable, Unit>
                val annotation = field.getAnnotation(ExtraInfo::class.java)
                addDrawableChild(ButtonWidget(width - DEFAULT_BUTTON_WIDTH - BUTTON_PADDING, BUTTON_PADDING + buttonIndex * (DEFAULT_BUTTON_HEIGHT + BUTTON_PADDING), DEFAULT_BUTTON_WIDTH, DEFAULT_BUTTON_HEIGHT, Text.of(annotation.name)) {
                    anyField(this) {
                        accountConsumer.accept(accountImplementation)
                        RenderSystem.recordRenderCall {
                            close()
                        }
                    }
                })
                buttonIndex++
            } else if (field.isAnnotationPresent(TextFieldInfo::class.java)) {
                val textFieldInfo = field.getAnnotation(TextFieldInfo::class.java)
                if (textFieldInfo.hidden) {
                    textFields.add(
                        addDrawableChild(
                            TextFieldWidgetPlaceholderPassword(
                                textRenderer,
                                width / 2 - TEXTFIELD_WIDTH / 2,
                                (height * 0.25F + textFieldIndex * (DEFAULT_BUTTON_HEIGHT + BUTTON_PADDING)).toInt(),
                                TEXTFIELD_WIDTH,
                                DEFAULT_BUTTON_HEIGHT,
                                Text.of(textFieldInfo.name)
                            ).also { it.setMaxLength(Int.MAX_VALUE); it.text = textFieldInfo.default })
                    )
                } else {
                    textFields.add(
                        addDrawableChild(
                            TextFieldWidgetPlaceholder(
                                textRenderer,
                                width / 2 - TEXTFIELD_WIDTH / 2,
                                (height * 0.25F + textFieldIndex * (DEFAULT_BUTTON_HEIGHT + BUTTON_PADDING)).toInt(),
                                TEXTFIELD_WIDTH,
                                DEFAULT_BUTTON_HEIGHT,
                                Text.of(textFieldInfo.name)
                            ).also { it.setMaxLength(Int.MAX_VALUE); it.text = textFieldInfo.default })
                    )
                }

                textFieldIndex++
            }
        }

        addDrawableChild(ButtonWidget(width / 2 - DEFAULT_BUTTON_WIDTH / 2, DEFAULT_BUTTON_HEIGHT + BUTTON_PADDING + (height * 0.75F).toInt(), DEFAULT_BUTTON_WIDTH, DEFAULT_BUTTON_HEIGHT, title) {
            accountImplementation.create(textFields.map { it.text })
            accountConsumer.accept(accountImplementation)
            close()
        }.also { submitButton = it })
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(context, mouseX, mouseY, delta)
        FontWrapper.textShadow(context, title.string, width / 2F, 8 - FontWrapper.fontHeight() / 2F, -1, centered = true)
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