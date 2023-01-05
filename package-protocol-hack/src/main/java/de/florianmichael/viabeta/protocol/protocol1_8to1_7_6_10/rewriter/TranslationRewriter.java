package de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.rewriter;

import com.viaversion.viaversion.libs.fastutil.objects.Object2ObjectMap;
import com.viaversion.viaversion.libs.fastutil.objects.Object2ObjectOpenHashMap;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.rewriter.ComponentRewriter;

public class TranslationRewriter {

    private static final Object2ObjectMap<String, String> TRANSLATIONS = new Object2ObjectOpenHashMap<>(59, 0.99F);

    static {
        TRANSLATIONS.put("gui.toMenu", "Back to title screen");
        TRANSLATIONS.put("generator.amplified", "Amplified");
        TRANSLATIONS.put("disconnect.loginFailedInfo.serversUnavailable", "The authentication are currently down for maintenance.");
        TRANSLATIONS.put("options.aoDesc0", "Enable faux ambient occlusion on blocks.");
        TRANSLATIONS.put("options.framerateLimitDesc0", "Selects the maximum frame rate:");
        TRANSLATIONS.put("options.framerateLimitDesc1", "35fps, 120fps, or 200+fps.");
        TRANSLATIONS.put("options.viewBobbingDesc0", "Enables view-bob when moving.");
        TRANSLATIONS.put("options.renderCloudsDesc0", "Enables the rendering of clouds.");
        TRANSLATIONS.put("options.graphicsDesc0", "'Fancy': Enables extra transparency.");
        TRANSLATIONS.put("options.graphicsDesc1", "'Fast': Suggested for lower-end hardware.");
        TRANSLATIONS.put("options.renderDistanceDesc0", "Maximum render distance. Smaller values");
        TRANSLATIONS.put("options.renderDistanceDesc1", "run better on lower-end hardware.");
        TRANSLATIONS.put("options.particlesDesc0", "Selects the overall amount of particles.");
        TRANSLATIONS.put("options.particlesDesc1", "On lower-end hardware, less is better.");
        TRANSLATIONS.put("options.advancedOpenglDesc0", "Enables occlusion queries. On AMD and Intel");
        TRANSLATIONS.put("options.advancedOpenglDesc1", "hardware, this may decrease performance.");
        TRANSLATIONS.put("options.fboEnableDesc0", "Enables the use of Framebuffer Objects.");
        TRANSLATIONS.put("options.fboEnableDesc1", "Necessary for certain Minecraft features.");
        TRANSLATIONS.put("options.postProcessEnableDesc0", "Enables post-processing. Disabling will");
        TRANSLATIONS.put("options.postProcessEnableDesc1", "result in reduction in Awesome Levels.");
        TRANSLATIONS.put("options.showCape", "Show Cape");
        TRANSLATIONS.put("options.anisotropicFiltering", "Anisotropic Filtering");
        TRANSLATIONS.put("tile.stone.name", "Stone");
        TRANSLATIONS.put("tile.sapling.roofed_oak.name", "Dark Oak Sapling");
        TRANSLATIONS.put("tile.sponge.name", "Sponge");
        TRANSLATIONS.put("tile.stairsStone.name", "Stone Stairs");
        TRANSLATIONS.put("tile.pressurePlate.name", "Pressure Plate");
        TRANSLATIONS.put("tile.fence.name", "Fence");
        TRANSLATIONS.put("tile.fenceGate.name", "Fence Gate");
        TRANSLATIONS.put("tile.trapdoor.name", "Trapdoor");
        TRANSLATIONS.put("item.doorWood.name", "Wooden Door");
        TRANSLATIONS.put("entity.Arrow.name", "arrow");
        TRANSLATIONS.put("achievement.overkill.desc", "Deal eight hearts of damage in a single hit");
        TRANSLATIONS.put("commands.generic.deprecatedId", "Warning: Using numeric IDs will not be supported in the future. Please use names, such as '%s'");
        TRANSLATIONS.put("commands.give.notFound", "There is no such item with ID %s");
        TRANSLATIONS.put("commands.effect.usage", "/effect <player> <effect> [seconds] [amplifier]");
        TRANSLATIONS.put("commands.clear.usage", "/clear <player> [item] [data]");
        TRANSLATIONS.put("commands.time.usage", "/time <set|add> <value>");
        TRANSLATIONS.put("commands.kill.usage", "/kill");
        TRANSLATIONS.put("commands.kill.success", "Ouch! That looked like it hurt");
        TRANSLATIONS.put("commands.tp.success.coordinates", "Teleported %s to %s,%s,%s");
        TRANSLATIONS.put("commands.tp.usage", "/tp [target player] <destination player> OR /tp [target player] <x> <y> <z>");
        TRANSLATIONS.put("commands.scoreboard.usage", "/scoreboard <objectives|players|teams>");
        TRANSLATIONS.put("commands.scoreboard.objectives.usage", "/scoreboard objectives <list|add|remove|setdisplay>");
        TRANSLATIONS.put("commands.scoreboard.players.usage", "/scoreboard players <set|add|remove|reset|list>");
        TRANSLATIONS.put("commands.scoreboard.players.set.usage", "/scoreboard players set <player> <objective> <score>");
        TRANSLATIONS.put("commands.scoreboard.players.add.usage", "/scoreboard players add <player> <objective> <count>");
        TRANSLATIONS.put("commands.scoreboard.players.remove.usage", "/scoreboard players remove <player> <objective> <count>");
        TRANSLATIONS.put("commands.scoreboard.players.reset.usage", "/scoreboard players reset <player>");
        TRANSLATIONS.put("commands.scoreboard.players.reset.success", "Reset all scores of player %s");
        TRANSLATIONS.put("commands.scoreboard.teams.usage", "/scoreboard teams <list|add|remove|empty|join|leave|option>");
        TRANSLATIONS.put("commands.scoreboard.teams.empty.usage", "/scoreboard teams empty");
        TRANSLATIONS.put("commands.scoreboard.teams.option.usage", "/scoreboard teams option <team> <friendlyfire|color|seeFriendlyInvisibles> <value>");
        TRANSLATIONS.put("commands.spawnpoint.usage", "/spawnpoint OR /spawnpoint <player> OR /spawnpoint <player> <x> <y> <z>");
        TRANSLATIONS.put("commands.setworldspawn.usage", "/setworldspawn OR /setworldspawn <x> <y> <z>");
        TRANSLATIONS.put("commands.gamerule.usage", "/gamerule <rule name> <value> OR /gamerule <rule name>");
        TRANSLATIONS.put("commands.testfor.usage", "/testfor <player>");
        TRANSLATIONS.put("commands.testfor.failed", "/testfor is only usable by commandblocks with analog output");
        TRANSLATIONS.put("commands.achievement.usage", "/achievement give <stat_name> [player]");
    }

    private static final ComponentRewriter REWRITER = new ComponentRewriter() {
        @Override
        protected void handleTranslate(JsonObject object, String translate) {
            final String text = TRANSLATIONS.get(translate);
            if (text != null) {
                object.addProperty("translate", text);
            }
        }
    };

    public static String toClient(final String text) {
        return REWRITER.processText(text).toString();
    }

}
