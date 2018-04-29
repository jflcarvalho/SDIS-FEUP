package ui;

import user.User;

import java.util.Scanner;

import static utils.Constants.DEBUG;
import static utils.Constants.MENU_AUTH;
import static utils.Utils.inputIntBetween;

public abstract class UI_Login {
    public static String authMenu(){
        System.out.print(MENU_AUTH);
        Integer choice = inputIntBetween(0, 2);
        if(choice == null)
            return null;
        System.out.print(choice);
        switch (choice){
            case 1:
                return loginMenu();
            case 2:
                return registerMenu();
            default:
                return null;
        }
    }

    private static String registerMenu(){
        Scanner in = new Scanner(System.in);
        System.out.println("Username ?");
        String username = in.nextLine();
        System.out.println("Password ?");
        String password = in.nextLine();
        User user;
        try {
            user = new User(username, password);
        } catch (User.LoginException e) {
            if(DEBUG)
                e.printStackTrace();
            else
                System.out.println(e.getError());
            return null;
        }
        return user.getUsername();
    }

    private static String loginMenu(){
        Scanner in = new Scanner(System.in);
        System.out.println("Username ?");
        String username = in.nextLine();
        System.out.println("Password ?");
        String password = in.nextLine();
        try {
            User.login(username, password);
        } catch (User.LoginException e) {
            if(DEBUG)
                e.printStackTrace();
            else
                System.out.println(e.getError());
            return null;
        }
        return username;
    }

}
