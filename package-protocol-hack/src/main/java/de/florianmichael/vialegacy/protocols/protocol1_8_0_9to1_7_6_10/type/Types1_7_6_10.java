/**
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.2--
 *
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license. The creator assumes no responsibility for any infringements
 * that have arisen, are arising or will arise from this project / file. If this licence is used anywhere,
 * the latest version published by the author Florian Michael (aka EnZaXD) always applies automatically.
 *
 * Changelog:
 *     v1.0:
 *         Added License
 *     v1.1:
 *         Ownership withdrawn
 *     v1.2:
 *         Version-independent validity and automatic renewal
 */

package de.florianmichael.vialegacy.protocols.protocol1_8_0_9to1_7_6_10.type;

import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import de.florianmichael.vialegacy.protocols.protocol1_8_0_9to1_7_6_10.type.impl.*;

import java.util.List;

public class Types1_7_6_10 {

    public static final Type<CompoundTag> COMPRESSED_NBT = new CompressedNBT1_7_6_10Type();
    public static final Type<Item[]> COMPRESSED_NBT_ITEM_ARRAY = new ItemArray1_7_6_10Type(true);
    public static final Type<Item> ITEM = new Item1_7_6_10Type(false);
    public static final Type<Item> COMPRESSED_NBT_ITEM = new Item1_7_6_10Type(true);
    public static final Type<List<Metadata>> METADATA_LIST = new MetadataList1_7_6_10Type();
    public static final Type<Metadata> METADATA = new Metadata1_7_6_10Type();
    public static final Type<CompoundTag> NBT = new NBT1_7_6_10Type();
    public static final Type<byte[]> BYTEARRAY = new ByteArray1_7_6_10Type();

}
