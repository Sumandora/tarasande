package su.mandora.tarasande_custom_minecraft.optimizations

import com.mojang.datafixers.util.Pair
import net.minecraft.client.gui.hud.InGameHud
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.scoreboard.ScoreboardObjective
import net.minecraft.scoreboard.ScoreboardPlayerScore
import net.minecraft.scoreboard.Team
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.screen.blursystem.ManagerBlur
import su.mandora.tarasande.util.render.RenderUtil
import su.mandora.tarasande_custom_minecraft.tarasandevalues.optimization.scoreboard.ScoreboardValues
import kotlin.math.ceil
import kotlin.math.floor
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
        val halfHeight = height / 2.0f

        val background = mc.options.getTextBackgroundColor(0.3f)
        val titleBackground = mc.options.getTextBackgroundColor(0.4f)

        if(ScoreboardValues.blur.value) {
            matrices!!.push()
            ManagerBlur.bind(false)
            RenderUtil.fill(matrices, (screenWidth - maxWidth - 2).toDouble(), (halfScreenHeight - halfHeight).toDouble(), screenWidth.toDouble(), (halfScreenHeight + halfHeight).toDouble(), -1)
            mc.framebuffer.beginWrite(false)
            matrices.pop()
        }

        InGameHud.fill(matrices, screenWidth - maxWidth - 2, floor(halfScreenHeight - halfHeight).toInt(), screenWidth, floor(halfScreenHeight - halfHeight + fontHeight).toInt(), titleBackground)
        inGameHud.textRenderer.draw(matrices, title, screenWidth - maxWidth / 2.0f - inGameHud.textRenderer.getWidth(title) / 2.0f, floor(halfScreenHeight - halfHeight) + 1.0f, -1)

        InGameHud.fill(matrices, screenWidth - maxWidth - 2, floor(halfScreenHeight - halfHeight + fontHeight).toInt(), screenWidth, ceil(halfScreenHeight + halfHeight).toInt(), background)


        lines.forEachIndexed { index, it ->
            inGameHud.textRenderer.draw(matrices, it.second, (screenWidth - maxWidth).toFloat(), halfScreenHeight + halfHeight - fontHeight - index * inGameHud.textRenderer.fontHeight, -1)
            if(ScoreboardValues.showScoreNumber.value) {
                val score = Formatting.RED.toString() + it.first.score
                inGameHud.textRenderer.draw(matrices, score, (screenWidth - inGameHud.textRenderer.getWidth(score)).toFloat(), (halfScreenHeight + halfHeight - fontHeight - index * inGameHud.textRenderer.fontHeight).toFloat(), -1)
            }
        }
    }
}