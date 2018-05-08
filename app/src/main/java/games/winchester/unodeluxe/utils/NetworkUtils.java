package games.winchester.unodeluxe.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NetworkUtils {
    /**
     * Private constructor for utility class
     */
    private NetworkUtils() {}

    /**
     * Get local IPV4 addresses.
     *
     * <p>If not available, an empty list is returned</p>
     * <p>If an exception occurs,<code>null</code> is returned</p>
     * @return A list or <code>null</code>
     */
    public static List<String> getLocalIpAddresses() {

        try {
            final List<String> addresses = new ArrayList<>(5);

            for (NetworkInterface currentInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                for(InetAddress address : Collections.list(currentInterface.getInetAddresses())){
                    // get IPV4 address
                    if (!address.isLoopbackAddress() && address instanceof Inet4Address) {
                        addresses.add(address.getHostAddress());
                    }
                }
            }

            return addresses;

        } catch(Exception ignored){
            return new ArrayList<>();
        }
    }
}
