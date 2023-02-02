/*
 * Copyright (c) FlorianMichael as EnZaXD 2022
 * Created on 7/16/22, 12:37 AM
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

package com.mcf.davidee.nbtedit.gui;

import com.mcf.davidee.nbtedit.NBTStringHelper;
import net.minecraft.SharedConstants;

public class CharacterFilter {
	public static String filerAllowedCharacters(String str, boolean section) {
        StringBuilder sb = new StringBuilder();
        char[] arr = str.toCharArray();
        int length = arr.length;

        for (int i = 0; i < length; ++i) {
            char c = arr[i];
            if (SharedConstants.isValidChar(c) || (section && (c == NBTStringHelper.SECTION_SIGN || c == '\n')))
                sb.append(c);
        }

        return sb.toString();
    }
}
