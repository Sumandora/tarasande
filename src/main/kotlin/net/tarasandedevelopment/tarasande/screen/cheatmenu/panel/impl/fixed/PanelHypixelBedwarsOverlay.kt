package net.tarasandedevelopment.tarasande.screen.cheatmenu.panel.impl.fixed

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.mojang.authlib.GameProfile
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.screen.cheatmenu.ScreenCheatMenu
import net.tarasandedevelopment.tarasande.screen.cheatmenu.panel.Alignment
import net.tarasandedevelopment.tarasande.screen.cheatmenu.panel.Panel
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import java.net.URL
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

/*
 * This code is probably worse than it has to be, but I just couldn't think of anything better (sleep deprivation :c)
 */
class PanelHypixelBedwarsOverlay(x: Double, y: Double, screenCheatMenu: ScreenCheatMenu) : Panel("Hypixel Bedwars Overlay", x, y, 200.0, MinecraftClient.getInstance().textRenderer.fontHeight.toDouble(), background = false, fixed = true) {

    private val blackList = ArrayList<GameProfile>()
    private val playerData = ConcurrentHashMap<GameProfile, Stats>()
    private val url = "https://api.hypixel.net/player?uuid=%s&key=%s"
    private val baseLine = "Name\tPlaytime\tAge\tLevel\tFKDR\tWS\tACH\tLength"

    init {
        val t = Thread {
            while (true) {
                try {
                    if (TarasandeMain.get().clientValues.hypixelApiKey.value.isEmpty() || !opened) {
                        Thread.sleep(1000L)
                    } else {
                        val entry = playerData.entries.firstOrNull { !it.value.requested } ?: continue
                        entry.value.requested = true

                        val urlConnection = URL(String.format(url, entry.key.id.toString().replace("-", ""), TarasandeMain.get().clientValues.hypixelApiKey.value)).openConnection()
                        val jsonStr = String(urlConnection.getInputStream().readAllBytes())
                        val jsonElement = TarasandeMain.get().gson.fromJson(jsonStr, JsonElement::class.java)
                        if (jsonElement != null && !jsonElement.isJsonNull) {
                            if (!entry.value.parse(jsonElement.asJsonObject)) {
                                blackList.add(entry.key)
                                playerData.remove(entry.key)
                            }
                        }
                        Thread.sleep(500L)
                    }
                } catch (t: Throwable) {
                    t.printStackTrace()
                    Thread.sleep(10000L)
                }
            }
        }
        t.name = "Hypixel enemies lookup thread"
        t.start()
    }

    private fun drawString(matrices: MatrixStack?, str: String, x: Double, y: Double) {
        val accent = TarasandeMain.get().clientValues.accentColor.getColor()
        val width = MinecraftClient.getInstance().textRenderer.getWidth(str)
        val titleBarHeight = titleBarHeight
        when (alignment) {
            Alignment.LEFT, Alignment.MIDDLE -> {
                RenderUtil.drawWithSmallShadow(matrices, str, x.toFloat(), (y + titleBarHeight).toFloat(), accent.rgb)
            }

            Alignment.RIGHT -> {
                RenderUtil.drawWithSmallShadow(matrices, str, (x + panelWidth - width).toFloat(), (y + titleBarHeight).toFloat(), accent.rgb)
            }
        }
    }

    private fun rowsToColumns(rows: ArrayList<String>): ArrayList<ArrayList<String>> {
        val list = ArrayList<ArrayList<String>>()
        val arrays = rows.map { it.split("\t") }
        val max = arrays.maxOf { it.size }
        for (i in 0 until max) {
            val col = ArrayList<String>()
            arrays.forEach {
                if (it.size > i)
                    col.add(it[i])
                else
                    col.add("")
            }
            list.add(col)
        }
        return list
    }

    override fun renderContent(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        val list = ArrayList<String>()
        list.add(baseLine)
        playerData.forEach {
            list.add(it.key.name + "\t" + it.value.toString())
        }
        if (list.isEmpty())
            return
        val newList = rowsToColumns(list)
        var xOffset = 0
        for (col in newList) {
            var maxWidth = 0
            for ((index, entry) in col.withIndex()) {
                val width = MinecraftClient.getInstance().textRenderer.getWidth(entry)
                if (width > maxWidth)
                    maxWidth = width
                drawString(matrices, entry, x + xOffset, y + index * MinecraftClient.getInstance().textRenderer.fontHeight)
            }
            if (alignment == Alignment.RIGHT)
                xOffset -= maxWidth + 10
            else
                xOffset += maxWidth + 10
        }
    }

