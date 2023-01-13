package net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.multiplayer.accountmanager.subscreen

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.text.Text
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.screen.base.ScreenBetter
import net.tarasandedevelopment.tarasande.screen.widget.textfield.TextFieldWidgetPlaceholder
import net.tarasandedevelopment.tarasande.system.screen.accountmanager.azureapp.AzureAppPreset
import net.tarasandedevelopment.tarasande.system.screen.accountmanager.azureapp.ManagerAzureApp
import net.tarasandedevelopment.tarasande.util.extension.minecraft.ButtonWidget
import java.util.function.Consumer

class ScreenBetterAzureApps(prevScreen: Screen?, private val azureApp: AzureAppPreset, private val environmentConsumer: Consumer<AzureAppPreset>) : ScreenBetter(azureApp.name, prevScreen) {

    private var clientIdTextField: TextFieldWidget? = null
    private var scopeTextField: TextFieldWidget? = null
    private var redirectUriTextField: TextFieldWidget? = null
    private var clientSecretTextField: TextFieldWidget? = null

    override fun init() {

        clientIdTextField = TextFieldWidgetPlaceholder(this.textRenderer, this.width / 2 - 100, this.height / 2 - 50 - 15, 200, 20, Text.of("Client ID")).also {
            it.setMaxLength(Int.MAX_VALUE)
            it.text = azureApp.clientId
            addDrawableChild(it)
        }

        scopeTextField = TextFieldWidgetPlaceholder(this.textRenderer, this.width / 2 - 100, this.height / 2 - 25 - 15, 200, 20, Text.of("Scope")).also {
            it.setMaxLength(Int.MAX_VALUE)
            it.text = azureApp.scope
            addDrawableChild(it)
        }

        redirectUriTextField = TextFieldWidgetPlaceholder(this.textRenderer, this.width / 2 - 100, this.height / 2 - 15, 200, 20, Text.of("Redirect Uri")).also {
            it.setMaxLength(Int.MAX_VALUE)
            it.text = azureApp.redirectUri
            addDrawableChild(it)
        }

        clientSecretTextField = TextFieldWidgetPlaceholder(this.textRenderer, this.width / 2 - 100, this.height / 2 + 25 - 15, 200, 20, Text.of("Client Secret")).also {
            it.setMaxLength(Int.MAX_VALUE)
            it.text = azureApp.clientSecret ?: ""
            addDrawableChild(it)
        }

        var x = 3
        var y = 3

        for (environmentPreset in ManagerAzureApp.list) {
            this.addDrawableChild(ButtonWidget(x, y, 130, 20, Text.of(environmentPreset.name)) {
                clientIdTextField?.text = environmentPreset.clientId
                scopeTextField?.text = environmentPreset.scope
                redirectUriTextField?.text = environmentPreset.redirectUri
                clientSecretTextField?.text = environmentPreset.clientSecret ?: ""
            })

            x += 130 + 3
            if (x + 135 >= mc.window.scaledWidth) {
                x = 3
                y += 20 + 3
            }
        }

        this.addDrawableChild(ButtonWidget(width / 2 - 50, height / 2 + 50 + 25, 100, 20, Text.of("Done")) {
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