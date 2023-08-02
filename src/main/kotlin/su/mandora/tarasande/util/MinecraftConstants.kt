package su.mandora.tarasande.util

import net.minecraft.entity.EntityStatuses
import net.minecraft.server.network.ServerPlayNetworkHandler
import kotlin.math.sqrt

// These constants may change with a game update, check in case that happens (the names are in yarn mappings, since that is what this projects currently uses)
const val MAX_FOOD_LEVEL = 20 // In HungerManager
const val DEFAULT_FLIGHT_SPEED = 0.05F // In PlayerAbilities
const val MAX_PLAYER_MOVE = 100.0 // In ServerPlayNetworkHandler (in PlayerMoveC2SPacket processing)
const val DEFAULT_REACH = 3.0 // In GameRenderer#updateTargetedEntity (Note that the 9.0 is squared)
const val DEFAULT_BLOCK_REACH = 4.5 // In PlayerInteractionManager#interactBlock (Note that the 20.25 is squared)
val maxReach = sqrt(ServerPlayNetworkHandler.MAX_BREAK_SQUARED_DISTANCE)
const val SCROLLBAR_WIDTH = 6 // In EntryListWidget#updateScrollingState
const val SERVER_INFORMATION_OFFSET = 5 // In MultiplayerServerListWidget$ServerEntry calls setMultiplayerScreenTooltip, check to ifs
const val SLOT_RENDER_SIZE = 16 // In HandledScreen#isPointOverSlot
const val UNSPRINT_SPEED_REDUCTION = 0.6 // In PlayerEntity#attack calls setVelocity with (0.6, 1.0, 0.6)
val opLevels = EntityStatuses.SET_OP_LEVEL_0..EntityStatuses.SET_OP_LEVEL_4 // ClientPlayerEntity#handleStatus, check the range
const val DEFAULT_WALK_SPEED = 0.28 // Just calculate the distance from previous position to current position, at walking speed
