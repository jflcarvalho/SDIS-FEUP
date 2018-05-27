package ui;

import network.Network;
import peers.DatabaseManager;
import peers.Node;
import peers.Protocol.APIMessage;
import peers.Protocol.Message;
import peers.Protocol.MessageFactory;
import user.User;
import utils.Utils;

import java.io.Serializable;
import java.util.Scanner;

import static utils.Constants.MENU_AUTH;
import static utils.Utils.exceptionPrint;
import static utils.Utils.inputIntBetween;

public abstract class UI_Login {
    public static User authMenu(Node db){
        User user = null;
        APIMessage msg;
        Integer choice;
        do {
            System.out.print(MENU_AUTH);
            choice = inputIntBetween(0, 2);
            if (choice == null)
                return null;
            switch (choice) {
                case 1:
                    user = inputUser();
                    msg = (APIMessage) MessageFactory.getMessage(Message.MessageType.LOGIN, new Serializable[]{user});
                    msg = (APIMessage) Network.sendRequest(db, msg, true);
                    System.out.println(msg.getReplyValue());
                    break;
                case 2:
                    user = inputUser();
                    msg = (APIMessage) MessageFactory.getMessage(Message.MessageType.REGISTER, new Serializable[]{user});
                    msg = (APIMessage) Network.sendRequest(db, msg, true);
                    System.out.println(msg.getReplyValue() + " - " + user.getLookup());
                    break;
                default:
                    break;
            }
        } while (choice != 0);
        return user;
    }

    private static User inputUser(){
        System.out.println("LOGIN");
        Scanner in = new Scanner(System.in);
        System.out.println("Username ?");
        String username = in.nextLine();
        System.out.println("Password ?");
        String password = in.nextLine();
        return new User(username, Utils.getHex(Utils.hashString(password)));
    }

}
