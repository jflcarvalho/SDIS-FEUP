package com.Utils;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {
    /**
     * Encode string to SHA-256
     *
     * @param data
     * @return strinng encoded in SHA-256
     */
    public static String hashString(String data){
        String encodedHash = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte [] hashByte = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            encodedHash = getHex(hashByte);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return encodedHash;
    }

    /**
     *  Convert byte[] to a string in hexadecimal form
     *
     * @param raw
     * @return string with hexadecimal form
     */
    public static String getHex(byte[] raw){
        return DatatypeConverter.printHexBinary(raw);
    }
}
