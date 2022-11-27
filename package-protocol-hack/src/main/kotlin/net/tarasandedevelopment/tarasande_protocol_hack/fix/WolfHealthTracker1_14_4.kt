package net.tarasandedevelopment.tarasande_protocol_hack.fix

object WolfHealthTracker1_14_4 {

    private val healthData = HashMap<Int, Float>()

    fun track(entityId: Int, health: Float) {
        healthData[entityId] = health
    }

    fun clear() = healthData.clear()

    fun getHealth(entityId: Int): Float {
        if (!healthData.containsKey(entityId)) return 1F

        return healthData[entityId]!!
    }
}
