package su.mandora.tarasande.util.connection

import java.net.InetSocketAddress

class Proxy(val socketAddress: InetSocketAddress, val type: ProxyType, val ping: Long) {

	var proxyAuthentication: ProxyAuthentication? = null

	constructor(socketAddress: InetSocketAddress, type: ProxyType, ping: Long, proxyAuthentication: ProxyAuthentication) : this(socketAddress, type, ping) {
		this.proxyAuthentication = proxyAuthentication
	}
}

class ProxyAuthentication(val username: String, val password: String?)

enum class ProxyType(val printable: String) { HTTP("HTTP"), SOCKS4("Socks 4"), SOCKS5("Socks 5") }