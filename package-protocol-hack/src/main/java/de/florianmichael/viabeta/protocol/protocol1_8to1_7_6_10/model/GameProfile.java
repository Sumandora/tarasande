package de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.model;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class GameProfile {

    public static final GameProfile NULL = new GameProfile();

    public String userName;
    public UUID uuid;
    public Map<String, List<Property>> properties = new HashMap<>();

    private final UUID offlineUuid;

    private GameProfile() {
        this.offlineUuid = new UUID(0, 0);
    }

    public GameProfile(final String userName) {
        if (userName == null) throw new IllegalStateException("Username can't be null");
        this.userName = userName;
        this.offlineUuid = this.uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + userName).getBytes(StandardCharsets.UTF_8));
    }

    public GameProfile(final String userName, final UUID uuid) {
        if (userName == null || uuid == null) throw new IllegalStateException("Username and UUID can't be null");
        this.userName = userName;
        this.uuid = uuid;
        this.offlineUuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + userName).getBytes(StandardCharsets.UTF_8));
    }

    public void addProperty(final Property property) {
        this.properties.computeIfAbsent(property.key, k -> new ArrayList<>()).add(property);
    }

    public List<Property> getAllProperties() {
        return this.properties.values().stream().reduce((p1, p2) -> {
            final List<Property> merge = new ArrayList<>();
            merge.addAll(p1);
            merge.addAll(p2);
            return merge;
        }).orElseGet(ArrayList::new);
    }

    public boolean isOffline() {
        return this.offlineUuid.equals(this.uuid);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameProfile that = (GameProfile) o;
        return Objects.equals(userName, that.userName) && Objects.equals(uuid, that.uuid) && Objects.equals(properties, that.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName, uuid, properties);
    }

    @Override
    public String toString() {
        return "GameProfile{" +
                "userName='" + userName + '\'' +
                ", uuid=" + uuid +
                '}';
    }

    public static class Property {

        public String key;
        public String value;
        public String signature;

        public Property(final String key, final String value) {
            this.key = key;
            this.value = value;
        }

        public Property(final String key, final String value, final String signature) {
            this(key, value);
            this.signature = signature;
        }

    }

}
