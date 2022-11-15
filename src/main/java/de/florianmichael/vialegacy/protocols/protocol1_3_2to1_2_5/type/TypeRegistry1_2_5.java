package de.florianmichael.vialegacy.protocols.protocol1_3_2to1_2_5.type;

import de.florianmichael.vialegacy.protocols.protocol1_3_2to1_2_5.type.impl.ItemType1_2_5;
import de.florianmichael.vialegacy.protocols.protocol1_3_2to1_2_5.type.impl.MetadataListType1_2_5;

public class TypeRegistry1_2_5 {
	
	public static final ItemType1_2_5 COMPRESSED_NBT_ITEM = new ItemType1_2_5(true);
	public static final MetadataListType1_2_5 METADATA_LIST = new MetadataListType1_2_5();
}
