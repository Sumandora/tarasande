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

package de.florianmichael.vialegacy.protocols.protocol1_8_0_9to1_7_6_10.type.impl;

import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.type.types.minecraft.MetaListTypeTemplate;
import de.florianmichael.vialegacy.protocols.protocol1_8_0_9to1_7_6_10.type.Types1_7_6_10;
import de.florianmichael.vialegacy.protocols.protocol1_8_0_9to1_7_6_10.type.Meta1_7_6_10Type;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

public class MetadataList1_7_6_10Type extends MetaListTypeTemplate {

	@Override
	public List<Metadata> read(ByteBuf buffer) throws Exception {
		ArrayList<Metadata> list = new ArrayList<>();

		Metadata m;
		do {
			m = Types1_7_6_10.METADATA.read(buffer);
			if (m != null) {
				list.add(m);
			}
		} while(m != null);

		return list;
	}

	@Override
	public void write(ByteBuf buffer, List<Metadata> metadata) throws Exception {
		for (Metadata meta : metadata)
			Types1_7_6_10.METADATA.write(buffer, meta);

		if (metadata.isEmpty())
			Types1_7_6_10.METADATA.write(buffer, new Metadata(0, Meta1_7_6_10Type.Byte, (byte)0));

		buffer.writeByte(127);
	}
}
