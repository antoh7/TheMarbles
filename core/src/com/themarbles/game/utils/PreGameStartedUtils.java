package com.themarbles.game.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.regex.Pattern;

/** Util, using to generate, split invite token and do operations with ip, before game started.
 * @see com.themarbles.game.EntryPoint#inviteToken
 * **/

public class PreGameStartedUtils {

    public static String getHost(String token){
        // splits decoded token and returns host
        return token.substring(0,token.indexOf(":"));
    }

    public static int getPort(String token){
        // splits decoded token and returns port
        return Integer.parseInt(token.substring(token.indexOf(":") + 1));
    }

    public static String generateToken(String host, int port){
        // generates an invite token
        return host + ":" + port;
    }

    public static String getDeviceIP(){
        // getting device-hosted server ip
        Pattern ipPattern192_168 = Pattern.compile("^192\\.168\\.(?:[0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.(?:[0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$");
        Pattern ipPattern172_16 = Pattern.compile("^(172\\.(16|17|18|19|20|21|22|23|24|25|26|27|28|29|30|31)\\.(?:[0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.(?:[0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]))$");
        Pattern ipPattern100_64 = Pattern.compile("^(100\\.((6[4-9]|[7-9][0-9])\\.([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\.([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5]))|(100\\.1(0[0-9]|1[0-6])\\.([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\.([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5]))|(100\\.12[0-7]\\.([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\.([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])))$");
        Pattern ipPattern10_0 = Pattern.compile("^(10\\.(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?))$");
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {

                NetworkInterface networkInterface = en.nextElement();

                for (Enumeration<InetAddress> address = networkInterface.getInetAddresses(); address.hasMoreElements();) {

                    InetAddress inetAddress = address.nextElement();
                    String hostAddress = inetAddress.getHostAddress();
                    boolean matches =
                            ipPattern192_168.matcher(hostAddress).matches() ||
                            ipPattern172_16.matcher(hostAddress).matches() ||
                            ipPattern100_64.matcher(hostAddress).matches() ||
                            ipPattern10_0.matcher(hostAddress).matches();

                    if (matches) {
                        return hostAddress;
                    }
                }
            }

        } catch (SocketException ignored) {
        }

        return "127.0.0.1";
    }
}
