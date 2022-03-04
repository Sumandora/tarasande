package su.mandora.tarasande.screen.menu.information

import net.minecraft.client.MinecraftClient
import net.minecraft.entity.LivingEntity
import net.minecraft.text.TranslatableText
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.base.screen.menu.information.Information
import su.mandora.tarasande.mixin.accessor.IMinecraftClient
import su.mandora.tarasande.mixin.accessor.IRenderTickCounter
import su.mandora.tarasande.module.combat.ModuleKillAura
import su.mandora.tarasande.module.misc.ModuleMurderMystery
import su.mandora.tarasande.module.misc.ModuleTickBaseManipulation
import su.mandora.tarasande.util.player.PlayerUtil
import kotlin.math.floor

class InformationTimeShifted : Information("Tick base manipulation", "Time shifted") {
    private val moduleTickBaseManipulation = TarasandeMain.get().managerModule?.get(ModuleTickBaseManipulation::class.java)!!

    override fun getMessage(): String? {
        if (!moduleTickBaseManipulation.enabled)
            return null
        if (moduleTickBaseManipulation.shifted == 0L)
            return null
        return moduleTickBaseManipulation.shifted.toString() + " (" + floor(moduleTickBaseManipulation.shifted / ((MinecraftClient.getInstance() as IMinecraftClient).renderTickCounter as IRenderTickCounter).tickTime).toInt() + ")"
    }
}

class InformationSimulatedDamage : Information("Kill aura", "Simulated Damage") {
    override fun getMessage(): String? {
        val killAura = TarasandeMain.get().managerModule?.get(ModuleKillAura::class.java)!!
        val targets = killAura.targets
        if (!killAura.enabled || targets.isEmpty())
            return null
        val list = ArrayList<String>()
        for (target in targets)
            if (target.first is LivingEntity) {
                val entName = target.first.name.asString()
                list.add((if (entName.trim().isEmpty()) target.first.javaClass.simpleName else entName) + " " + PlayerUtil.simulateAttack(target.first as LivingEntity))
            }
        if (list.isEmpty())
            return null
        return "\n" + list.joinToString("\n")
    }
}

class InformationMurderer : Information("Murder Mystery", "Suspected murderers") {
    override fun getMessage(): String? {
        val murderMystery = TarasandeMain.get().managerModule?.get(ModuleMurderMystery::class.java)!!
        if (murderMystery.enabled)
            if (murderMystery.suspects.isNotEmpty()) {
                return "\n" + murderMystery.suspects.entries.joinToString("\n") { it.key.name + " (" + it.value.joinToString(" and ") { (it.name as TranslatableText).string } + ")" }
            }

        return null
    }
}