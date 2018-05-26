import peers.ChordNode;
import peers.Node;
import ui.UI_Login;

import java.util.Scanner;

import static utils.Constants.MENU_TITLE;

public class Main {
    public static void main(String[] args){
        //System.out.print(MENU_TITLE);
        //String username;
        //username = UI_Login.authMenu();
        //System.out.println(username);

        Scanner in = new Scanner(System.in);
        System.out.println("port to listen");
        int port = Integer.parseInt(in.nextLine());
        ChordNode node = new ChordNode(port);
        node.print();
        System.out.println("join?");
        String nodeAdress = in.nextLine();
        Node contact = new Node(nodeAdress);
        node.join(contact);
    }
}
