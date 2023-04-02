package su.mandora.tarasande_custom_minecraft.tarasandevalues.debug

enum class ConnectionState(val display: String) {
    UNKNOWN(""),

    RESOLVING_SRV("Resolving SRV..."),
    STARTING_NETTY_CONNECTION("Starting Netty connection..."),
    RESOLVING_IP("Resolving IP..."),
    CONNECTING("Connecting..."),
    SENDING_LOGIN_PACKETS("Sending login packets..."),
    WAITING_FOR_RESPONSE("Waiting for response..."),
    VERIFYING_SESSION("Verifying session..."),
    ENCRYPTING("Encrypting..."),
    SUCCESS("Success!")
}
