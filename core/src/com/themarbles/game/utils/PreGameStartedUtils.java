package com.themarbles.game.utils;

import com.badlogic.gdx.utils.StringBuilder;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Base64;
import java.util.Enumeration;

/** Util, using to encode/decode, separate invite token and operations with ip.
 * @see com.themarbles.game.EntryPoint#inviteToken
 * **/

public class PreGameStartedUtils {

    public static String decodeToken(String token){
        // decodes an invite token to string, formatted ip:port
        StringBuilder builder = new StringBuilder();
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] decoded_bytes = decoder.decode(token);
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
        Base64.Encoder encoder = Base64.getEncoder();
        String orig = host + ":" + port;
        return encoder.encodeToString(orig.getBytes());
    }

    public static String getDeviceIP(){
        // getting device-hosted server ip
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {

                NetworkInterface networkInterface = en.nextElement();

                for (Enumeration<InetAddress> address = networkInterface.getInetAddresses(); address.hasMoreElements();) {

                    InetAddress inetAddress = address.nextElement();

                    if (!inetAddress.isLoopbackAddress() &&
                            !inetAddress.isLinkLocalAddress() &&
                            inetAddress.isSiteLocalAddress()) {

                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ignored) {
        }

        return "127.0.0.1";
    }
}
