package net.lenni0451.mcstructs.nbt.snbt.impl.v1_14;

import net.lenni0451.mcstructs.nbt.snbt.impl.v1_12.SNbtSerializer_v1_12;

public class SNbtSerializer_v1_14 extends SNbtSerializer_v1_12 {

    @Override
    protected String escape(String s) {
        StringBuilder out = new StringBuilder(" ");
        char openQuotation = 0;
        char[] chars = s.toCharArray();
        for (char c : chars) {
            if (c == '\\') {
                out.append("\\");
            } else if (c == '"' || c == '\'') {
                if (openQuotation == 0) {
                    if (c == '"') openQuotation = '\'';
                    else openQuotation = '"';
                }
                if (openQuotation == c) out.append("\\");
            }
            out.append(c);
        }
        if (openQuotation == 0) openQuotation = '"';
        out.setCharAt(0, openQuotation);
        out.append(openQuotation);
        return out.toString();
    }

}
