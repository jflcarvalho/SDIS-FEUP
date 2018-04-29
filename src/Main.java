import ui.UI_Login;

import static utils.Constants.MENU_TITLE;

public class Main {
    public static void main(String[] args){
        System.out.print(MENU_TITLE);
        String username;
        int tries = 0;
        do{
            username= UI_Login.authMenu();
            tries++;
        } while (username == null && tries < 3);
        System.out.println(username);
    }
}
