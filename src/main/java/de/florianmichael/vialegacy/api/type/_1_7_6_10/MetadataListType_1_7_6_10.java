package de.florianmichael.vialegacy.api.type._1_7_6_10;

import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.type.types.minecraft.MetaListTypeTemplate;
import de.florianmichael.vialegacy.api.type.TypeRegistry1_7_6_10;
import de.florianmichael.vialegacy.api.type.meta.MetaType_1_7_6_10;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

public class MetadataListType_1_7_6_10 extends MetaListTypeTemplate {

	@Override
	public List<Metadata> read(ByteBuf buffer) throws Exception {
		ArrayList<Metadata> list = new ArrayList<>();

		Metadata m;
		do {
			m = TypeRegistry1_7_6_10.METADATA.read(buffer);
			if (m != null) {
				list.add(m);
			}
		} while(m != null);

		return list;
	}

	@Override
	public void write(ByteBuf buffer, List<Metadata> metadata) throws Exception {
		for (Metadata meta : metadata)
			TypeRegistry1_7_6_10.METADATA.write(buffer, meta);

		if (metadata.isEmpty())
			TypeRegistry1_7_6_10.METADATA.write(buffer, new Metadata(0, MetaType_1_7_6_10.Byte, (byte)0));

		buffer.writeByte(127);
	}
}
