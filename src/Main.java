import peers.*;
import user.User;

import java.util.Scanner;

public class Main {
    public static void main(String[] args){
        if(args.length != 0 && args[0].equals("Worker")){
            worker(args);
            return;
        } else if(args.length != 0 && args[0].equals("Database")){
            database(args);
            return;
        }

        Scanner in = new Scanner(System.in);
        System.out.println("Worker (w) / Database (d)");
        String choice = in.nextLine();
        if(choice.equals("w"))
            worker(args);
        else if(choice.equals("d"))
            database(args);
    }

    private static void database(String[] args){
        if(args.length != 0){
            DatabaseManager node = new DatabaseManager(Integer.parseInt(args[1]));
            node.print();
            if(args.length == 3 && args[2].length() > 0) {
                Node contact = new Node(args[2]);
                node.join(contact);
            } else node.join(null);
            return;
        }

        Scanner in = new Scanner(System.in);
        System.out.println("Port to listen?");
        int port = Integer.parseInt(in.nextLine());

        DatabaseManager node = new DatabaseManager(port);
        node.print();

        System.out.println("Join?");
        String nodeAddress = in.nextLine();
        if(nodeAddress.length() > 0) {
            Node contact = new Node(nodeAddress);
            node.join(contact);
        } else node.join(null);
    }

    private static void worker(String[] args){
        if(args.length != 0){
            Worker node;
            if(args[2].length() > 0) {
                Node database = new Node(args[2]);
                node = new Worker(Integer.parseInt(args[1]), database);
            } else return;
            node.print();
            if(args.length == 4 && args[3].length() > 0) {
                Node contact = new Node(args[3]);
                node.join(contact);
            } else node.join(null);
            return;
        }

        Scanner in = new Scanner(System.in);
        System.out.println("Port to listen?");
        int port = Integer.parseInt(in.nextLine());

        System.out.println("Database Node?");
        String nodeAddress = in.nextLine();

        Worker node;
        if(nodeAddress.length() > 0) {
            Node database_Node = new Node(nodeAddress);
            node = new Worker(port, database_Node);
        } else {
            return;
        }
        node.print();

        System.out.println("Join?");
        nodeAddress = in.nextLine();
        if(nodeAddress.length() > 0) {
            Node contact = new Node(nodeAddress);
            node.join(contact);
        } else node.join(null);
    }
}
