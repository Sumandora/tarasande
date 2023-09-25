package su.mandora.tarasande.feature.screen.accountmanager.subscreen

import com.mojang.authlib.Environment
import com.mojang.authlib.yggdrasil.YggdrasilEnvironment
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.text.Text
import su.mandora.tarasande.util.BUTTON_PADDING
import su.mandora.tarasande.util.DEFAULT_BUTTON_HEIGHT
import su.mandora.tarasande.util.DEFAULT_BUTTON_WIDTH
import su.mandora.tarasande.util.TEXTFIELD_WIDTH
import su.mandora.tarasande.util.extension.minecraft.render.widget.ButtonWidget
import su.mandora.tarasande.feature.screen.ScreenBetter
import su.mandora.tarasande.util.screen.widget.TextFieldWidgetPlaceholder
import java.util.function.Consumer

class ScreenBetterEnvironment(prevScreen: Screen?, private val environment: Environment?, private val environmentConsumer: Consumer<Environment>) : ScreenBetter("Environment", prevScreen) {

    private var authHostTextField: TextFieldWidget? = null
    private var accountsHostTextField: TextFieldWidget? = null
    private var sessionHostTextField: TextFieldWidget? = null
    private var servicesHostTextField: TextFieldWidget? = null

    override fun init() {
        val environment = environment ?: YggdrasilEnvironment.PROD.environment
        authHostTextField = TextFieldWidgetPlaceholder(this.textRenderer, this.width / 2 - TEXTFIELD_WIDTH / 2, (height * 0.25F + 1 * (DEFAULT_BUTTON_HEIGHT + BUTTON_PADDING)).toInt(), TEXTFIELD_WIDTH, DEFAULT_BUTTON_HEIGHT, Text.of("Auth Host")).also {
            it.setMaxLength(Int.MAX_VALUE)
            it.text = environment.authHost
            addDrawableChild(it)
        }

        accountsHostTextField = TextFieldWidgetPlaceholder(this.textRenderer, this.width / 2 - TEXTFIELD_WIDTH / 2, (height * 0.25F + 2 * (DEFAULT_BUTTON_HEIGHT + BUTTON_PADDING)).toInt(), TEXTFIELD_WIDTH, DEFAULT_BUTTON_HEIGHT, Text.of("Accounts Host")).also {
            it.setMaxLength(Int.MAX_VALUE)
            it.text = environment.accountsHost
            addDrawableChild(it)
        }

        sessionHostTextField = TextFieldWidgetPlaceholder(this.textRenderer, this.width / 2 - TEXTFIELD_WIDTH / 2, (height * 0.25F + 3 * (DEFAULT_BUTTON_HEIGHT + BUTTON_PADDING)).toInt(), TEXTFIELD_WIDTH, DEFAULT_BUTTON_HEIGHT, Text.of("Session Host")).also {
            it.setMaxLength(Int.MAX_VALUE)
            it.text = environment.sessionHost
            addDrawableChild(it)
        }

        servicesHostTextField = TextFieldWidgetPlaceholder(this.textRenderer, this.width / 2 - TEXTFIELD_WIDTH / 2, (height * 0.25F + 4 * (DEFAULT_BUTTON_HEIGHT + BUTTON_PADDING)).toInt(), TEXTFIELD_WIDTH, DEFAULT_BUTTON_HEIGHT, Text.of("Services Host")).also {
            it.setMaxLength(Int.MAX_VALUE)
            it.text = environment.servicesHost
            addDrawableChild(it)
        }

        this.addDrawableChild(ButtonWidget(width / 2 - DEFAULT_BUTTON_WIDTH / 2, DEFAULT_BUTTON_HEIGHT + BUTTON_PADDING + (height * 0.75F).toInt(), DEFAULT_BUTTON_WIDTH, DEFAULT_BUTTON_HEIGHT, Text.of("Done")) {
            environmentConsumer.accept(Environment.create(authHostTextField?.text!!, accountsHostTextField?.text!!, sessionHostTextField?.text!!, servicesHostTextField?.text!!, "Custom"))
            RenderSystem.recordRenderCall { close() }
        })

        super.init()
    }
}
