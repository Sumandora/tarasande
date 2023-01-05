package de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.rewriter;

import com.viaversion.viaversion.libs.fastutil.objects.Object2ObjectMap;
import com.viaversion.viaversion.libs.fastutil.objects.Object2ObjectOpenHashMap;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.rewriter.ComponentRewriter;

public class TranslationRewriter {

    private static final Object2ObjectMap<String, String> TRANSLATIONS = new Object2ObjectOpenHashMap<>(37, 0.99F);

    static {
        TRANSLATIONS.put("menu.playdemo", "Play Demo World");
        TRANSLATIONS.put("options.ao.off", "Off");
        TRANSLATIONS.put("options.framerateLimit", "Performance");
        TRANSLATIONS.put("options.resourcepack", "Resource Packs");
        TRANSLATIONS.put("performance.max", "Max FPS");
        TRANSLATIONS.put("performance.balanced", "Balanced");
        TRANSLATIONS.put("performance.powersaver", "Power saver");
        TRANSLATIONS.put("key.forward", "Forward");
        TRANSLATIONS.put("key.left", "Left");
        TRANSLATIONS.put("key.back", "Back");
        TRANSLATIONS.put("key.right", "Right");
        TRANSLATIONS.put("key.drop", "Drop");
        TRANSLATIONS.put("key.chat", "Chat");
        TRANSLATIONS.put("key.fog", "Toggle Fog");
        TRANSLATIONS.put("key.attack", "Attack");
        TRANSLATIONS.put("key.use", "Use Item");
        TRANSLATIONS.put("key.command", "Command");
        TRANSLATIONS.put("resourcePack.title", "Select Resource Pack");
        TRANSLATIONS.put("tile.dirt.name", "Dirt");
        TRANSLATIONS.put("tile.sand.name", "Sand");
        TRANSLATIONS.put("tile.flower.name", "Flower");
        TRANSLATIONS.put("tile.rose.name", "Rose");
        TRANSLATIONS.put("item.fishRaw.name", "Raw Fish");
        TRANSLATIONS.put("item.fishCooked.name", "Cooked Fish");
        TRANSLATIONS.put("commands.give.usage", "/give <player> <item> [amount] [data]");
        TRANSLATIONS.put("commands.give.success", "Given %s (ID %s) * %s to %s");
        TRANSLATIONS.put("commands.scoreboard.objectives.add.wrongType", "Invalid objective criteria type. Valid types are: %s");
        TRANSLATIONS.put("commands.scoreboard.objectives.list.count", "Showing %s objective(s) on scoreboard");
        TRANSLATIONS.put("commands.scoreboard.players.list.count", "Showing %s tracked players on the scoreboard");
        TRANSLATIONS.put("commands.scoreboard.players.list.player.count", "Showing %s tracked objective(s) for %s");
        TRANSLATIONS.put("commands.scoreboard.teams.list.count", "Showing %s teams on the scoreboard");
        TRANSLATIONS.put("commands.scoreboard.teams.list.player.count", "Showing %s player(s) in team %s");
        TRANSLATIONS.put("commands.scoreboard.teams.empty.usage", "/scoreboard teams clear <name>");
        TRANSLATIONS.put("commands.scoreboard.teams.option.usage", "/scoreboard teams option <team> <friendlyfire|color> <value>");
        TRANSLATIONS.put("commands.weather.usage", "/weather <clear/rain/thunder> [duration in seconds]");
        TRANSLATIONS.put("mco.configure.world.subscription.extend", "Extend");
        TRANSLATIONS.put("mco.configure.world.restore.question.line1", "Your realm will be restored to a previous version");
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
