package de.florianmichael.tarasande_protocol_hack.tarasande.event

import net.tarasandedevelopment.tarasande.event.Event

class EventFinishLegacyCompletions(val completions: ArrayList<String>) : Event(false)
