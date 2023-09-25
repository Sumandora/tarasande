package su.mandora.tarasande.feature.screen.proxy

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import org.lwjgl.glfw.GLFW
import su.mandora.tarasande.mc
import su.mandora.tarasande.util.BUTTON_PADDING
import su.mandora.tarasande.util.DEFAULT_BUTTON_HEIGHT
import su.mandora.tarasande.util.DEFAULT_BUTTON_WIDTH
import su.mandora.tarasande.util.TEXTFIELD_WIDTH
import su.mandora.tarasande.util.connection.Proxy
import su.mandora.tarasande.util.connection.ProxyAuthentication
import su.mandora.tarasande.util.connection.ProxyType
import su.mandora.tarasande.util.extension.minecraft.render.widget.ButtonWidget
import su.mandora.tarasande.util.render.RenderUtil
import su.mandora.tarasande.util.render.font.FontWrapper
import su.mandora.tarasande.feature.screen.ScreenBetter
import su.mandora.tarasande.util.screen.widget.TextFieldWidgetPlaceholder
import su.mandora.tarasande.util.screen.widget.TextFieldWidgetPlaceholderPassword
import su.mandora.tarasande.util.threading.ThreadRunnableExposed
import java.awt.Color
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.nio.channels.IllegalBlockingModeException

class ScreenBetterProxy : ScreenBetter("Proxy", null) {

    var proxy: Proxy? = null
    private var ipTextField: TextFieldWidget? = null
    private var portTextField: TextFieldWidget? = null
    private var usernameTextField: TextFieldWidget? = null
    private var passwordTextField: TextFieldWidget? = null

    private var doneButton: ButtonWidget? = null

    private var proxyTypeButtonWidget: ButtonWidget? = null
    private var proxyType: ProxyType? = null

    private var pingThread: ThreadRunnableExposed? = null

    private var status: Text? = null

    companion object {
        const val IP_PORT_RATIO = 0.8
    }

    override fun init() {
        addDrawableChild(TextFieldWidgetPlaceholder(textRenderer, width / 2 - TEXTFIELD_WIDTH / 2, height / 2 - 50 - 15, (TEXTFIELD_WIDTH * IP_PORT_RATIO).toInt(), DEFAULT_BUTTON_HEIGHT, Text.of("IP-Address")).also {
            ipTextField = it
            it.setMaxLength(Int.MAX_VALUE)
            if (proxy?.socketAddress != null) {
                it.text = proxy!!.socketAddress.address.hostAddress
            }
        })
        addDrawableChild(TextFieldWidgetPlaceholder(textRenderer, width / 2 - TEXTFIELD_WIDTH / 2 + (TEXTFIELD_WIDTH * IP_PORT_RATIO).toInt() + BUTTON_PADDING, height / 2 - 50 - 15, (TEXTFIELD_WIDTH * (1.0 - IP_PORT_RATIO)).toInt(), DEFAULT_BUTTON_HEIGHT, Text.of("Port")).also {
            portTextField = it
            it.setMaxLength(Int.MAX_VALUE)
            if (proxy != null) {
                it.text = proxy!!.socketAddress.port.toString()
            }
        })
        addDrawableChild(ButtonWidget((width / 2 - DEFAULT_BUTTON_WIDTH * 0.75).toInt(), height / 2 - 40, (DEFAULT_BUTTON_WIDTH * 1.5).toInt(), DEFAULT_BUTTON_HEIGHT, Text.of("Proxy Type: ")) {
            proxyType = ProxyType.entries[(proxyType?.ordinal!! + 1) % ProxyType.entries.size]
            it.message = Text.of("Proxy Type: " + proxyType?.printable!!)
        }.also {
            proxyTypeButtonWidget = it
            proxyType = proxy?.type ?: ProxyType.entries[0]
            it.message = Text.of("Proxy Type: " + proxyType?.printable!!)
        })
        addDrawableChild(TextFieldWidgetPlaceholder(textRenderer, width / 2 - TEXTFIELD_WIDTH / 2, height / 2 - 15, TEXTFIELD_WIDTH, DEFAULT_BUTTON_HEIGHT, Text.of("Username")).also {
            usernameTextField = it
            it.setMaxLength(Int.MAX_VALUE)
            if (proxy?.proxyAuthentication != null) {
                it.text = proxy?.proxyAuthentication?.username!!
            }
        })
        addDrawableChild(TextFieldWidgetPlaceholderPassword(textRenderer, width / 2 - TEXTFIELD_WIDTH / 2, height / 2 + 10, TEXTFIELD_WIDTH, DEFAULT_BUTTON_HEIGHT, Text.of("Password")).also {
            passwordTextField = it
            it.setMaxLength(Int.MAX_VALUE)
            if (proxy?.proxyAuthentication != null) {
                it.text = proxy?.proxyAuthentication?.password!!
            }
        })

        addDrawableChild(ButtonWidget(width / 2 - DEFAULT_BUTTON_WIDTH / 2, height / 2 + 50, DEFAULT_BUTTON_WIDTH, DEFAULT_BUTTON_HEIGHT, Text.of("Done")) {
            if (ipTextField?.text?.isEmpty()!! || portTextField?.text?.isEmpty()!!) {
                status = Text.literal(when {
                    ipTextField?.text?.isEmpty()!! && portTextField?.text?.isEmpty()!! -> "IP and port are empty"
                    ipTextField?.text?.isEmpty()!! -> "IP is empty"
                    portTextField?.text?.isEmpty()!! -> "Port is empty"
                    else -> ""
                }).styled { it.withColor(Formatting.RED) }
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
                    status = Text.literal("Port is not numeric").styled { it.withColor(Formatting.RED) }
                } catch (unknownHostException: UnknownHostException) {
                    status = Text.literal("Invalid IP or port").styled { it.withColor(Formatting.RED) }
                } catch (illegalArgumentException: IllegalArgumentException) {
                    status = Text.literal("Invalid IP or port").styled { it.withColor(Formatting.RED) }
                } catch (throwable: Throwable) {
                    status = Text.literal(if(throwable.message?.isNotEmpty() == true) throwable.message!! else "Unknown error").styled { it.withColor(Formatting.RED) }
                }
            }
        }.also { doneButton = it })

