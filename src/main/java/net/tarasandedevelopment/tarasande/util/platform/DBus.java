package net.tarasandedevelopment.tarasande.util.platform;

import java.io.IOException;

public class DBus {

    public static String setTrack() throws IOException {
        String trackInfo = new String(new ProcessBuilder("bash",
                "-c",
                "dbus-send " +
                        "--print-reply " +
                        "--dest=" +
                        "$(dbus-send " +
                        "--session " +
                        "--dest=org.freedesktop.DBus " +
                        "--type=method_call " +
                        "--print-reply /org/freedesktop/DBus org.freedesktop.DBus.ListNames " +
                        "| grep org.mpris.MediaPlayer2 " +
                        "| sed -e 's/.*\\\"\\(.*\\)\\\"/\\1/' " +
                        "| head -n 1 " +
                        ") " +
                        "/org/mpris/MediaPlayer2 " +
                        "org.freedesktop.DBus.Properties.Get " +
                        "string:'org.mpris.MediaPlayer2.Player' " +
                        "string:'Metadata'"
        ).start().getInputStream().readAllBytes());

        boolean nextLine = false;
        for (String line : trackInfo.split("\n")) {
            if (nextLine) {
                String secondPart = line.split("string \"")[1];
                return secondPart.substring(0, secondPart.length() - 1);
            }
            if (line.contains("string \"xesam:title\"")) {
                nextLine = true;
            }
        }
        return null;
    }

}
