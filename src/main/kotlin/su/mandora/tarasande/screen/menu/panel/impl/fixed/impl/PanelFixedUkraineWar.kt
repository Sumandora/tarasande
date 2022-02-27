package su.mandora.tarasande.screen.menu.panel.impl.fixed.impl

import com.gargoylesoftware.htmlunit.BrowserVersion
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController
import com.gargoylesoftware.htmlunit.SilentCssErrorHandler
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.HtmlElement
import com.gargoylesoftware.htmlunit.html.HtmlPage
import com.gargoylesoftware.htmlunit.javascript.SilentJavaScriptErrorListener
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.MathHelper
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.screen.menu.panel.Alignment
import su.mandora.tarasande.screen.menu.panel.impl.fixed.PanelFixed
import su.mandora.tarasande.util.render.RenderUtil
import java.awt.Color
import java.util.concurrent.CopyOnWriteArrayList
import java.util.logging.Level
import java.util.logging.Logger

class PanelFixedUkraineWar(x: Double, y: Double) : PanelFixed("Ukraine war", x, y, 75.0, resizable = false) {

    private val animations = HashMap<String, Double>()
    private val news = CopyOnWriteArrayList<String>()

    init {
        val t = Thread {
            System.getProperties()["org.apache.commons.logging.simplelog.defaultlog"] = "trace"
            Logger.getLogger("com.gargoylesoftware.htmlunit").level = Level.OFF
            Logger.getLogger("org.apache.commons.httpclient").level = Level.OFF
            val webClient = WebClient(BrowserVersion.CHROME)
            webClient.ajaxController = NicelyResynchronizingAjaxController()
            webClient.options.isJavaScriptEnabled = false
            webClient.options.isActiveXNative = false
            webClient.options.isCssEnabled = false
            webClient.options.isPrintContentOnFailingStatusCode = false
            webClient.options.isThrowExceptionOnScriptError = false
            webClient.options.isThrowExceptionOnFailingStatusCode = false
            webClient.cssErrorHandler = SilentCssErrorHandler()
            webClient.javaScriptErrorListener = SilentJavaScriptErrorListener()

            while(true) {
                if(!opened) {
                    Thread.sleep(1000)
                    continue
                }

                val p = webClient.getPage<HtmlPage>("https://liveuamap.com/")
                if(p.asNormalizedText().contains("Your IP")) {
                    // cloudflare
                    Thread.sleep(1000)
                    continue
                }
                try {
                    val feedler = p.getByXPath<HtmlElement>("//*[@id=\"feedler\"]")[0]
                    news.clear()
                    for(child in feedler.childNodes)
                        news.add(child.asNormalizedText())
                } catch (t: Throwable) {
                    t.printStackTrace()
                }

                Thread.sleep(30000)
            }
        }
        t.name = "Ukraine war lookup"
        t.start()
    }

    override fun renderContent(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        var index = 1.0
        for (it in news) {
            if(!animations.containsKey(it))
                continue
            val animation = animations[it]!!
            val accent = TarasandeMain.get().clientValues?.accentColor?.getColor()!!
            val alpha = (animation * 255 - (index-1) * 10).toInt()
            if(alpha <= 0) break

            val color = Color(accent.red, accent.green, accent.blue, alpha)
            RenderSystem.enableBlend()
            it.split("\n").forEach {
                var it = it
                val parts = ArrayList<String>()
                while(MinecraftClient.getInstance().textRenderer.getWidth(it) > 300) {
                    val str = MinecraftClient.getInstance().textRenderer.trimToWidth(it, 300)
                    parts.add(str)
                    it = it.substring(str.length)
                }
                if(it.isNotEmpty())
                    parts.add(it)
                for(it in parts) {
                    when (alignment) {
                        Alignment.LEFT -> RenderUtil.drawWithSmallShadow(matrices, it, (x - (MinecraftClient.getInstance().textRenderer.getWidth(it) * (1.0 - animation))).toFloat(), (y + MinecraftClient.getInstance().textRenderer.fontHeight * index).toFloat(), color.rgb)
                        Alignment.MIDDLE -> RenderUtil.drawWithSmallShadow(matrices, it, x.toFloat() + panelWidth.toFloat() / 2.0f - MinecraftClient.getInstance().textRenderer.getWidth(it).toFloat() / 2.0f, (y + MinecraftClient.getInstance().textRenderer.fontHeight * index).toFloat(), color.rgb)
                        Alignment.RIGHT -> RenderUtil.drawWithSmallShadow(matrices, it, (x + panelWidth - MinecraftClient.getInstance().textRenderer.getWidth(it) * animation).toFloat(), (y + MinecraftClient.getInstance().textRenderer.fontHeight * index).toFloat(), color.rgb)
                    }
                    index += animation
                }
            }
        }
    }

    override fun isVisible(): Boolean {
        news.forEach { news ->
            var animation = animations.putIfAbsent(news, 0.0)
            if (animation == null || animation.isNaN()) animation = 0.0 else {
                animation += 0.005 * RenderUtil.deltaTime
            }
            animations[news] = MathHelper.clamp(animation, 0.0, 1.0)
        }

        return opened
    }

}