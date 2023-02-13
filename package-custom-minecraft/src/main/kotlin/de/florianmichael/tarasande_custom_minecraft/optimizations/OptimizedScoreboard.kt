package de.florianmichael.tarasande_custom_minecraft.optimizations

import com.mojang.datafixers.util.Pair
import de.florianmichael.tarasande_custom_minecraft.tarasandevalues.optimization.scoreboard.ScoreboardValues
import net.minecraft.client.gui.hud.InGameHud
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.scoreboard.ScoreboardObjective
import net.minecraft.scoreboard.ScoreboardPlayerScore
import net.minecraft.scoreboard.Team
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.tarasandedevelopment.tarasande.mc
import net.tarasandedevelopment.tarasande.system.screen.blursystem.ManagerBlur
import net.tarasandedevelopment.tarasande.util.render.RenderUtil
import kotlin.math.max

object OptimizedScoreboard {

    private const val SCOREBOARD_JOINER = ": "
    private val joinerWidth = mc.textRenderer.getWidth(SCOREBOARD_JOINER)

    fun renderScoreboardSidebar(inGameHud: InGameHud, matrices: MatrixStack?, objective: ScoreboardObjective) {
        val scoreboard = objective.scoreboard
        var playerScores = scoreboard.getAllPlayerScores(objective)
            .filter { it.playerName != null && !it.playerName.startsWith("#") }

        if(ScoreboardValues.limitEntries.value)
            playerScores = playerScores.filterIndexed { index, _ -> index < ScoreboardValues.maxEntries.value }

        val lines = ArrayList<Pair<ScoreboardPlayerScore, MutableText>>(playerScores.size)

        val title = objective.displayName
        var maxWidth = inGameHud.textRenderer.getWidth(title)
        for (scoreboardPlayerScore in playerScores) {
            val text2 = Team.decorateName(scoreboard.getPlayerTeam(scoreboardPlayerScore.playerName), Text.literal(scoreboardPlayerScore.playerName))
            lines.add(Pair.of(scoreboardPlayerScore, text2))
            maxWidth = max(maxWidth, inGameHud.textRenderer.getWidth(text2) + joinerWidth + inGameHud.textRenderer.getWidth(scoreboardPlayerScore.score.toString()))
        }

        val screenWidth = mc.window.scaledWidth
        val screenHeight = mc.window.scaledHeight
        val halfScreenHeight = screenHeight / 2

        val fontHeight = inGameHud.textRenderer.fontHeight

        val height = (playerScores.size + 1 /* title */) * fontHeight
        val halfHeight = height / 2

        val background = mc.options.getTextBackgroundColor(0.3f)
        val titleBackground = mc.options.getTextBackgroundColor(0.4f)

        if(ScoreboardValues.blur.value) {
            matrices!!.push()
            ManagerBlur.bind(false)
            RenderUtil.fill(matrices, (screenWidth - maxWidth - 2).toDouble(), (halfScreenHeight - halfHeight).toDouble(), screenWidth.toDouble(), (halfScreenHeight + halfHeight).toDouble(), -1)
            mc.framebuffer.beginWrite(false)
            matrices.pop()
        }

        InGameHud.fill(matrices, screenWidth - maxWidth - 2, halfScreenHeight - halfHeight, screenWidth, halfScreenHeight + halfHeight, titleBackground)
        inGameHud.textRenderer.draw(matrices, title, (screenWidth - maxWidth / 2 - inGameHud.textRenderer.getWidth(title) / 2).toFloat(), (halfScreenHeight - halfHeight + 1).toFloat(), -1)

        InGameHud.fill(matrices, screenWidth - maxWidth - 2, halfScreenHeight - halfHeight + fontHeight, screenWidth, halfScreenHeight - halfHeight, background)


        lines.forEachIndexed { index, it ->
            inGameHud.textRenderer.draw(matrices, it.second, (screenWidth - maxWidth).toFloat(), (halfScreenHeight + halfHeight - fontHeight - index * inGameHud.textRenderer.fontHeight).toFloat(), -1)
            if(ScoreboardValues.showScoreNumber.value) {
                val score = Formatting.RED.toString() + it.first.score
                inGameHud.textRenderer.draw(matrices, score, (screenWidth - inGameHud.textRenderer.getWidth(score)).toFloat(), (halfScreenHeight + halfHeight - fontHeight - index * inGameHud.textRenderer.fontHeight).toFloat(), -1)
            }
        }

//        for (pair in lines) {
//            val scoreboardPlayerScore2 = pair.first as ScoreboardPlayerScore
//            val text3 = pair.second as Text
//            val string = "" + Formatting.RED + scoreboardPlayerScore2.score
//            val t = m - ++p * inGameHud.textRenderer.fontHeight
//            val u = inGameHud.scaledWidth - 3 + 2
//            InGameHud.fill(matrices, o - 2, t, u, t + inGameHud.textRenderer.fontHeight, background)
//            inGameHud.textRenderer.draw(matrices, string, (u - inGameHud.textRenderer.getWidth(string)).toFloat(), t.toFloat(), -1)
//            if (p != playerScores.size) continue
//            InGameHud.fill(matrices, o - 2, t - inGameHud.textRenderer.fontHeight - 1, u, t - 1, titleBackground)
//            InGameHud.fill(matrices, o - 2, t - 1, u, t, background)
//            inGameHud.textRenderer.draw(matrices, title, (o + maxWidth / 2 - i / 2).toFloat(), (t - inGameHud.textRenderer.fontHeight).toFloat(), -1)
//        }
    }
}