    override fun tick() {
        if (MinecraftClient.getInstance().networkHandler == null || !opened)
            playerData.clear()
        else {
            playerData.entries.removeIf { MinecraftClient.getInstance().networkHandler?.playerList?.none { entry -> entry.profile == it.key } == true }
            MinecraftClient.getInstance().networkHandler?.playerList?.forEach {
                if (!playerData.containsKey(it.profile) && !blackList.contains(it.profile))
                    playerData[it.profile] = Stats()
            }
        }
    }

    inner class Stats {
        internal var requested = false
        private var playSession: Long? = null
        private var age: Long? = null
        private var level: Int? = null
        private var fkdr: Double? = null
        private var winstreak: Int? = null
        private var achievements: Int? = null
        private var apiResponseLength: Int? = null

        fun parse(jsonObject: JsonObject): Boolean {
            if (!jsonObject.get("success").asBoolean)
                return false
            val player = jsonObject.get("player")
            if (player != null && !player.isJsonNull) {
                val player = player.asJsonObject
                val lastLogin = player.get("lastLogin")
                val firstLogin = player.get("firstLogin")
                if (lastLogin != null)
                    playSession = System.currentTimeMillis() - lastLogin.asLong
                if (firstLogin != null)
                    age = System.currentTimeMillis() - firstLogin.asLong
                val achievements = player.get("achievements")
                if (achievements != null && !achievements.isJsonNull) {
                    val achievements = achievements.asJsonObject
                    val bedwarsLevel = achievements.get("bedwars_level")
                    if (bedwarsLevel != null)
                        level = bedwarsLevel.asInt
                }
                val stats = player.get("stats")
                if (stats != null && !stats.isJsonNull) {
                    val stats = stats.asJsonObject
                    val bedwarsStats = stats.get("Bedwars")
                    if (bedwarsStats != null && !bedwarsStats.isJsonNull) {
                        val bedwarsStats = bedwarsStats.asJsonObject
                        val finalKills = bedwarsStats.get("final_kills_bedwars")
                        val finalDeaths = bedwarsStats.get("final_deaths_bedwars")
                        if (finalKills != null && finalDeaths != null)
                            fkdr = (finalKills.asInt / finalDeaths.asInt.toDouble() * 100).roundToInt() / 100.0
                        val winstreak = bedwarsStats.get("winstreak")
                        val twoFourWinstreak = bedwarsStats.get("two_four_winstreak")
                        val fourThreeWinstreak = bedwarsStats.get("four_three_winstreak")
                        val fourFourWinstreak = bedwarsStats.get("four_four_winstreak")
                        val eightTwoWinstreak = bedwarsStats.get("eight_two_winstreak")
                        val list = ArrayList<Int>()
                        if (winstreak != null)
                            list.add(winstreak.asInt)
                        if (twoFourWinstreak != null)
                            list.add(twoFourWinstreak.asInt)
                        if (fourThreeWinstreak != null)
                            list.add(fourThreeWinstreak.asInt)
                        if (fourFourWinstreak != null)
                            list.add(fourFourWinstreak.asInt)
                        if (eightTwoWinstreak != null)
                            list.add(eightTwoWinstreak.asInt)
                        this.winstreak = list.maxOrNull() ?: -1
                    }
                }
                val achievementsOneTime = player.get("achievementsOneTime")
                if (achievementsOneTime != null && !achievementsOneTime.isJsonNull)
                    this.achievements = achievementsOneTime.asJsonArray.size()
            }
            apiResponseLength = jsonObject.toString().length
            return true
        }

        override fun toString(): String {
            var str = ""
            if (playSession != null)
                str += TimeUnit.MILLISECONDS.toHours(playSession!!).toString() + "h"
            str += "\t"
            if (age != null)
                str += TimeUnit.MILLISECONDS.toHours(age!!).toString() + "h"
            str += "\t"
            if (level != null)
                str += level.toString()
            str += "\t"
            if (fkdr != null)
                str += fkdr.toString()
            str += "\t"
            if (winstreak != null)
                str += winstreak.toString()
            str += "\t"
            if (achievements != null)
                str += achievements.toString()
            str += "\t"
            if (apiResponseLength != null)
                str += apiResponseLength.toString()

            return str
        }
    }

}