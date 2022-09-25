/*
 * Copyright (c) FlorianMichael as EnZaXD 2022
 * Created on 7/8/22, 8:24 PM
 *
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.0--
 *
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license.
 */

package de.enzaxd.viaforge.util;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.item.DataItem;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.*;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ItemRewriter;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.HandItemProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.util.registry.Registry;

import java.util.Objects;

public class FabricHandItemProvider extends HandItemProvider {

    public static ItemStack lastUsedItem = ItemStack.EMPTY;

    public static Tag minecraftTagToViaTag(final NbtElement minecraft) {
        if (minecraft instanceof final NbtByte nbtByte) return new ByteTag(nbtByte.byteValue());
        if (minecraft instanceof final NbtDouble nbtDouble) return new DoubleTag(nbtDouble.doubleValue());
        if (minecraft instanceof final NbtInt nbtInt) return new IntTag(nbtInt.intValue());
        if (minecraft instanceof final NbtLong nbtLong) return new LongTag(nbtLong.longValue());
        if (minecraft instanceof final NbtShort nbtShort) return new ShortTag(nbtShort.shortValue());
        if (minecraft instanceof final NbtFloat nbtFloat) return new FloatTag(nbtFloat.floatValue());

        if (minecraft instanceof final NbtString nbtString) return new StringTag(nbtString.asString());

        if (minecraft instanceof final NbtIntArray nbtIntArray) return new IntArrayTag(nbtIntArray.getIntArray());
        if (minecraft instanceof final NbtLongArray nbtLongArray) return new LongArrayTag(nbtLongArray.getLongArray());

        if (minecraft instanceof final NbtList nbtList) {
            final ListTag viaTag = new ListTag();

            for (NbtElement nbtElement : nbtList) {
                viaTag.add(minecraftTagToViaTag(nbtElement));
            }

            return viaTag;
        }

        if (minecraft instanceof final NbtCompound nbtCompound) {
            final CompoundTag viaTag = new CompoundTag();

            for (String key : nbtCompound.getKeys()) {
                viaTag.put(key, minecraftTagToViaTag(nbtCompound.get(key)));
            }

            return viaTag;
        }

        return new CompoundTag();
    }

    public static DataItem remapAndVia(final ItemStack stack) {
        DataItem viaItem = new DataItem();

        if (stack == ItemStack.EMPTY)
            return null;

        viaItem.setIdentifier(Registry.ITEM.getRawId(stack.getItem()));
        viaItem.setAmount(stack.getCount());
        viaItem.setData((short) stack.getDamage());

        if (stack.getNbt() != null) {
            final CompoundTag viaTag = new CompoundTag();

            for (String key : stack.getNbt().getKeys())
                viaTag.put(key, minecraftTagToViaTag(stack.getNbt().get(key)));

            viaItem.setTag(viaTag);
        }

        viaItem = (DataItem) Objects.requireNonNull(ProtocolInstances.getProtocol1_18To1_17_1().getItemRewriter()).handleItemToServer(viaItem);
        viaItem = (DataItem) Objects.requireNonNull(ProtocolInstances.getProtocol1_17To1_16_4().getItemRewriter()).handleItemToServer(viaItem);
        viaItem = (DataItem) Objects.requireNonNull(ProtocolInstances.getProtocol1_16_2To1_16_1().getItemRewriter()).handleItemToServer(viaItem);
        viaItem = (DataItem) Objects.requireNonNull(ProtocolInstances.getProtocol1_16To1_15_2().getItemRewriter()).handleItemToServer(viaItem);
        viaItem = (DataItem) Objects.requireNonNull(ProtocolInstances.getProtocol1_15To1_14_4().getItemRewriter()).handleItemToServer(viaItem);
        viaItem = (DataItem) Objects.requireNonNull(ProtocolInstances.getProtocol1_14To1_13_2().getItemRewriter()).handleItemToServer(viaItem);
        viaItem = (DataItem) Objects.requireNonNull(ProtocolInstances.getProtocol1_13_1To1_13().getItemRewriter()).handleItemToServer(viaItem);
        viaItem = (DataItem) Objects.requireNonNull(ProtocolInstances.getProtocol1_13To1_12_2().getItemRewriter()).handleItemToServer(viaItem);
        viaItem = (DataItem) Objects.requireNonNull(ProtocolInstances.getProtocol1_12To1_11_1().getItemRewriter()).handleItemToServer(viaItem);
        viaItem = (DataItem) Objects.requireNonNull(ProtocolInstances.getProtocol1_11To1_10().getItemRewriter()).handleItemToServer(viaItem);
        viaItem = (DataItem) Objects.requireNonNull(ProtocolInstances.getProtocol1_10To1_9_3_4().getItemRewriter()).handleItemToServer(viaItem);
        ItemRewriter.toServer(viaItem);

        return viaItem;
    }

    @Override
    public Item getHandItem(UserConnection info) {
        return remapAndVia(lastUsedItem);
    }
}
