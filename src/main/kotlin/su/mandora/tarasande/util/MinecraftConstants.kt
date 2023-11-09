package su.mandora.tarasande.util

import net.minecraft.entity.EntityStatuses
import net.minecraft.server.network.ServerPlayNetworkHandler
import net.minecraft.util.UseAction
import kotlin.math.sqrt

// These constants may change with a game update, check in case that happens (the names are in yarn mappings, since that is what this projects currently uses)
const val MAX_FOOD_LEVEL = 20 // In HungerManager
const val DEFAULT_FLIGHT_SPEED = 0.05F // In PlayerAbilities
const val DEFAULT_REACH = 3.0 // In GameRenderer#updateTargetedEntity (Note that the 9.0 is squared)
const val DEFAULT_BLOCK_REACH = 4.5 // In PlayerInteractionManager#interactBlock (Note that the 20.25 is squared)
val maxReach = sqrt(ServerPlayNetworkHandler.MAX_BREAK_SQUARED_DISTANCE)
const val SCROLLBAR_WIDTH = 6 // In EntryListWidget#updateScrollingState
const val SERVER_INFORMATION_OFFSET = 5 // In MultiplayerServerListWidget$ServerEntry calls setMultiplayerScreenTooltip, check to ifs
const val SLOT_RENDER_SIZE = 16 // In HandledScreen#isPointOverSlot
const val UNSPRINT_SPEED_REDUCTION = 0.6 // In PlayerEntity#attack calls setVelocity with (0.6, 1.0, 0.6)
val opLevels = EntityStatuses.SET_OP_LEVEL_0..EntityStatuses.SET_OP_LEVEL_4 // ClientPlayerEntity#handleStatus, check the range
const val DEFAULT_WALK_SPEED = 0.28 // Just calculate the distance from previous position to current position, at walking speed
const val TRIDENT_USE_TIME = 10 // TridentItem#onStoppedUsing
const val INVENTORY_SYNC_ID = 0
const val MAX_NAME_LENGTH = 16
const val PROJECTILE_GRAVITY = 0.006 // This is calculated based on samples
const val DEFAULT_TPS = 20 // In MinecraftClient: Instantiation of RenderTickCounter
const val DEFAULT_PLACE_DELAY = 4 // MinecraftClient#doItemUse: The assignment of itemUseCooldown
val timeBasedUseActions = arrayOf(UseAction.EAT, UseAction.DRINK, UseAction.BOW, UseAction.SPEAR, UseAction.CROSSBOW, UseAction.BRUSH) // UseActions which require time to be used (like Eating; unlike Blocking)
const val DEFAULT_CONTAINER_WIDTH = 176 // Walk the inheritance tree from InventoryScreen
const val DEFAULT_CONTAINER_HEIGHT = 166
const val INFINITE_FPS_VALUE = 260 // Check call to RenderSystem.limitDisplayFPS in MinecraftClient#render
const val DEFAULT_SNEAK_SLOWDOWN = 0.3 // ClientPlayerEntity check Input#tick second parameter (take the base of the clamp)
const val DEFAULT_LANGUAGE_CODE = "en_us" // GameOptions#language
const val DEFAULT_THIRD_PERSON_DISTANCE = 4.0 // Camera#update: check call to clipToSpace

// The following are rendering constants like paddings, widths, heights etc.
// They don't have to be accurate but in order to have a uniform look with the base game, they should be close
const val DEFAULT_BUTTON_WIDTH = 100 // Minecraft uses 98 (who came up with this value)
const val DEFAULT_BUTTON_HEIGHT = 20 // In the ButtonWidget Builder the default height is set in the initializer
const val BUTTON_PADDING = 3 // The game seems to be even less uniform on this on, sometimes 4, sometimes 3
const val TEXTFIELD_WIDTH = 200 // CreateWorldScreen: World name field (I don't know where the +8 comes from, lets ignore it)

// Mapping related constants (We can't use the name, since fabric will run in intermediary mode when using launchers)
val useActionMapping = hashMapOf(
    UseAction.NONE to "None",
    UseAction.EAT to "Eat",
    UseAction.DRINK to "Drink",
    UseAction.BLOCK to "Block",
    UseAction.BOW to "Bow",
    UseAction.SPEAR to "Spear",
    UseAction.CROSSBOW to "Crossbow",
    UseAction.SPYGLASS to "Spyglass",
    UseAction.TOOT_HORN to "Toot horn",
    UseAction.BRUSH to "Brush"
)
