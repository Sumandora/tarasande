package net.tarasandedevelopment.tarasande.util.connection

import java.net.InetSocketAddress

class Proxy(val socketAddress: InetSocketAddress, val type: ProxyType) {

    var proxyAuthentication: ProxyAuthentication? = null
    var ping: Long? = null

    constructor(socketAddress: InetSocketAddress, type: ProxyType, proxyAuthentication: ProxyAuthentication?) : this(socketAddress, type) {
        this.proxyAuthentication = proxyAuthentication
    }
}

class ProxyAuthentication(val username: String, val password: String?)

enum class ProxyType(val printable: String) { HTTP("HTTP"), SOCKS4("Socks 4"), SOCKS5("Socks 5") }