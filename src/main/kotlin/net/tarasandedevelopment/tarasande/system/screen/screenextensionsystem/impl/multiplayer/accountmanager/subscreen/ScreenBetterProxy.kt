package net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.multiplayer.accountmanager.subscreen

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.tarasandedevelopment.tarasande.screen.base.ScreenBetter
import net.tarasandedevelopment.tarasande.screen.widget.textfield.TextFieldWidgetPlaceholder
import net.tarasandedevelopment.tarasande.screen.widget.textfield.TextFieldWidgetPlaceholderPassword
import net.tarasandedevelopment.tarasande.util.connection.Proxy
import net.tarasandedevelopment.tarasande.util.connection.ProxyAuthentication
import net.tarasandedevelopment.tarasande.util.connection.ProxyType
import net.tarasandedevelopment.tarasande.util.extension.minecraft.ButtonWidget
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import net.tarasandedevelopment.tarasande.util.render.font.FontWrapper
import net.tarasandedevelopment.tarasande.util.threading.ThreadRunnableExposed
import org.lwjgl.glfw.GLFW
import java.awt.Color
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.nio.channels.IllegalBlockingModeException

class ScreenBetterProxy : ScreenBetter(null) {

    var proxy: Proxy? = null
    private var ipTextField: TextFieldWidget? = null
    private var portTextField: TextFieldWidget? = null
    private var usernameTextField: TextFieldWidget? = null
    private var passwordTextField: TextFieldWidget? = null

    private var doneButton: ButtonWidget? = null

    private var proxyTypeButtonWidget: ButtonWidget? = null
    private var proxyType: ProxyType? = null

    private var status: String? = null

    private var pingThread: ThreadRunnableExposed? = null

    override fun init() {
        addDrawableChild(TextFieldWidgetPlaceholder(textRenderer, width / 2 - 100, height / 2 - 50 - 15, 143, 20, Text.of("IP-Address")).also {
            ipTextField = it
            it.setMaxLength(Int.MAX_VALUE)
            if (proxy?.socketAddress != null) {
                it.text = proxy!!.socketAddress.address.hostAddress
            }
        })
        addDrawableChild(TextFieldWidgetPlaceholder(textRenderer, width / 2 + 47, height / 2 - 50 - 15, 53, 20, Text.of("Port")).also {
            portTextField = it
            it.setMaxLength(Int.MAX_VALUE)
            if (proxy != null) {
                it.text = proxy!!.socketAddress.port.toString()
            }
        })
        addDrawableChild(ButtonWidget(width / 2 - 75, height / 2 - 40, 150, 20, Text.of("Proxy Type: ")) {
            proxyType = ProxyType.values()[(proxyType?.ordinal!! + 1) % ProxyType.values().size]
            it.message = Text.of("Proxy Type: " + proxyType?.printable!!)
        }.also {
            proxyTypeButtonWidget = it
            proxyType = proxy?.type ?: ProxyType.values()[0]
            it.message = Text.of("Proxy Type: " + proxyType?.printable!!)
        })
        addDrawableChild(TextFieldWidgetPlaceholder(textRenderer, width / 2 - 100, height / 2 - 15, 200, 20, Text.of("Username")).also {
            usernameTextField = it
            it.setMaxLength(Int.MAX_VALUE)
            if (proxy?.proxyAuthentication != null) {
                it.text = proxy?.proxyAuthentication?.username!!
            }
        })
        addDrawableChild(TextFieldWidgetPlaceholderPassword(textRenderer, width / 2 - 100, height / 2 + 10, 200, 20, Text.of("Password")).also {
            passwordTextField = it
            it.setMaxLength(Int.MAX_VALUE)
            if (proxy?.proxyAuthentication != null) {
                it.text = proxy?.proxyAuthentication?.password!!
            }
        })

        addDrawableChild(ButtonWidget(width / 2 - 50, height / 2 + 50, 100, 20, Text.of("Done")) {
            if (ipTextField?.text?.isEmpty()!! || portTextField?.text?.isEmpty()!!) {
                status = Formatting.RED.toString() + if (ipTextField?.text?.isEmpty()!! && portTextField?.text?.isEmpty()!!) "IP and port are empty"
                else if (ipTextField?.text?.isEmpty()!!) "IP is empty"
                else if (portTextField?.text?.isEmpty()!!) "Port is empty"
                else null
            } else {
                try {
                    val port = portTextField?.text?.toInt()!!
                    val inetSocketAddress = InetSocketAddress(ipTextField?.text!!, port)
                    val proxy =
                        if (usernameTextField?.text?.isNotEmpty()!! && (proxyType == ProxyType.SOCKS4 || passwordTextField?.text?.isNotEmpty()!!))
                            Proxy(inetSocketAddress, proxyType!!,
                                if (usernameTextField?.text?.isNotEmpty() == true || passwordTextField?.text?.isNotEmpty() == true)
                                    ProxyAuthentication(usernameTextField?.text!!,
                                        if (proxyType == ProxyType.SOCKS4)
                                            null
                                        else
                                            passwordTextField?.text
                                    )
                                else null
                            )
                        else Proxy(inetSocketAddress, proxyType!!)
                    this.proxy = proxy
                    if (pingThread != null && pingThread?.isAlive!!)
                        (pingThread?.runnable as RunnablePing).cancelled = true
                    ThreadRunnableExposed(RunnablePing(proxy)).also { pingThread = it }.start()
                } catch (numberFormatException: NumberFormatException) {
                    status = Formatting.RED.toString() + "Port is not numeric"
                } catch (unknownHostException: UnknownHostException) {
                    status = Formatting.RED.toString() + "Invalid IP or port"
                } catch (illegalArgumentException: IllegalArgumentException) {
                    status = Formatting.RED.toString() + "Invalid IP or port"
                } catch (throwable: Throwable) {
                    status = Formatting.RED.toString() + throwable.message
                }
            }
        }.also { doneButton = it })

        addDrawableChild(ButtonWidget(width / 2 - 50, height / 2 + 50 + 25, 100, 20, Text.of("Disable")) {
            this.proxy = null
            status = "Disabled"
        })

        tick()
        super.init()
    }

