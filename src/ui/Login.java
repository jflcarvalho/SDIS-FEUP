package ui;

import static utils.Constants.MENU_AUTH;
import static utils.Utils.inputIntBetween;

public abstract class Login {
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

    private static String loginMenu(){
        return null;
    }

    private static String registerMenu(){
        return null;
    }

}
