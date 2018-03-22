package com.DBS.Utils;

import javax.xml.bind.DatatypeConverter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
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

    /**
     *
     * @return byte[] with MAC Address
     */
    public static byte[] getMAC(){
        InetAddress address = null;
        try {
            address = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        NetworkInterface networkInterface = null;
        try {
            if (address != null) {
                networkInterface = NetworkInterface.getByInetAddress(address);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        try {
            if (networkInterface != null) {
                return networkInterface.getHardwareAddress();
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }
}
