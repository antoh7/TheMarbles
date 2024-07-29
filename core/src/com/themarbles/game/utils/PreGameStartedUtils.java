package com.themarbles.game.utils;

import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.StringBuilder;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Base64;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Util, using to encode/decode, separate invite token and operations with ip.
 * @see com.themarbles.game.EntryPoint#inviteToken
 * **/

public class PreGameStartedUtils {

    public static String decodeToken(String token){
        // decodes an invite token to string, formatted ip:port
        StringBuilder builder = new StringBuilder();
        byte[] decoded_bytes = Base64Coder.decode(token);
        for (byte b: decoded_bytes){
            builder.append((char) b);
        }
        return builder.toString();
    }

    public static String getHost(String decodedToken){
        // splits decoded token and returns host
        return decodedToken.substring(0,decodedToken.indexOf(":"));
    }

    public static int getPort(String decodedToken){
        // splits decoded token and returns port
        return Integer.parseInt(decodedToken.substring(decodedToken.indexOf(":") + 1));
    }

    public static String generateToken(String host, int port){
        // generates an invite token
        String orig = host + ":" + port;
        String token = Base64Coder.encodeLines(orig.getBytes()).replace("\n", "");
        return token;
    }

    public static String getDeviceIP(){
        // getting device-hosted server ip
        Pattern ipPattern = Pattern.compile("^192\\.168\\.(?:[0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.(?:[0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$");
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {

                NetworkInterface networkInterface = en.nextElement();

                for (Enumeration<InetAddress> address = networkInterface.getInetAddresses(); address.hasMoreElements();) {

                    InetAddress inetAddress = address.nextElement();
                    boolean matches = ipPattern.matcher(inetAddress.getHostAddress()).matches();

                    if (matches) {
                        return inetAddress.getHostAddress();
                    }
                }
            }

        } catch (SocketException ignored) {
        }

        return "127.0.0.1";
    }
}
