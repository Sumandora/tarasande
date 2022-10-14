package de.florianmichael.vialegacy.protocols.protocol1_8to1_7_10.chunk;

import com.viaversion.viaversion.libs.gson.JsonArray;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.util.GsonUtil;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class SpigotDebreakifier {

    private static final boolean[] validBlocks = new boolean[3168];
    private static final int[] correctedValues = new int[198];

    public static int getCorrectedData(int id, int data) {
        if (id > 197)
            return data;
        else {
            if (id == 175 && data > 8)
                data = 8;

            return validBlocks[id << 4 | data] ? data : correctedValues[id] & 15;
        }
    }

    static {
        Arrays.fill(correctedValues, -1);
        final InputStream in = SpigotDebreakifier.class.getResourceAsStream("/florianmichael/vialegacy/block-mappings-1-7-10.json");

        try {
            JsonArray json = GsonUtil.getGson().fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), JsonArray.class).getAsJsonArray();

            for (JsonElement entry : json) {
                String[] parts = entry.getAsString().split(":");
                int id = Integer.parseInt(parts[0]);
                int data = Integer.parseInt(parts[1]);
                validBlocks[id << 4 | data] = true;

                if (correctedValues[id] == -1 || data < correctedValues[id])
                    correctedValues[id] = data;

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
