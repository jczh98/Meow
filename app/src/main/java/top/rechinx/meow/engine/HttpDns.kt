package top.rechinx.meow.engine

import okhttp3.Dns
import top.rechinx.meow.support.log.L
import java.net.InetAddress
import java.net.UnknownHostException
import java.util.ArrayList

class HttpDns(addresses: String) : Dns {

    private var address = addresses

    @Throws(UnknownHostException::class)
    override fun lookup(paramString: String): List<InetAddress> {
        try {
            val list = ArrayList<InetAddress>()
            list.add(InetAddress.getByName(address))
            return list
        } catch (localException: Exception) {
            localException.printStackTrace()
        }

        return Dns.SYSTEM.lookup(paramString)
    }
}