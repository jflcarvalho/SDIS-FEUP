import peers.*;
import user.User;

import java.util.Scanner;

public class Main {
    public static void main(String[] args){
        Scanner in = new Scanner(System.in);
        System.out.println("Worker (w) / Database (d)");
        String choice = in.nextLine();
        if(choice.equals("w"))
            worker();
        else if(choice.equals("d"))
            database();
    }

    private static void database(){
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

    private static void worker(){
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
