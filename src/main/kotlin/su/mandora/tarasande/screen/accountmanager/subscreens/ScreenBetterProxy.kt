package su.mandora.tarasande.screen.accountmanager.subscreens

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import org.lwjgl.glfw.GLFW
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.screen.accountmanager.elements.TextFieldWidgetPlaceholder
import su.mandora.tarasande.util.connection.Proxy
import su.mandora.tarasande.util.connection.ProxyAuthentication
import su.mandora.tarasande.util.connection.ProxyType
import su.mandora.tarasande.util.render.RenderUtil
import su.mandora.tarasande.util.render.screen.ScreenBetter
import java.awt.Color
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.nio.channels.IllegalBlockingModeException
import java.util.function.Consumer

class ScreenBetterProxy(
    prevScreen: Screen?,
    private val proxy: Proxy?,
    private val proxyConsumer: Consumer<Proxy?>,
) : ScreenBetter(prevScreen) {
    private var ipTextField: TextFieldWidget? = null
    private var portTextField: TextFieldWidget? = null
    private var usernameTextField: TextFieldWidget? = null
    private var passwordTextField: TextFieldWidget? = null

    private var proxyTypeButtonWidget: ButtonWidget? = null
    private var proxyType: ProxyType? = null

    private var status: String? = null

    private var pingThread: Thread? = null

    override fun init() {
        addDrawableChild(TextFieldWidgetPlaceholder(textRenderer, width / 2 - 100, height / 2 - 50 - 15, 143, 20, Text.of("IP-Address")).also {
            ipTextField = it
            it.setMaxLength(Int.MAX_VALUE)
            if (proxy?.socketAddress != null) {
                it.text = proxy.socketAddress.address.hostAddress
            }
        })
        addDrawableChild(TextFieldWidgetPlaceholder(textRenderer, width / 2 + 47, height / 2 - 50 - 15, 53, 20, Text.of("Port")).also {
            portTextField = it
            it.setMaxLength(Int.MAX_VALUE)
            if (proxy != null) {
                it.text = proxy.socketAddress.port.toString()
            }
        })
        addDrawableChild(TextFieldWidgetPlaceholder(textRenderer, width / 2 - 100, height / 2 - 15, 200, 20, Text.of("Username")).also {
            usernameTextField = it
            it.setMaxLength(Int.MAX_VALUE)
            if (proxy?.proxyAuthentication != null) {
                it.text = proxy.proxyAuthentication?.username!!
            }
        })
        addDrawableChild(TextFieldWidgetPlaceholder(textRenderer, width / 2 - 100, height / 2 + 10, 200, 20, Text.of("Password")).also {
            passwordTextField = it
            it.setMaxLength(Int.MAX_VALUE)
            if (proxy?.proxyAuthentication != null) {
                it.text = proxy.proxyAuthentication?.password!!
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
                    val proxy = if (usernameTextField?.text?.isNotEmpty()!! && (proxyType == ProxyType.SOCKS4 || passwordTextField?.text?.isNotEmpty()!!)) Proxy(inetSocketAddress, proxyType!!, ProxyAuthentication(usernameTextField?.text!!, if (proxyType == ProxyType.SOCKS4) null else passwordTextField?.text))
                    else Proxy(inetSocketAddress, proxyType!!)
                    proxyConsumer.accept(proxy)
                    if (pingThread != null && pingThread?.isAlive!!) pingThread?.stop() // even more hacky
                    Thread {
                        val socket = Socket()
                        try {
                            status = Formatting.YELLOW.toString() + "Pinging..."
                            val beginTime = System.currentTimeMillis()
                            socket.connect(inetSocketAddress, 5000)
                            val timeDelta = System.currentTimeMillis() - beginTime
                            if (TarasandeMain.get().screens?.betterScreenAccountManager?.proxy == proxy) {
                                status = RenderUtil.formattingByHex(RenderUtil.colorInterpolate(Color.green, Color.red.darker(), (timeDelta / 1000.0).coerceAtMost(1.0)).rgb).toString() + "Reached proxy in ${timeDelta}ms"
                                proxy.ping = timeDelta
                            }
                        } catch (throwable: Throwable) {
                            if (TarasandeMain.get().screens?.betterScreenAccountManager?.proxy == proxy) {
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
                    }.also { pingThread = it }.start()
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
        })

        addDrawableChild(ButtonWidget(width / 2 - 50, height / 2 + 50 + 25, 100, 20, Text.of("Back")) {
            RenderSystem.recordRenderCall {
                close()
            }
        })

        addDrawableChild(ButtonWidget(width / 2 - 50, height / 2 + 50 + 25 * 2, 100, 20, Text.of("Disable")) {
            proxyConsumer.accept(null)
            RenderSystem.recordRenderCall {
                close()
            }
        })

        tick()
        super.init()
    }

    override fun tick() {
        passwordTextField?.visible = proxyType != ProxyType.SOCKS4
        super.tick()
    }

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(matrices, mouseX, mouseY, delta)
        drawCenteredText(matrices, textRenderer, "Proxy", width / 2, 8 - textRenderer.fontHeight / 2, Color.white.rgb)
        if (status != null) textRenderer.drawWithShadow(matrices, status!!, width / 2 - textRenderer.getWidth(status!!) / 2.0F, height / 2F - 50 - 15 - textRenderer.fontHeight - 2, Color.white.rgb)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
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
        return super.keyPressed(keyCode, scanCode, modifiers)
    }
}