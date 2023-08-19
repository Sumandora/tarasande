package su.mandora.tarasande_example

import net.fabricmc.api.ClientModInitializer
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventSuccessfulLoad
import su.mandora.tarasande.system.feature.commandsystem.ManagerCommand
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule
import su.mandora.tarasande.system.screen.graphsystem.ManagerGraph
import su.mandora.tarasande.system.screen.informationsystem.ManagerInformation
import su.mandora.tarasande.system.screen.panelsystem.ManagerPanel
import su.mandora.tarasande_example.examples.MyCommand
import su.mandora.tarasande_example.examples.MyInformation
import su.mandora.tarasande_example.examples.MyPanel
import su.mandora.tarasande_example.examples.graphs.MyGraph
import su.mandora.tarasande_example.examples.graphs.MyTickableGraph
import su.mandora.tarasande_example.examples.modules.AnotherModule
import su.mandora.tarasande_example.examples.modules.MyModule
import kotlin.math.abs

class TarasandeExample : ClientModInitializer {

    override fun onInitializeClient() {
        EventDispatcher.add(EventSuccessfulLoad::class.java) {
            ManagerPanel.add(MyPanel())
            ManagerInformation.add(MyInformation())
            ManagerGraph.apply {
                add(MyTickableGraph())
                val graph = MyGraph().also { add(it) }
                repeat(100) {
                    graph.add(abs(50 - it))
                }
            }
            ManagerCommand.add(MyCommand())
            ManagerModule.add(MyModule(), AnotherModule())
        }
    }
}
