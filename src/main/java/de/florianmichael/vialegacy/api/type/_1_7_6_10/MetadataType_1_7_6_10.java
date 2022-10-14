package de.florianmichael.vialegacy.api.type._1_7_6_10;

import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.type.types.minecraft.MetaTypeTemplate;
import de.florianmichael.vialegacy.api.type.meta.MetaType_1_7_6_10;
import io.netty.buffer.ByteBuf;

public class MetadataType_1_7_6_10 extends MetaTypeTemplate {
	@Override
	public Metadata read(ByteBuf buffer) throws Exception {
		byte item = buffer.readByte();
		if (item == 127) {
			return null;
		} else {
			int typeID = (item & 224) >> 5;
			MetaType_1_7_6_10 type = MetaType_1_7_6_10.byId(typeID);
			int id = item & 31;
			return new Metadata(id, type, type.type().read(buffer));
		}
	}

	@Override
	public void write(ByteBuf buffer, Metadata meta) throws Exception {
		int item = (meta.metaType().typeId() << 5 | meta.id() & 31) & 255;
		buffer.writeByte(item);
		meta.metaType().type().write(buffer, meta.getValue());
	}
}