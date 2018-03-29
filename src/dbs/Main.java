package dbs;

import dbs.peer.Peer;

import java.net.SocketException;
import java.util.Scanner;

import static dbs.utils.Constants.testFilePath;

public class Main {

    public static void main(String[] args) throws SocketException {
        /* Needed for Mac OS X */
        System.setProperty("java.net.preferIPv4Stack", "true");
        Peer initiator_Peer = new Peer(args);
        initiator_Peer.start();

        System.out.println("What u wanna do?");
        Scanner scan = new Scanner(System.in);
        if(scan.next().equals("1"))
            initiator_Peer.backup(testFilePath, 2);
    }
}
