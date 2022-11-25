package net.tarasandedevelopment.tarasande.system.feature.screenextensionsystem.impl.accountmanager.subscreen

import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.screen.base.ScreenBetter
import net.tarasandedevelopment.tarasande.screen.widget.textfield.TextFieldWidgetPlaceholder
import net.tarasandedevelopment.tarasande.screen.widget.textfield.TextFieldWidgetPlaceholderPassword
import net.tarasandedevelopment.tarasande.system.feature.screenextensionsystem.impl.accountmanager.screenextension.ScreenExtensionButtonAccountManager
import net.tarasandedevelopment.tarasande.system.screen.accountmanager.account.Account
import net.tarasandedevelopment.tarasande.system.screen.accountmanager.account.api.AccountInfo
import net.tarasandedevelopment.tarasande.system.screen.accountmanager.account.api.ExtraInfo
import net.tarasandedevelopment.tarasande.system.screen.accountmanager.account.api.TextFieldInfo
import net.tarasandedevelopment.tarasande.util.render.font.FontWrapper
import org.lwjgl.glfw.GLFW
import java.util.function.Consumer

class ScreenBetterAccount(
    prevScreen: Screen,
    val name: String,
    private val accountConsumer: Consumer<Account>,
) : ScreenBetter(prevScreen) {

    private val textFields = ArrayList<TextFieldWidget>()

    private var implementationClass: Class<out Account> = TarasandeMain.managerScreenExtension().get(ScreenExtensionButtonAccountManager::class.java).screenBetterSlotListAccountManager.managerAccount.list.first()
    private var accountImplementation = implementationClass.getDeclaredConstructor().newInstance()

    private var submitButton: ButtonWidget? = null

    override fun init() {
        textFields.clear()
        children().clear()
        this.drawables.clear()
        this.selectables.clear()

        super.init()

        addDrawableChild(
            ButtonWidget(
                5,
                5,
                100,
                20,
                Text.of((implementationClass.annotations[0] as AccountInfo).name)
            ) { button ->
                implementationClass = TarasandeMain.managerScreenExtension().get(ScreenExtensionButtonAccountManager::class.java).screenBetterSlotListAccountManager.managerAccount.let { it.list[(it.list.indexOf(implementationClass) + 1) % it.list.size] }
                accountImplementation = implementationClass.getDeclaredConstructor().newInstance()
                init()
                button.message = Text.of(implementationClass.name)
            })

        var index = 0

        val fields = implementationClass.declaredFields.toMutableList()

        var myClass = implementationClass
        while (myClass.superclass != null) {
            fields.addAll(myClass.superclass.declaredFields)
            @Suppress("UNCHECKED_CAST")
            myClass = myClass.superclass as Class<out Account>
        }

        var i = 0
        for (field in fields) {
            field.isAccessible = true
            if (field.isAnnotationPresent(ExtraInfo::class.java)) {
                @Suppress("UNCHECKED_CAST")
                val anyField = field.get(accountImplementation) as Function1<Screen, *>
                addDrawableChild(ButtonWidget(width - 105, 5 + index * 23, 100, 20, Text.of(field.getAnnotation(ExtraInfo::class.java).name)) {
                    anyField.invoke(this)
                })
                index++
            } else if (field.isAnnotationPresent(TextFieldInfo::class.java)) {
                val textFieldInfo: TextFieldInfo = field.getAnnotation(TextFieldInfo::class.java)
                if (textFieldInfo.hidden) {
                    textFields.add(
                        addDrawableChild(
                            TextFieldWidgetPlaceholderPassword(
                                textRenderer,
                                width / 2 - 150,
                                (height * 0.25F + i * 25).toInt(),
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
                                (height * 0.25F + i * 25).toInt(),
                                300,
                                20,
                                Text.of(textFieldInfo.name)
                            ).also { it.setMaxLength(Int.MAX_VALUE); it.text = textFieldInfo.default })
                    )
                }

                i++
            }
        }

        addDrawableChild(ButtonWidget(width / 2 - 50, 25 + (height * 0.75F).toInt(), 100, 20, Text.of(name)) {
            accountImplementation.create(textFields.map { it.text })
            accountConsumer.accept(accountImplementation)
            close()
        }.also { submitButton = it })
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(matrices, mouseX, mouseY, delta)
        FontWrapper.textShadow(matrices, name, width / 2.0F, 8 - FontWrapper.fontHeight() / 2.0F, -1, centered = true)
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