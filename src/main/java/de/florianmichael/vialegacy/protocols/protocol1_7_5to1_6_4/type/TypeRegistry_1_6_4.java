/*
 * Copyright (c) FlorianMichael as EnZaXD 2022
 * Created on 24.06.22, 13:55
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

package de.florianmichael.vialegacy.protocols.protocol1_7_5to1_6_4.type;

import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.vialegacy.protocols.protocol1_7_5to1_6_4.type.impl.MetadataListType_1_6_4;
import de.florianmichael.vialegacy.protocols.protocol1_7_5to1_6_4.type.impl.StringType_1_6_4;

public class TypeRegistry_1_6_4 {
	
	public static final Type<String> STRING = new StringType_1_6_4();
	public static final MetadataListType_1_6_4 METADATA_LIST = new MetadataListType_1_6_4();

}
