package de.florianmichael.viabeta.protocol.protocol1_1to1_0_0_1.rewriter;

public class ChatFilter {

    private final static char[] ALLOWED_CHARACTERS = new char[]{
            ' ', '!', '"', '#', '$', '%', '&', '\'', '(', ')',
            '*', '+', ',', '-', '.', '/', '0', '1', '2', '3',
            '4', '5', '6', '7', '8', '9', ':', ';', '<', '=',
            '>', '?', '@', 'A', 'B', 'C', 'D', 'E', 'F', 'G',
            'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
            'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '[',
            '\\', ']', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
            'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
            's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '{', '|',
            '}', '~', '⌂', 'Ç', 'ü', 'é', 'â', 'ä', 'à', 'å',
            'ç', 'ê', 'ë', 'è', 'ï', 'î', 'ì', 'Ä', 'Å', 'É',
            'æ', 'Æ', 'ô', 'ö', 'ò', 'û', 'ù', 'ÿ', 'Ö', 'Ü',
            'ø', '£', 'Ø', '×', 'ƒ', 'á', 'í', 'ó', 'ú', 'ñ',
            'Ñ', 'ª', 'º', '¿', '®', '¬', '½', '¼', '¡', '«',
            '»', '_', '^', '\''
    };

    public static String filter(String message) {
        String allowed = new String(ALLOWED_CHARACTERS);

        for (int i = 0; i < message.length(); ++i) {
            String toReplace = Character.toString(message.charAt(i));

            if (!allowed.contains(toReplace)) {
                message = message.replaceAll(toReplace, "*");
            }
        }

        return message;
    }

}
