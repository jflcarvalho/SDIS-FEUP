package dbs.utils;

import javax.xml.bind.DatatypeConverter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static dbs.utils.Constants.DEBUG;

public class Utils {
    /**
     * Encode string to SHA-256
     *
     * @param data
     * @return string encoded in SHA-256
     */
    public static String hashString(String data){
        String encodedHash = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte [] hashByte = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            encodedHash = getHex(hashByte);
        } catch (NoSuchAlgorithmException e) {
            if(DEBUG)
                e.printStackTrace();
            else
                System.out.println("[ERROR] Hashing String");
        }
        return encodedHash;
    }

    /**
     *  Convert byte[] to a string in hexadecimal form
     *
     * @param raw
     * @return string with hexadecimal form
     */
    private static String getHex(byte[] raw){
        return DatatypeConverter.printHexBinary(raw);
    }

    /**
     *
     * @return byte[] with MAC Address
     */
    public static byte[] getMAC(){
        InetAddress address;
        NetworkInterface networkInterface;
        try {
            address = InetAddress.getLocalHost();
            if (address == null)
                return null;
            networkInterface = NetworkInterface.getByInetAddress(address);
            if (networkInterface != null)
                return networkInterface.getHardwareAddress();
        } catch (SocketException | UnknownHostException e) {
            if(DEBUG)
                e.printStackTrace();
            else
                System.out.println("[ERROR] Getting MAC Address");
        }
        return null;
    }

    public static void sleepThread(long sleepingTime){
        try {
            Thread.sleep(sleepingTime);
        } catch (InterruptedException e) {
            if(DEBUG)
                e.printStackTrace();
            else
                System.out.println("[ERROR] Sleeping Thread");
        }
    }

    public static void sleepRandomTime(int sleepingTime){
        try {
            Thread.sleep((long) (Math.random() * sleepingTime));
        } catch (InterruptedException e) {
            if(DEBUG)
                e.printStackTrace();
            else
                System.out.println("[ERROR] Sleeping Thread");
        }
    }
}
