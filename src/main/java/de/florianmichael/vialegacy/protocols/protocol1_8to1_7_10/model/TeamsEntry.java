package de.florianmichael.vialegacy.protocols.protocol1_8to1_7_10.model;

public class TeamsEntry {

    public String uniqueName;
    public String prefix;
    public String name;
    public String suffix;

    public TeamsEntry(String uniqueName, String prefix, String name, String suffix) {
        this.uniqueName = uniqueName;
        this.prefix = prefix;
        this.name = name;
        this.suffix = suffix;
    }

    public String concat(String player) {
        return prefix + player + suffix;
    }
}
