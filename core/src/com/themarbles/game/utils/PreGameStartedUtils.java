package com.themarbles.game.utils;

import static java.lang.Integer.parseInt;
import static java.util.Base64.getDecoder;

import com.badlogic.gdx.utils.StringBuilder;

import java.util.Base64;

public class PreGameStartedUtils {

    public static String decodeToken(String token){

        // decodes an invite token to string, formatted ip:port
        StringBuilder builder = new StringBuilder();
        Base64.Decoder decoder = getDecoder();
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
        return parseInt(decodedToken.substring(decodedToken.indexOf(":") + 1));
    }

    public static String generateToken(String host, int port){

        // generates an invite token
        Base64.Encoder encoder = Base64.getEncoder();
        String orig = host + ":" + port;
        return encoder.encodeToString(orig.getBytes());
    }
}
