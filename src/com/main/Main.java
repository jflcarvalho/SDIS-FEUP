package com.main;

import com.client.Client;
import com.server.Server;

public class Main {

    /**
     *
     * @param args -Client -Server
     */
    public static void main(String[] args) {
        if (args.length != 3 && (args[0] == "--client" || args[0] == "-C")) {
            new Client(args[1], args[2]);
        } else if (args.length != 1 && (args[0] == "--server" || args[0] == "-S")) {
            new Server();
        }
        return;
    }
}
