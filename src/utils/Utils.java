package utils;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static utils.Constants.MSG_GET_MAC;
import static utils.Constants.MSG_SLEEP_THREAD;

public abstract class Utils {
    /**
     * Encode string to SHA-256
     *
     * @param data
     * @return string encoded in SHA-256
     */
    public static byte[] hashString(String data){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte [] hashByte = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            return hashByte;
        } catch (NoSuchAlgorithmException e) {
            exceptionPrint(e, "[ERROR] Hashing String");
        }
        return null;
    }

    public static int get32bitHashValue(BigInteger bigInteger) {
        return Integer.remainderUnsigned(Math.abs(bigInteger.intValue()), 128);
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
     * Method to get MAC address of PC
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
            exceptionPrint(e, MSG_GET_MAC);
        }
        return null;
    }

    /**
     * Method to sleep the working thread
     *
     * @param sleepingTime time to make sleep thread
     */
    public static void sleepThread(long sleepingTime){
        try {
            Thread.sleep(sleepingTime);
        } catch (InterruptedException e) {
            exceptionPrint(e, MSG_SLEEP_THREAD);
        }
    }

    /**
     * Method to sleep the working thread a random time
     *
     * @param sleepingTime time to make sleep thread
     */
    public static void sleepRandomTime(int sleepingTime){
        try {
            Thread.sleep((long) (Math.random() * sleepingTime));
        } catch (InterruptedException e) {
            exceptionPrint(e, MSG_SLEEP_THREAD);
        }
    }

    public static Integer inputIntBetween(int min, int max){
        int a;
        System.out.print(": ");
        do {
            try {
                a = Character.getNumericValue(System.in.read());
            } catch (IOException e) {
                exceptionPrint(e, ""); //TODO: make error message
                return null;
            }
        } while (a < min || a > max);
        cleanIn();
        return a;
    }

    private static void cleanIn(){
        try {
            System.in.skip(System.in.available());
        } catch (IOException e) {
            exceptionPrint(e, ""); //TODO: make error message
        }
    }

    public static void exceptionPrint(Throwable e, String errorExplain){
        if(Constants.DEBUG)
            e.printStackTrace();
        else
            System.out.println(errorExplain);
    }
}
