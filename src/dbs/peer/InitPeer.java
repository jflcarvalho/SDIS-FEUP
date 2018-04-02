package dbs.peer;

class InitPeer {

    public static void main(String[] args) {
        /* Needed for Mac OS X */
        System.setProperty("java.net.preferIPv4Stack", "true");

        Peer initiator_Peer = new Peer(args);
        initiator_Peer.start();
    }
}
