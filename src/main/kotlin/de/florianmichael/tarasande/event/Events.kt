package de.florianmichael.tarasande.event

import net.minecraft.client.gui.screen.Screen
import su.mandora.tarasande.base.event.Event

class EventChangeScreen(var newScreen: Screen?) : Event(true)
