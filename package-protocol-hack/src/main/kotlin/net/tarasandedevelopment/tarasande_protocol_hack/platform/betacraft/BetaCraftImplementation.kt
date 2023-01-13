package net.tarasandedevelopment.tarasande_protocol_hack.platform.betacraft

import de.florianmichael.vialoadingbase.ViaLoadingBase
import de.florianmichael.vialoadingbase.util.VersionListEnum
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueMode
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.sidebar.SidebarEntry
import net.tarasandedevelopment.tarasande.util.threading.ThreadRunnableExposed
import org.jsoup.Jsoup
import java.net.URL

// Defines a Beta-Craft Server
class BetaCraftServ(val displayName: String, val onlineMode: Boolean, val address: String, val version: VersionListEnum) {

    companion object {
        fun fromJoinString(displayName: String, onlineMode: Boolean, joinString: String): BetaCraftServ? {
            return try {
                val joinStringAsArray = joinString.split("/")

                if (joinStringAsArray[4].toIntOrNull() != null) {
                    BetaCraftServ(displayName, onlineMode, joinStringAsArray[2], VersionListEnum.fromProtocolId(joinStringAsArray[4].toInt()))
                } else {
                    BetaCraftServ(displayName, onlineMode, joinStringAsArray[2], VersionListEnum.UNKNOWN)
                }
            } catch (_: Exception) {
                null
            }
        }
    }
}

const val WEB_DATA = "http://www.betacraft.uk/serverlist"

class SidebarEntryBetaCraftServers : SidebarEntry("BetaCraft Servers", "Protocol Hack") {

    private val autoRequest = ValueMode(this, "Auto request", false, "Only first time", "Everytime")
    private var serversScreen: ScreenBetterSlotListBetaCraftServers? = null

    private var hasAlreadyLoaded = false

    companion object {
        fun createLookupThread(finish: (servers: ArrayList<BetaCraftServ>) -> Unit): ThreadRunnableExposed {
            return ThreadRunnableExposed(RunnableBetaCraft { finish(it) }).apply { name = "BetaCraft servers look up thread" }
        }
    }

    class RunnableBetaCraft(val finishLoading: (ArrayList<BetaCraftServ>) -> Unit) : Runnable {
        override fun run() {
            val servers = ArrayList<BetaCraftServ>()
            val websiteHtmlCode = URL(WEB_DATA).openConnection().getInputStream().readAllBytes().decodeToString()

            for (element in Jsoup.parse(websiteHtmlCode).body().select("a")) {
                for (onlineClass in element.getElementsByClass("online")) {
                    var onlineMode = false
                    for (fontTag in onlineClass.getElementsByTag("font")) {
                        fontTag.remove()
                        onlineMode = true
                    }

                    val joinString = onlineClass.attr("href")
                    val model = BetaCraftServ.fromJoinString(element.html(), onlineMode, joinString)
                    if (model == null) {
                        ViaLoadingBase.instance().logger().severe("Skipped BetaCraft Model with invalid data: " + joinString + " named: " + element.html())
                        continue
                    }

                    servers.add(model)
                }
            }
            finishLoading(servers)
        }
    }

    override fun onClick(mouseButton: Int) {
        if (autoRequest.isSelected(0) && hasAlreadyLoaded) {
            mc.setScreen(serversScreen)
            return
        }
        createLookupThread {
            mc.executeSync {
                serversScreen = ScreenBetterSlotListBetaCraftServers(name, mc.currentScreen!!, it)
                mc.setScreen(serversScreen)
                hasAlreadyLoaded = true
            }
        }.start()
    }
}
