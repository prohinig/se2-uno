package games.winchester.unodeluxe.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;

public class NetworkUtils {

    /**
     * Get local IPV4 address. If not available, or an exception occurs, <code>null</code> is returned
     * @return IPv4 Address or <code>null</code>
     */
    public static String getLocalIpAddress() {
        try {
            for (NetworkInterface currentInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                for(InetAddress address : Collections.list(currentInterface.getInetAddresses())){

                    // get IPV4 address
                    if (!address.isLoopbackAddress() && address instanceof Inet4Address) {
                        return address.getHostAddress();
                    }
                }
            }
        } catch(Exception ignored){ }
        return null;
    }
}
