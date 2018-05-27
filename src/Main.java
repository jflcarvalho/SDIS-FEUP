import peers.ChordNode;
import peers.DatabaseManager;
import peers.Node;
import peers.Protocol.ChordMessage;
import ui.UI_Login;

import java.util.Scanner;

import static utils.Constants.MENU_TITLE;

public class Main {
    public static void main(String[] args){
        Scanner in = new Scanner(System.in);
        System.out.println("port to listen");
        int port = Integer.parseInt(in.nextLine());
        DatabaseManager node = new DatabaseManager(port);
        node.print();
        System.out.println("join?");
        String nodeAddress = in.nextLine();
        if(nodeAddress.length() > 0) {
            Node contact = new Node(nodeAddress);
            node.join(contact);
        } else node.join(null);

        System.out.print(MENU_TITLE);
        UI_Login.authMenu(node);
    }
}
