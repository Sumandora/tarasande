package net.tarasandedevelopment.tarasande.screen.list.accountmanager.subscreens

import com.mojang.authlib.Environment
import com.mojang.authlib.yggdrasil.YggdrasilEnvironment
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.screen.accountmanager.account.Account
import net.tarasandedevelopment.tarasande.base.screen.accountmanager.account.AccountInfo
import net.tarasandedevelopment.tarasande.base.screen.accountmanager.account.TextFieldInfo
import net.tarasandedevelopment.tarasande.mixin.accessor.IScreen
import net.tarasandedevelopment.tarasande.screen.ScreenBetter
import net.tarasandedevelopment.tarasande.screen.menu.clientmenu.ElementMenuScreenAccountManager
import net.tarasandedevelopment.tarasande.screen.widget.textfields.TextFieldWidgetPassword
import net.tarasandedevelopment.tarasande.screen.widget.textfields.TextFieldWidgetPlaceholder
import org.lwjgl.glfw.GLFW
import java.awt.Color
import java.lang.reflect.Constructor
import java.util.function.Consumer

class ScreenBetterAccount(
    prevScreen: Screen,
    val name: String,
    private val accountConsumer: Consumer<Account>,
) : ScreenBetter(prevScreen) {

    private val informationList = ArrayList<Element>()
    private val modeSelections = HashMap<String, String>()
    private var implementationClass: Class<out Account> = TarasandeMain.get().managerClientMenu.get(ElementMenuScreenAccountManager::class.java).screenBetterAccountManager.managerAccount.list[0]

    private var environment: Environment? = null

    private var submitButton: ButtonWidget? = null

    override fun init() {
        informationList.clear()
        children().clear()
        (this as IScreen).also {
            it.tarasande_getDrawables().clear()
            it.tarasande_getSelectables().clear()
        }

        super.init()

        addDrawableChild(
            ButtonWidget(
                5,
                5,
                100,
                20,
                Text.of((implementationClass.annotations[0] as AccountInfo).name)
            ) { button ->
                val accountManager = TarasandeMain.get().managerClientMenu.get(ElementMenuScreenAccountManager::class.java).screenBetterAccountManager

                implementationClass = accountManager.managerAccount.list[(accountManager.managerAccount.list.indexOf(implementationClass) + 1) % accountManager.managerAccount.list.size]
                init()
                button.message = Text.of(implementationClass.name)
            })

        addDrawableChild(ButtonWidget(5, 30, 100, 20, Text.of("Environment")) {
            client?.setScreen(ScreenBetterEnvironment(this, environment) { environment = it })
        })

        var constructor: Constructor<*>? = null
        for (c in implementationClass.constructors) {
            if (constructor == null || c.parameters.size > constructor.parameters.size) {
                constructor = c
            }
        }
        val parameters = constructor?.parameters!!
        for (i in parameters.indices) {
            val parameterType = parameters[i]
            if (parameterType.isAnnotationPresent(TextFieldInfo::class.java)) {
                val textFieldInfo = parameterType.getAnnotation(TextFieldInfo::class.java)
                if (textFieldInfo.hidden) {
                    informationList.add(
                        addDrawableChild(
                            TextFieldWidgetPassword(
                                textRenderer,
                                width / 2 - 150,
                                (height * 0.25f + i * 25).toInt(),
                                300,
                                20,
                                Text.of(textFieldInfo.name)
                            ).also { it.setMaxLength(Int.MAX_VALUE); it.text = textFieldInfo.default })
                    )
                } else {
                    informationList.add(
                        addDrawableChild(
                            TextFieldWidgetPlaceholder(
                                textRenderer,
                                width / 2 - 150,
                                (height * 0.25f + i * 25).toInt(),
                                300,
                                20,
                                Text.of(textFieldInfo.name)
                            ).also { it.setMaxLength(Int.MAX_VALUE); it.text = textFieldInfo.default })
                    )
                }
            } else if (parameterType.isAnnotationPresent(ModeInfo::class.java)) {
                val modeInfo = parameterType.getAnnotation(ModeInfo::class.java)

                if (!modeSelections.contains(modeInfo.name))
                    modeSelections[modeInfo.name] = modeInfo.options[0]

                val prefix = Text.literal(modeInfo.name + ": ");
                this.informationList.add(addDrawableChild(ButtonWidget(width / 2 - 150, (height * 0.25f + i * 25).toInt(), 300, 20, prefix.append(Text.of(modeSelections[modeInfo.name]))) {
                    val currentIndex = modeInfo.options.indexOf(it.message.string.split(" ")[1])
                    if (currentIndex + 1 >= modeInfo.options.size)
                        modeSelections[modeInfo.name] = modeInfo.options[0]
                    else
                        modeSelections[modeInfo.name] = modeInfo.options[currentIndex + 1]
                }))
            }
        }

        addDrawableChild(ButtonWidget(width / 2 - 50, 25 + (height * 0.75f).toInt(), 100, 20, Text.of(name)) {
            val providedList = ArrayList<String>()
            informationList.forEach {
                if (it is TextFieldWidget)
                    providedList.add(it.text)
                else if (it is ButtonWidget) {
                    modeSelections.forEach { (key, value) ->
                        if (it.message.string.contains(key, true))
                            providedList.add(value)
                    }
                }
            }
            val account = (implementationClass.getDeclaredConstructor().newInstance() as Account).create(providedList)
            account.environment = environment ?: YggdrasilEnvironment.PROD.environment
            accountConsumer.accept(account)
            close()
        }.also { submitButton = it })

        this.addDrawableChild(ButtonWidget(5, this.height - 25, 20, 20, Text.of("<-")) { this.close() })
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(matrices, mouseX, mouseY, delta)
        drawCenteredText(matrices, textRenderer, name, width / 2, 8 - textRenderer.fontHeight / 2, Color.white.rgb)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        var focused = false
        for (element in informationList)
            if (element is TextFieldWidget)
                if (element.isFocused)
                    focused = true
        if (hasControlDown() && keyCode == GLFW.GLFW_KEY_V && !focused) {
            val clipboardContent = GLFW.glfwGetClipboardString(client?.window?.handle!!)
            if (clipboardContent != null) {
                val parts = clipboardContent.split(":")
                if (parts.size == informationList.size)
                    for ((index, element) in informationList.withIndex())
                        if (element is TextFieldWidget)
                            element.text = parts[index]
            }
        }
        if (keyCode == GLFW.GLFW_KEY_ENTER)
            submitButton?.onPress()
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

}