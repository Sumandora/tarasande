package de.florianmichael.viabeta.protocol.protocol1_7_6_10to1_7_2_5.rewriter;

import com.viaversion.viaversion.libs.fastutil.objects.Object2ObjectMap;
import com.viaversion.viaversion.libs.fastutil.objects.Object2ObjectOpenHashMap;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.rewriter.ComponentRewriter;

public class TranslationRewriter {

    private static final Object2ObjectMap<String, String> TRANSLATIONS = new Object2ObjectOpenHashMap<>(86, 0.99F);

    static {
        TRANSLATIONS.put("generator.amplified", "AMPLIFIED");
        TRANSLATIONS.put("$o", "Play Demo World");
        TRANSLATIONS.put("options.serverTextures", "Server Textures");
        TRANSLATIONS.put("mco.title", "Minecraft Realms");
        TRANSLATIONS.put("mco.terms.buttons.agree", "Agree");
        TRANSLATIONS.put("mco.terms.buttons.disagree", "Don't Agree");
        TRANSLATIONS.put("mco.terms.title", "Realms Terms of Service");
        TRANSLATIONS.put("mco.terms.sentence.1", "I agree to Minecraft Realms");
        TRANSLATIONS.put("mco.terms.sentence.2", "Terms of Service");
        TRANSLATIONS.put("mco.buy.realms.title", "Buy a Realm");
        TRANSLATIONS.put("mco.buy.realms.buy", "I want one!");
        TRANSLATIONS.put("mco.selectServer.play", "Play");
        TRANSLATIONS.put("mco.selectServer.configure", "Configure");
        TRANSLATIONS.put("mco.selectServer.leave", "Leave Realm");
        TRANSLATIONS.put("mco.selectServer.create", "Create Realm");
        TRANSLATIONS.put("mco.selectServer.buy", "Buy Realm");
        TRANSLATIONS.put("mco.selectServer.moreinfo", "More Info");
        TRANSLATIONS.put("mco.selectServer.expired", "Expired Server");
        TRANSLATIONS.put("mco.selectServer.open", "Open Server");
        TRANSLATIONS.put("mco.selectServer.closed", "Closed Server");
        TRANSLATIONS.put("mco.selectServer.locked", "Locked Server");
        TRANSLATIONS.put("mco.selectServer.expires.days", "Expires in %s days");
        TRANSLATIONS.put("mco.selectServer.expires.day", "Expires in a day");
        TRANSLATIONS.put("mco.selectServer.expires.soon", "Expires soon");
        TRANSLATIONS.put("mco.configure.world.edit.title", "Edit Realm");
        TRANSLATIONS.put("mco.configure.world.title", "Configure Realm");
        TRANSLATIONS.put("mco.configure.world.name", "Name");
        TRANSLATIONS.put("mco.configure.world.description", "Description");
        TRANSLATIONS.put("mco.configure.world.location", "Location");
        TRANSLATIONS.put("mco.configure.world.invited", "Invited");
        TRANSLATIONS.put("mco.configure.world.buttons.edit", "Edit");
        TRANSLATIONS.put("mco.configure.world.buttons.reset", "Reset Realm");
        TRANSLATIONS.put("mco.configure.world.buttons.done", "Done");
        TRANSLATIONS.put("mco.configure.world.buttons.delete", "Delete");
        TRANSLATIONS.put("mco.configure.world.buttons.open", "Open Realm");
        TRANSLATIONS.put("mco.configure.world.buttons.close", "Close Realm");
        TRANSLATIONS.put("mco.configure.world.buttons.invite", "Invite");
        TRANSLATIONS.put("mco.configure.world.buttons.uninvite", "Uninvite");
        TRANSLATIONS.put("mco.configure.world.buttons.backup", "Backups");
        TRANSLATIONS.put("mco.configure.world.buttons.subscription", "Subscription");
        TRANSLATIONS.put("mco.configure.world.invite.profile.name", "Name");
        TRANSLATIONS.put("mco.configure.world.uninvite.question", "Are you sure that you want to uninvite");
        TRANSLATIONS.put("mco.configure.world.status", "Status");
        TRANSLATIONS.put("mco.configure.world.subscription.title", "Subscription Info");
        TRANSLATIONS.put("mco.configure.world.subscription.daysleft", "Days Left");
        TRANSLATIONS.put("mco.configure.world.subscription.start", "Start Date");
        TRANSLATIONS.put("mco.configure.world.subscription.extend", "Extend Subscription");
        TRANSLATIONS.put("mco.create.world.location.title", "Locations");
        TRANSLATIONS.put("mco.create.world.location.warning", "You may not get the exact location you select");
        TRANSLATIONS.put("mco.create.world.wait", "Creating the realm...");
        TRANSLATIONS.put("mco.create.world.seed", "Seed (Optional)");
        TRANSLATIONS.put("mco.reset.world.title", "Reset Realm");
        TRANSLATIONS.put("mco.reset.world.warning", "This will permanently delete your realm!");
        TRANSLATIONS.put("mco.reset.world.seed", "Seed (Optional)");
        TRANSLATIONS.put("mco.reset.world.resetting.screen.title", "Resetting Realm...");
        TRANSLATIONS.put("mco.configure.world.close.question.line1", "Your realm will become unavailable.");
        TRANSLATIONS.put("mco.configure.world.close.question.line2", "Are you sure you want to do that?");
        TRANSLATIONS.put("mco.configure.world.leave.question.line1", "If you leave this realm you won't see it unless invited again");
        TRANSLATIONS.put("mco.configure.world.leave.question.line2", "Are you sure you want to do that?");
        TRANSLATIONS.put("mco.configure.world.reset.question.line1", "Your realm will be regenerated and your current realm will be lost");
        TRANSLATIONS.put("mco.configure.world.reset.question.line2", "Are you sure you want to do that?");
        TRANSLATIONS.put("mco.configure.world.restore.question.line1", "Your realm will be restored to date");
        TRANSLATIONS.put("mco.configure.world.restore.question.line2", "Are you sure you want to do that?");
        TRANSLATIONS.put("mco.configure.world.restore.download.question.line1", "You will be redirected to your default browser to download your world map.");
        TRANSLATIONS.put("mco.configure.world.restore.download.question.line2", "Do you want to continue?");
        TRANSLATIONS.put("mco.more.info.question.line1", "You will be redirected to your default browser to see the page.");
        TRANSLATIONS.put("mco.more.info.question.line2", "Do you want to continue?");
        TRANSLATIONS.put("mco.connect.connecting", "Connecting to the online server...");
        TRANSLATIONS.put("mco.connect.authorizing", "Logging in...");
        TRANSLATIONS.put("mco.connect.failed", "Failed to connect to the online server");
        TRANSLATIONS.put("mco.create.world", "Create");
        TRANSLATIONS.put("mco.client.outdated.title", "Client Outdated!");
        TRANSLATIONS.put("mco.client.outdated.msg", "Your client is outdated, please consider updating it to use Realms");
        TRANSLATIONS.put("mco.backup.title", "Backups");
        TRANSLATIONS.put("mco.backup.button.restore", "Restore");
        TRANSLATIONS.put("mco.backup.restoring", "Restoring your realm");
        TRANSLATIONS.put("mco.backup.button.download", "Download Latest");
        TRANSLATIONS.put("mco.template.title", "Realm Templates");
        TRANSLATIONS.put("mco.template.button.select", "Select");
        TRANSLATIONS.put("mco.template.default.name", "Select Template (Optional)");
        TRANSLATIONS.put("mco.template.name", "Template");
        TRANSLATIONS.put("mco.invites.button.accept", "Accept");
        TRANSLATIONS.put("mco.invites.button.reject", "Reject");
        TRANSLATIONS.put("mco.invites.title", "Pending Invitations");
        TRANSLATIONS.put("mco.invites.pending", "New invitations!");
        TRANSLATIONS.put("mco.invites.nopending", "No pending invitations!");
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
