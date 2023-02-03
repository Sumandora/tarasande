package net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.multiplayer.accountmanager.subscreen

import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.tarasandedevelopment.tarasande.system.screen.accountmanager.account.Account
import net.tarasandedevelopment.tarasande.system.screen.accountmanager.account.ManagerAccount
import net.tarasandedevelopment.tarasande.system.screen.accountmanager.account.api.AccountInfo
import net.tarasandedevelopment.tarasande.system.screen.accountmanager.account.api.ExtraInfo
import net.tarasandedevelopment.tarasande.system.screen.accountmanager.account.api.TextFieldInfo
import net.tarasandedevelopment.tarasande.util.extension.minecraft.ButtonWidget
import net.tarasandedevelopment.tarasande.util.render.font.FontWrapper
import net.tarasandedevelopment.tarasande.util.screen.ScreenBetter
import net.tarasandedevelopment.tarasande.util.screen.widget.TextFieldWidgetPlaceholder
import net.tarasandedevelopment.tarasande.util.screen.widget.TextFieldWidgetPlaceholderPassword
import org.lwjgl.glfw.GLFW
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

        addDrawableChild(ButtonWidget(5, 5, 100, 20, Text.of((implementationClass.getAnnotation(AccountInfo::class.java)).name)) { button ->
            implementationClass = ManagerAccount.let { it.list[(it.list.indexOf(implementationClass) + 1) % it.list.size] }
            accountImplementation = implementationClass.getDeclaredConstructor().newInstance()
            clearAndInit()
            button.message = Text.of(implementationClass.name)
        })

        val fields = implementationClass.declaredFields.toMutableList()

        var myClass = implementationClass
        if(implementationClass.getAnnotation(AccountInfo::class.java).inherit) {
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
                val anyField = field.get(accountImplementation) as Function1<Screen, Unit>
                val annotation = field.getAnnotation(ExtraInfo::class.java)
                addDrawableChild(ButtonWidget(width - 105, 5 + buttonIndex * 23, 100, 20, Text.of(annotation.name)) {
                    anyField(this)
                    if(annotation.alternativeLogin) {
                        accountConsumer.accept(accountImplementation)
                        close()
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
                                width / 2 - 150,
                                (height * 0.25F + textFieldIndex * 25).toInt(),
                                300,
                                20,
                                Text.of(textFieldInfo.name)
                            ).also { it.setMaxLength(Int.MAX_VALUE); it.text = textFieldInfo.default })
                    )
                } else {
                    textFields.add(
                        addDrawableChild(
                            TextFieldWidgetPlaceholder(
                                textRenderer,
                                width / 2 - 150,
                                (height * 0.25F + textFieldIndex * 25).toInt(),
                                300,
                                20,
                                Text.of(textFieldInfo.name)
                            ).also { it.setMaxLength(Int.MAX_VALUE); it.text = textFieldInfo.default })
                    )
                }

                textFieldIndex++
            }
        }

        addDrawableChild(ButtonWidget(width / 2 - 50, 25 + (height * 0.75F).toInt(), 100, 20, title) {
            accountImplementation.create(textFields.map { it.text })
            accountConsumer.accept(accountImplementation)
            close()
        }.also { submitButton = it })
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(matrices, mouseX, mouseY, delta)
        FontWrapper.textShadow(matrices, title.string, width / 2.0F, 8 - FontWrapper.fontHeight() / 2.0F, -1, centered = true)
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