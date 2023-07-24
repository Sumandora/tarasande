package su.mandora.tarasande.util

// These constants may change with a game update, check in case that happens (the names are in yarn mappings, since that is what this projects currently uses)
const val MAX_FOOD_LEVEL = 20 // In HungerManager
const val DEFAULT_FLIGHT_SPEED = 0.05F // In PlayerAbilities
const val MAX_PLAYER_MOVE = 100.0 // In ServerPlayNetworkHandler (in PlayerMoveC2SPacket processing)
const val MAX_REACH = 3.0 // In GameRenderer#updateTargetedEntity (Note that the 9.0 is squared)
const val SCROLLBAR_WIDTH = 6 // In EntryListWidget#updateScrollingState
const val SERVER_INFORMATION_OFFSET = 5 // In MultiplayerServerListWidget$ServerEntry calls setMultiplayerScreenTooltip, check to ifs
const val SLOT_RENDER_SIZE = 16 // In HandledScreen#isPointOverSlot
const val UNSPRINT_SPEED_REDUCTION = 0.6 // In PlayerEntity#attack calls setVelocity with (0.6, 1.0, 0.6)
