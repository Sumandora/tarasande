package de.florianmichael.vialegacy.api.type._1_7_6_10;

import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.opennbt.NBTIO;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

import java.io.*;

public class NBTType_1_7_6_10 extends Type<CompoundTag> {
	public NBTType_1_7_6_10() {
		super(CompoundTag.class);
	}

	@Override
	public CompoundTag read(ByteBuf buffer) {
		short length = buffer.readShort();
		if (length < 0) {return null;}
		ByteBufInputStream byteBufInputStream = new ByteBufInputStream(buffer);
		DataInputStream dataInputStream = new DataInputStream(byteBufInputStream);
		try {
			return (CompoundTag) NBTIO.readTag((DataInput) dataInputStream);
		} catch (Throwable throwable) {throwable.printStackTrace();}
		finally {
			try {
				dataInputStream.close();
			} catch (IOException e) {e.printStackTrace();}
		}
		return null;
	}

	@Override
	public void write(ByteBuf buffer, CompoundTag nbt) throws Exception {
		if (nbt == null) {
			buffer.writeShort(-1);
		} else {
			ByteBuf buf = buffer.alloc().buffer();
			ByteBufOutputStream bytebufStream = new ByteBufOutputStream(buf);
			DataOutputStream dataOutputStream = new DataOutputStream(bytebufStream);
			NBTIO.writeTag((DataOutput) dataOutputStream, nbt);
			dataOutputStream.close();
			buffer.writeShort(buf.readableBytes());
			buffer.writeBytes(buf);
			buf.release();
		}
	}
}