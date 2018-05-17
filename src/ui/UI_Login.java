package ui;

import user.User;

import java.util.Scanner;

import static utils.Constants.MENU_AUTH;
import static utils.Utils.exceptionPrint;
import static utils.Utils.inputIntBetween;

public abstract class UI_Login {
    public static String authMenu(){
        String username;
        do {
            System.out.print(MENU_AUTH);
            Integer choice = inputIntBetween(0, 2);
            if (choice == null)
                return null;
            System.out.print(choice);
            switch (choice) {
                case 1:
                    username = loginMenu();
                    break;
                case 2:
                    username = registerMenu();
                    break;
                default:
                    return null;
            }
        } while (username == null);
        return username;
    }

    private static String registerMenu(){
        System.out.println("SIGN IN");
        Scanner in = new Scanner(System.in);
        System.out.println("Username ?");
        String username = in.nextLine();
        System.out.println("Password ?");
        String password = in.nextLine();
        try {
            return (new User(username, password)).getUsername();
        } catch (User.LoginException e) {
            exceptionPrint(e, e.getError());
            return null;
        }
    }

    private static String loginMenu(){
        System.out.println("LOGIN");
        Scanner in = new Scanner(System.in);
        System.out.println("Username ?");
        String username = in.nextLine();
        System.out.println("Password ?");
        String password = in.nextLine();
        try {
            User.login(username, password);
        } catch (User.LoginException e) {
            exceptionPrint(e, e.getError());
            return null;
        }
        return username;
    }

}
