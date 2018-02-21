package com.main;

import com.client.Client;
import com.server.Server;

public class Main {

    /*!
     * Usage:
     *   Client: java Main -C <host_name> <port> <data>
     *     Example: java Main -C localhost 5000 "REGISTER 34-22-GT Timon"
     *     Example: java Main -C localhost 5000 "LOOKUP 34-22-GT"
     *   Server: java Main -S <port>
     *     Example: java Main -S 5000
     */
    public static void main(String[] args) {
        if (args.length == 4 && (args[0].equals("--client") || args[0].equals("-C"))) {
            System.out.println("Initializing Client");
            Client client = new Client(args[1], Integer.parseInt(args[2]), args[3]);
        } else if (args.length == 4 && (args[0].equals("--server") || args[0].equals("-S"))) {
            System.out.println("Initializing Server");
            Server server = new Server(Integer.parseInt(args[1]), args[2], Integer.parseInt(args[3]));
            server.Run();
        }
        return;
    }
}
