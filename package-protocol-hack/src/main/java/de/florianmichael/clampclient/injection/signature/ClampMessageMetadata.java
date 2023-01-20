package de.florianmichael.clampclient.injection.signature;

public record ClampMessageMetadata(String plain, long timestamp, long salt) {
}
