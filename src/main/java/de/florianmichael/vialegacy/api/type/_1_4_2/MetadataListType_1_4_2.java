package de.florianmichael.vialegacy.api.type._1_4_2;

import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.minecraft.item.DataItem;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.type.types.minecraft.MetaListTypeTemplate;
import de.florianmichael.vialegacy.api.type.TypeRegistry_1_6_4;
import de.florianmichael.vialegacy.api.type.meta.MetaType_1_6_4;
import de.florianmichael.vialegacy.api.type.meta.MetaType_1_7_6_10;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MetadataListType_1_4_2 extends MetaListTypeTemplate {
	
	@Override
	public List<Metadata> read(ByteBuf buf) throws Exception {
		ArrayList list = null;

		for (byte var2 = buf.readByte(); var2 != 127; var2 = buf.readByte()) {
			if (list == null) {
				list = new ArrayList();
			}

			int typeId = (var2 & 224) >> 5;
			int key = var2 & 31;
			Metadata metadata = null;

			switch (typeId) {
			case 0:
				metadata = new Metadata(key, MetaType_1_6_4.Byte, Byte.valueOf(buf.readByte()));
				break;

			case 1:
				metadata = new Metadata(key, MetaType_1_6_4.Short, Short.valueOf(buf.readShort()));
				break;

			case 2:
				metadata = new Metadata(key, MetaType_1_6_4.Int, Integer.valueOf(buf.readInt()));
				break;

			case 3:
				metadata = new Metadata(key, MetaType_1_6_4.Float, Float.valueOf(buf.readFloat()));
				break;

			case 4:
				metadata = new Metadata(key, MetaType_1_6_4.String, TypeRegistry_1_6_4.STRING.read(buf));
				break;

			case 5:
				int shiftedId = buf.readShort();
				Item item = null;
				if(shiftedId >= 0) {
					byte amount = buf.readByte();
					short damage = buf.readShort();
					item = new DataItem(shiftedId - 256, amount, damage, null);
				}
				metadata = new Metadata(key, MetaType_1_7_6_10.Slot, item);
				break;
			case 6:
				int x = buf.readInt();
				int y = buf.readInt();
				int z = buf.readInt();
				metadata = new Metadata(key, MetaType_1_6_4.Position, new Position(x, (short) y, z));
			}

			list.add(metadata);
		}

        return list;
	}

	@Override
	public void write(ByteBuf buf, List<Metadata> list) throws Exception {
		Iterator<Metadata> it = list.iterator();
		while(it.hasNext()) {
			Metadata obj = it.next();
			int typeId = obj.metaType().typeId();
			int var2 = (typeId << 5 | obj.id() & 31) & 255;
			buf.writeByte(var2);
	
			switch (typeId) {
			case 0:
				buf.writeByte(obj.value());
				break;
	
			case 1:
				buf.writeShort(obj.value());
				break;
	
			case 2:
				buf.writeInt(obj.value());
				break;
	
			case 3:
				buf.writeFloat(obj.value());
				break;
	
			case 4:
				TypeRegistry_1_6_4.STRING.write(buf, obj.value());
				break;
	
			case 5:
				Item item = obj.value();
				if(item != null) {
					buf.writeShort(256 + item.identifier());
					buf.writeByte(item.amount());
					buf.writeShort(item.data());
				}else {
					buf.writeShort(-1);
				}
				break;
	
			case 6:
				Position var3 = obj.value();
				buf.writeInt(var3.getX());
				buf.writeInt(var3.getY());
				buf.writeInt(var3.getZ());
			}
		}
		
		buf.writeByte(127);
	}
}
