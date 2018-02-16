package com.main;

import com.client.Client;
import com.server.Server;

public class Main {

    public static void main(String[] args) {
        if (args.length == 4 && (args[0].equals("--client") || args[0].equals("-C"))) {
            System.out.println("Initializing Client");
            Client client = new Client(args[1], Integer.parseInt(args[2]), args[3]);
        } else if (args.length == 2 && (args[0].equals("--server") || args[0].equals("-S"))) {
            System.out.println("Initializing Server");
            Server server = new Server(Integer.parseInt(args[1]));
            server.Run();
        }
        return;
    }
}
