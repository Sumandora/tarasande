package de.florianmichael.viabeta.api.rewriter;

import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectArrayList;
import com.viaversion.viaversion.libs.fastutil.objects.ObjectList;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.*;

import java.util.List;

public abstract class AbstractItemRewriter {

    private final ObjectList<RewriteEntry> REWRITE_ENTRIES = new ObjectArrayList<>();
    protected final String HACK_TAG_NAME;
    protected final String protocolName;
    private final boolean jsonName;

    public AbstractItemRewriter(final String protocolName, final boolean jsonName) {
        this.HACK_TAG_NAME = protocolName.replace(".", "_") + "_ProtocolHack_" + System.currentTimeMillis();
        this.protocolName = protocolName;
        this.jsonName = jsonName;
    }

    protected void registerRemappedItem(final int oldItemId, final int newItemId, final String newItemName) {
        registerRemappedItem(oldItemId, newItemId, -1, newItemName);
    }

    protected void registerRemappedItem(final int oldItemId, final int newItemId, final int newItemMeta, final String newItemName) {
        registerRemappedItem(oldItemId, -1, newItemId, newItemMeta, newItemName);
    }

    protected void registerRemappedItem(final int oldItemId, final int oldItemMeta, final int newItemId, final int newItemMeta, final String newItemName) {
        REWRITE_ENTRIES.add(new RewriteEntry(oldItemId, (short) oldItemMeta, newItemId, (short) newItemMeta, newItemName));
    }

    public void rewriteRead(final Item item) {
        if (item == null) return;

        for (RewriteEntry rewriteEntry : REWRITE_ENTRIES) {
            if (rewriteEntry.rewrites(item)) {
                setRemappedNameRead(item, rewriteEntry.newItemName);
                if (rewriteEntry.newItemMeta != -1) {
                    item.setData(rewriteEntry.newItemMeta);
                }
                item.setIdentifier(rewriteEntry.newItemID);
            }
        }
    }

    public void rewriteWrite(final Item item) {
        if (item == null) return;

        setRemappedTagWrite(item);
    }


    private void setRemappedNameRead(final Item item, final String name) {
        //Set protocol hack tag for later remapping
        CompoundTag protocolHackTag = (item.tag() != null && item.tag().contains(HACK_TAG_NAME) ? item.tag().get(HACK_TAG_NAME) : new CompoundTag());
        if (item.tag() == null || !item.tag().contains(HACK_TAG_NAME)) {
            protocolHackTag.put("Id", new IntTag(item.identifier()));
            protocolHackTag.put("Meta", new ShortTag(item.data()));
        }

        //Get Item tag
        CompoundTag tag = item.tag();
        if (tag == null) {
            tag = new CompoundTag();
            item.setTag(tag);
            protocolHackTag.put("RemoveTag", new IntTag(0));
        }
        tag.put(HACK_TAG_NAME, protocolHackTag);

        //Set name/lore of item
        CompoundTag display = tag.get("display");
        if (display == null) {
            display = new CompoundTag();
            tag.put("display", display);
            protocolHackTag.put("RemoveDisplayTag", new IntTag(0));
        }
        if (display.contains("Name")) {
            ListTag lore = display.get("Lore");
            if (lore == null) {
                lore = new ListTag();
                display.put("Lore", lore);
                protocolHackTag.put("RemoveLore", new IntTag(0));
            }
            if (this.jsonName) {
                lore.add(new StringTag(this.messageToJson("§r " + this.protocolName + " Item ID: " + item.identifier() + " (" + name + ")").toString()));
            } else {
                lore.add(new StringTag("§r " + this.protocolName + " Item ID: " + item.identifier() + " (" + name + ")"));
            }
            protocolHackTag.put("RemoveLastLore", new IntTag(0));
        } else {
            if (this.jsonName) {
                display.put("Name", new StringTag(this.messageToJson("§r" + this.protocolName + " " + name).toString()));
            } else {
                display.put("Name", new StringTag("§r" + this.protocolName + " " + name));
            }
            protocolHackTag.put("RemoveDisplayName", new IntTag(0));
        }
    }

    private void setRemappedTagWrite(final Item item) {
        if (item.tag() == null) return;
        if (!item.tag().contains(HACK_TAG_NAME)) return;

        CompoundTag tag = item.tag();
        CompoundTag protocolHackTag = tag.get(HACK_TAG_NAME);
        tag.remove(HACK_TAG_NAME);

        item.setIdentifier(((IntTag) protocolHackTag.get("Id")).asInt());
        item.setData(((ShortTag) protocolHackTag.get("Meta")).asShort());
        if (protocolHackTag.contains("RemoveLastLore")) {
            ListTag lore = ((CompoundTag) tag.get("display")).get("Lore");
            List<Tag> tags = lore.getValue();
            tags.remove(lore.size() - 1);
            lore.setValue(tags);
        }
        if (protocolHackTag.contains("RemoveLore")) {
            ((CompoundTag) tag.get("display")).remove("Lore");
        }
        if (protocolHackTag.contains("RemoveDisplayName")) {
            ((CompoundTag) tag.get("display")).remove("Name");
        }
        if (protocolHackTag.contains("RemoveDisplayTag")) {
            tag.remove("display");
        }
        if (protocolHackTag.contains("RemoveTag")) {
            item.setTag(null);
        }
    }

    private JsonObject messageToJson(final String s) {
        JsonObject ob = new JsonObject();
        ob.addProperty("text", s);
        return ob;
    }


    public static class RewriteEntry {

        private final int oldItemID;
        private final short oldItemMeta;
        private final int newItemID;
        private final short newItemMeta;
        private final String newItemName;

        public RewriteEntry(final int oldItemID, final short oldItemMeta, final int newItemID, final short newItemMeta, final String newItemName) {
            this.oldItemID = oldItemID;
            this.oldItemMeta = oldItemMeta;
            this.newItemID = newItemID;
            this.newItemMeta = newItemMeta;
            this.newItemName = newItemName;
        }

        public int getOldItemID() {
            return this.oldItemID;
        }

        public short getOldItemMeta() {
            return this.oldItemMeta;
        }

        public boolean rewrites(final Item item) {
            return item.identifier() == this.oldItemID && (this.oldItemMeta == -1 || this.oldItemMeta == item.data());
        }

    }

}