        addDrawableChild(ButtonWidget(width / 2 - DEFAULT_BUTTON_WIDTH / 2, height / 2 + 50 + 25, DEFAULT_BUTTON_WIDTH, DEFAULT_BUTTON_HEIGHT, Text.of("Disable")) {
            proxy = null
            status = Text.literal("Disabled").styled { it.withColor(Formatting.RED) }
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

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(context, mouseX, mouseY, delta)
        proxy?.socketAddress?.also {
            FontWrapper.textShadow(context, it.address.hostAddress + ":" + it.port + if (proxy?.ping != null) " (" + proxy?.ping + "ms)" else "", width / 2F, height / 2F - 100, centered = true)
        }
        if (status != null)
            context.drawCenteredTextWithShadow(mc.textRenderer, status!!, width / 2, height / 2 - 85, -1)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            doneButton?.onPress()
            return true
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
                return true
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    inner class RunnablePing(private val proxy: Proxy) : Runnable {
        var cancelled = false

        override fun run() {
            val socket = Socket()
            try {
                status = Text.literal("Pinging...").styled { it.withColor(Formatting.YELLOW) }
                val beginTime = System.currentTimeMillis()
                socket.connect(proxy.socketAddress, 5000)
                if (cancelled)
                    return
                val timeDelta = System.currentTimeMillis() - beginTime
                if (this.proxy == proxy) {
                    status = Text.literal("Reached proxy in " + timeDelta + "ms").styled { it.withColor(RenderUtil.colorInterpolate(Color.green, Color.red.darker(), (timeDelta / 1000.0).coerceAtMost(1.0)).rgb) }
                    proxy.ping = timeDelta
                }
            } catch (throwable: Throwable) {
                if (this.proxy == proxy) {
                    status = Text.literal(when {
                        throwable is SocketTimeoutException -> "Timeout reached, unreachable"
                        throwable is IOException -> "Failed to reach proxy"
                        throwable is IllegalBlockingModeException -> "Illegal blocking method"
                        throwable is IllegalArgumentException -> "Invalid IP or port"
                        throwable.message?.isNotEmpty() == true -> throwable.message
                        else -> "Unknown error"
                    }).styled { it.withColor(Formatting.RED) }
                }
                throwable.printStackTrace()
            } finally {
                socket.close()
            }
        }
    }
}