    override fun close() {
        super.close()

        RenderSystem.recordRenderCall {
            if (pingThread != null && pingThread?.isAlive!!)
                (pingThread?.runnable as RunnablePing).cancelled = true
        }
    }

    override fun tick() {
        passwordTextField?.visible = proxyType != ProxyType.SOCKS4
        super.tick()
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(matrices, mouseX, mouseY, delta)
        FontWrapper.textShadow(matrices, "Proxy", width / 2.0F, 8 - FontWrapper.fontHeight() / 2.0F, -1, centered = true)
        FontWrapper.textShadow(matrices, status ?: return, width / 2.0F, height / 2F - 50 - 15 - FontWrapper.fontHeight() - 2, -1, centered = true)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            doneButton?.onPress()
        } else {
            var focused = false
            for (textField in listOf(ipTextField, portTextField, usernameTextField, passwordTextField)) if (textField?.isFocused!!) focused = true
            if (hasControlDown() && keyCode == GLFW.GLFW_KEY_V && !focused) {
                val clipboardContent = GLFW.glfwGetClipboardString(client?.window?.handle!!)
                if (clipboardContent != null) {
                    val parts = clipboardContent.split(":")
                    if (parts.size == 2) {
                        ipTextField?.text = parts[0]
                        portTextField?.text = parts[1]
                    }
                }
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    inner class RunnablePing(private val proxy: Proxy) : Runnable {
        var cancelled = false

        override fun run() {
            val socket = Socket()
            try {
                status = Formatting.YELLOW.toString() + "Pinging..."
                val beginTime = System.currentTimeMillis()
                socket.connect(proxy.socketAddress, 5000)
                if (cancelled)
                    return
                val timeDelta = System.currentTimeMillis() - beginTime
                if (this.proxy == proxy) {
                    status = RenderUtil.formattingByHex(RenderUtil.colorInterpolate(Color.green, Color.red.darker(), (timeDelta / 1000.0).coerceAtMost(1.0)).rgb).toString() + "Reached proxy in " + timeDelta + "ms"
                    proxy.ping = timeDelta
                }
            } catch (throwable: Throwable) {
                if (this.proxy == proxy) {
                    status = when (throwable) {
                        is SocketTimeoutException -> Formatting.RED.toString() + "Timeout reached, unreachable"
                        is IOException -> Formatting.RED.toString() + "Failed to reach proxy"
                        is IllegalBlockingModeException -> Formatting.RED.toString() + "Illegal blocking method"
                        is IllegalArgumentException -> Formatting.RED.toString() + "Invalid IP or port"
                        else -> Formatting.RED.toString() + (if (throwable.message != null && throwable.message?.isNotEmpty()!!) throwable.message else "Unknown error")
                    }
                }
                throwable.printStackTrace()
            } finally {
                socket.close()
            }
        }
    }
}