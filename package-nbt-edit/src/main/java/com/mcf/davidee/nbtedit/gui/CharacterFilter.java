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
