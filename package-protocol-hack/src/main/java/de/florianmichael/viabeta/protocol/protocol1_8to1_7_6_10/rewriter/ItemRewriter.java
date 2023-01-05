package de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.rewriter;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import de.florianmichael.viabeta.ViaBeta;
import de.florianmichael.viabeta.api.rewriter.AbstractItemRewriter;
import de.florianmichael.viabeta.api.data.ItemList1_6;
import de.florianmichael.viabeta.protocol.protocol1_7_6_10to1_7_2_5.provider.GameProfileFetcher;
import de.florianmichael.viabeta.protocol.protocol1_7_6_10to1_7_2_5.Protocol1_7_6_10to1_7_2_5;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.model.GameProfile;

import java.util.UUID;

public class ItemRewriter extends AbstractItemRewriter {

    public ItemRewriter() {
        super("1.7", false);
        registerRemappedItem(8, 326, "Water Block");
        registerRemappedItem(9, 326, "Stationary Water Block");
        registerRemappedItem(10, 327, "Lava Block");
        registerRemappedItem(11, 327, "Stationary Lava Block");
        registerRemappedItem(51, 385, "Fire");
        registerRemappedItem(90, 399, "Nether portal");
        registerRemappedItem(119, 381, "End portal");
        registerRemappedItem(127, 351, 3, "Cocoa Block");
        registerRemappedItem(141, 391, "Carrot Crops");
        registerRemappedItem(142, 392, "Potato Crops");
        registerRemappedItem(43, 44, "Double Stone Slab");
        registerRemappedItem(125, 126, "Double Wood Slab");
    }

    @Override
    public void rewriteRead(Item item) {
        super.rewriteRead(item);
        if (item == null) return;

        if (item.identifier() == ItemList1_6.skull.itemID && item.data() == 3 && item.tag() != null) { // player_skull
            if (!item.tag().contains("SkullOwner")) return;

            String skullOwnerName = null;
            if (item.tag().get("SkullOwner") instanceof StringTag) {
                final StringTag skullOwnerTag = item.tag().remove("SkullOwner");
                item.tag().put("1_7_SkullOwner", skullOwnerTag);
                skullOwnerName = skullOwnerTag.getValue();
            } else if (item.tag().get("SkullOwner") instanceof CompoundTag) {
                final CompoundTag skullOwnerTag = item.tag().get("SkullOwner");
                if (skullOwnerTag.get("Name") instanceof StringTag && !skullOwnerTag.contains("Id")) {
                    final StringTag skullOwnerNameTag = skullOwnerTag.get("Name");
                    skullOwnerName = skullOwnerNameTag.getValue();
                }
            }

            if (skullOwnerName != null) {
                final GameProfileFetcher gameProfileFetcher = Via.getManager().getProviders().get(GameProfileFetcher.class);
                if (!ViaBeta.getConfig().isLegacySkullLoading()) return;

                if (gameProfileFetcher.isUUIDLoaded(skullOwnerName)) {
                    final UUID uuid = gameProfileFetcher.getMojangUUID(skullOwnerName);
                    if (gameProfileFetcher.isGameProfileLoaded(uuid)) {
                        final GameProfile skullProfile = gameProfileFetcher.getGameProfile(uuid);
                        if (skullProfile == null || skullProfile.isOffline()) return;
                        item.tag().put("SkullOwner", Protocol1_7_6_10to1_7_2_5.writeGameProfileToTag(skullProfile));
                        return;
                    }
                }

                gameProfileFetcher.getMojangUUIDAsync(skullOwnerName).thenAccept(gameProfileFetcher::getGameProfile);
            }
        }
    }

    @Override
    public void rewriteWrite(Item item) {
        if (item == null) return;

        NOT_VALID:
        if (item.identifier() == ItemList1_6.skull.itemID && item.data() == 3 && item.tag() != null) { // player_skull
            if (!item.tag().contains("1_7_SkullOwner")) break NOT_VALID;
            if (item.tag().get("1_7_SkullOwner") instanceof StringTag) {
                item.tag().put("SkullOwner", item.tag().remove("1_7_SkullOwner"));
            }
        }

        super.rewriteWrite(item);
    }

}
