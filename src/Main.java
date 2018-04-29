import ui.Login;

import static utils.Constants.MENU_TITLE;

public class Main {
    private String username;

    public static void main(String[] args){
        System.out.print(MENU_TITLE);
        Login.authMenu();
    }
}
