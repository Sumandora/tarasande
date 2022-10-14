package de.florianmichael.vialegacy.api.type._1_7_6_10;

import com.viaversion.viaversion.api.type.PartialType;
import com.viaversion.viaversion.api.type.Type;
import io.netty.buffer.ByteBuf;

public class CustomStringType_1_7_6_10 extends PartialType<String[], Integer> {

	public CustomStringType_1_7_6_10(Integer param) {
		super(param, String[].class);
	}

	public String[] read(ByteBuf buffer, Integer size) throws Exception {
		if (buffer.readableBytes() < size/4) {
			throw new RuntimeException("Readable bytes does not match expected!");
		} else {
			String[] array = new String[size];
			for (int i = 0; i<size; i++) {
				array[i] = Type.STRING.read(buffer);
			}
			return array;
		}
	}

	public void write(ByteBuf buffer, Integer size, String[] strings) throws Exception {
		for (String s : strings) Type.STRING.write(buffer, s);
	}
}