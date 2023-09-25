package su.mandora.tarasande_third_party.panel

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.mojang.authlib.GameProfile
import net.minecraft.client.gui.DrawContext
import su.mandora.tarasande.feature.tarasandevalue.TarasandeValues
import su.mandora.tarasande.gson
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.api.DontExport
import su.mandora.tarasande.system.base.valuesystem.impl.ValueMode
import su.mandora.tarasande.system.base.valuesystem.impl.ValueText
import su.mandora.tarasande.system.screen.panelsystem.api.PanelFixed
import su.mandora.tarasande.util.extension.javaruntime.Thread
import su.mandora.tarasande.util.render.font.FontWrapper
import su.mandora.tarasande.util.render.helper.Alignment
import java.net.URL
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

/*
 * This code is probably worse than it has to be, but I just couldn't think of anything better (sleep deprivation :c)
 */
class PanelHypixelOverlay : PanelFixed("Hypixel Overlay", 200.0, FontWrapper.fontHeight().toDouble(), resizable = false) {

    @DontExport
    private val apiKey = ValueText(this, "API Key", "")
    private val fields = ValueMode(this, "Fields", true, "Name", "Playtime", "Age", "Level", "Final-Kill/Death Ratio", "Winstreak", "Achievements", "API response length")

    init {
        fields.select(0)
        fields.select(4)
    }

    private val blackList = ArrayList<GameProfile>()
    private var playerData = ConcurrentHashMap<GameProfile, Stats>()
    private val url = "https://api.hypixel.net/player?uuid=%s&key=%s"
    private val baseLine = "Name\tPlaytime\tAge\tLevel\tFKDR\tWS\tACH\tLength"

    init {
        Thread("Hypixel enemies lookup thread") {
            while (true) {
                try {
                    if (apiKey.value.isEmpty() || !opened) {
                        Thread.sleep(1000L)
                    } else {
                        val entry = playerData.entries.firstOrNull { !it.value.requested } ?: continue
                        entry.value.requested = true

                        val urlConnection = URL(String.format(url, entry.key.id.toString().replace("-", ""), apiKey.value)).openConnection()
                        val jsonStr = urlConnection.getInputStream().readAllBytes().decodeToString()
                        val jsonElement = gson.fromJson(jsonStr, JsonElement::class.java)
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
        }.start()
    }

    private fun drawString(context: DrawContext, str: String, x: Double, y: Double) {
        val accent = TarasandeValues.accentColor.getColor()
        val width = FontWrapper.getWidth(str)
        val titleBarHeight = titleBarHeight
        when (alignment) {
            Alignment.LEFT, Alignment.MIDDLE -> {
                FontWrapper.textShadow(context, str, x.toFloat(), (y + titleBarHeight).toFloat(), accent.rgb, offset = 0.5F)
            }

            Alignment.RIGHT -> {
                FontWrapper.textShadow(context, str, (x + panelWidth - width).toFloat(), (y + titleBarHeight).toFloat(), accent.rgb, offset = 0.5F)
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

    override fun renderContent(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        val list = ArrayList<String>()
        list.add(baseLine)
        playerData.forEach {
            list.add(it.key.name + "\t" + it.value.toString())
        }
        if (list.isEmpty())
            return
        val newList = rowsToColumns(list)
        var xOffset = 0
        for ((colIndex, col) in newList.withIndex()) {
            if (!fields.isSelected(colIndex))
                continue
            var maxWidth = 0
            for ((rowIndex, entry) in col.withIndex()) {
                val width = FontWrapper.getWidth(entry)
                if (width > maxWidth)
                    maxWidth = width
                drawString(context, entry, x + xOffset, y + rowIndex * FontWrapper.fontHeight())
            }
            if (alignment == Alignment.RIGHT)
                xOffset -= maxWidth + 10
            else
                xOffset += maxWidth + 10
        }
    }

    override fun tick() {
        if (mc.networkHandler == null || !opened)
            playerData = ConcurrentHashMap()
        else {
            playerData.entries.removeIf { mc.networkHandler?.playerList?.none { entry -> entry.profile == it.key } == true }
            mc.networkHandler?.playerList?.forEach {
                if (!playerData.containsKey(it.profile) && !blackList.contains(it.profile))
                    playerData[it.profile] = Stats()
            }
        }
    }

    inner class Stats {
        var requested = false
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
                @Suppress("NAME_SHADOWING")
                val player = player.asJsonObject
                val lastLogin = player.get("lastLogin")
                val firstLogin = player.get("firstLogin")
                if (lastLogin != null)
                    playSession = System.currentTimeMillis() - lastLogin.asLong
                if (firstLogin != null)
                    age = System.currentTimeMillis() - firstLogin.asLong
                val achievements = player.get("achievements")
                if (achievements != null && !achievements.isJsonNull) {
                    @Suppress("NAME_SHADOWING")
                    val achievements = achievements.asJsonObject
                    val bedwarsLevel = achievements.get("bedwars_level")
                    if (bedwarsLevel != null)
                        level = bedwarsLevel.asInt
                }
                val stats = player.get("stats")
                if (stats != null && !stats.isJsonNull) {
                    @Suppress("NAME_SHADOWING")
                    val stats = stats.asJsonObject
                    val bedwarsStats = stats.get("Bedwars")
                    if (bedwarsStats != null && !bedwarsStats.isJsonNull) {
                        @Suppress("NAME_SHADOWING")
                        val bedwarsStats = bedwarsStats.asJsonObject
                        val finalKills = bedwarsStats.get("final_kills_bedwars")
                        val finalDeaths = bedwarsStats.get("final_deaths_bedwars")
                        if (finalKills != null && finalDeaths != null)
                            fkdr = (finalKills.asInt / finalDeaths.asInt.coerceAtLeast(1).toDouble() * 100).roundToInt() / 100.0
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