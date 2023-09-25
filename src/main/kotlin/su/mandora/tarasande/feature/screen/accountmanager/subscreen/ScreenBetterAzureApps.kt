package su.mandora.tarasande.feature.screen.accountmanager.subscreen

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.text.Text
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.screen.accountmanager.azureapp.AzureAppPreset
import su.mandora.tarasande.system.screen.accountmanager.azureapp.ManagerAzureApp
import su.mandora.tarasande.util.BUTTON_PADDING
import su.mandora.tarasande.util.DEFAULT_BUTTON_HEIGHT
import su.mandora.tarasande.util.DEFAULT_BUTTON_WIDTH
import su.mandora.tarasande.util.TEXTFIELD_WIDTH
import su.mandora.tarasande.util.extension.minecraft.render.widget.ButtonWidget
import su.mandora.tarasande.feature.screen.ScreenBetter
import su.mandora.tarasande.util.screen.widget.TextFieldWidgetPlaceholder
import java.util.function.Consumer

class ScreenBetterAzureApps(prevScreen: Screen?, private val azureApp: AzureAppPreset, private val environmentConsumer: Consumer<AzureAppPreset>) : ScreenBetter(azureApp.name, prevScreen) {

    private var clientIdTextField: TextFieldWidget? = null
    private var scopeTextField: TextFieldWidget? = null
    private var redirectUriTextField: TextFieldWidget? = null
    private var clientSecretTextField: TextFieldWidget? = null

    override fun init() {

        clientIdTextField = TextFieldWidgetPlaceholder(this.textRenderer, this.width / 2 - TEXTFIELD_WIDTH / 2, (height * 0.25F + 1 * (DEFAULT_BUTTON_HEIGHT + BUTTON_PADDING)).toInt(), TEXTFIELD_WIDTH, DEFAULT_BUTTON_HEIGHT, Text.of("Client ID")).also {
            it.setMaxLength(Int.MAX_VALUE)
            it.text = azureApp.clientId
            addDrawableChild(it)
        }

        scopeTextField = TextFieldWidgetPlaceholder(this.textRenderer, this.width / 2 - TEXTFIELD_WIDTH / 2, (height * 0.25F + 2 * (DEFAULT_BUTTON_HEIGHT + BUTTON_PADDING)).toInt(), TEXTFIELD_WIDTH, DEFAULT_BUTTON_HEIGHT, Text.of("Scope")).also {
            it.setMaxLength(Int.MAX_VALUE)
            it.text = azureApp.scope
            addDrawableChild(it)
        }

        redirectUriTextField = TextFieldWidgetPlaceholder(this.textRenderer, this.width / 2 - TEXTFIELD_WIDTH / 2, (height * 0.25F + 3 * (DEFAULT_BUTTON_HEIGHT + BUTTON_PADDING)).toInt(), TEXTFIELD_WIDTH, DEFAULT_BUTTON_HEIGHT, Text.of("Redirect Uri")).also {
            it.setMaxLength(Int.MAX_VALUE)
            it.text = azureApp.redirectUri
            addDrawableChild(it)
        }

        clientSecretTextField = TextFieldWidgetPlaceholder(this.textRenderer, this.width / 2 - TEXTFIELD_WIDTH / 2, (height * 0.25F + 4 * (DEFAULT_BUTTON_HEIGHT + BUTTON_PADDING)).toInt(), TEXTFIELD_WIDTH, DEFAULT_BUTTON_HEIGHT, Text.of("Client Secret")).also {
            it.setMaxLength(Int.MAX_VALUE)
            it.text = azureApp.clientSecret ?: ""
            addDrawableChild(it)
        }

        var x = BUTTON_PADDING
        var y = BUTTON_PADDING

        for (environmentPreset in ManagerAzureApp.list) {
            this.addDrawableChild(ButtonWidget(x, y, DEFAULT_BUTTON_WIDTH, DEFAULT_BUTTON_HEIGHT, Text.of(environmentPreset.name)) {
                clientIdTextField?.text = environmentPreset.clientId
                scopeTextField?.text = environmentPreset.scope
                redirectUriTextField?.text = environmentPreset.redirectUri
                clientSecretTextField?.text = environmentPreset.clientSecret ?: ""
            })

            x += DEFAULT_BUTTON_WIDTH + BUTTON_PADDING
            if (x + DEFAULT_BUTTON_WIDTH + BUTTON_PADDING >= mc.window.scaledWidth) {
                x = BUTTON_PADDING
                y += DEFAULT_BUTTON_HEIGHT + BUTTON_PADDING
            }
        }

        this.addDrawableChild(ButtonWidget(width / 2 - DEFAULT_BUTTON_WIDTH / 2, DEFAULT_BUTTON_HEIGHT + BUTTON_PADDING + (height * 0.75F).toInt(), DEFAULT_BUTTON_WIDTH, DEFAULT_BUTTON_HEIGHT, Text.of("Done")) {
            environmentConsumer.accept(AzureAppPreset("Custom",
                this.clientIdTextField!!.text,
                this.scopeTextField!!.text,
                this.redirectUriTextField!!.text,
                this.clientSecretTextField!!.text.let { it.ifBlank { null } }
            ))
            RenderSystem.recordRenderCall { close() }
        })

        super.init()
    }

}