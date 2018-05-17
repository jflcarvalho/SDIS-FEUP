package utils;

import java.util.Date;

public abstract class Constants {
    public static final int PACKET_LENGTH = 65536;
    public static final boolean DEBUG = true;
    public static final double VERSION = 0.1;

    /* STRINGS */

    // MENUS
    public static final String MENU_TITLE = "\n" +
                    "   ____ _           _                                  \n" +
                    "  / ___| |_   _ ___| |_ ___ _ __      WELCOME          \n" +
                    " | |   | | | | / __| __/ _ \\ '__|                     \n" +
                    " | |___| | |_| \\__ \\ ||  __/ |        Version:   " + VERSION + "\n" +
                    "  \\____|_|\\__,_|___/\\__\\___|_|        Date:      " + (new Date()).toString() + "\n" +
                    "             ____                  _                   \n" +
                    "            / ___|  ___ _ ____   _(_) ___ ___          \n" +
                    "            \\___ \\ / _ \\ '__\\ \\ / / |/ __/ _ \\   \n" +
                    "             ___) |  __/ |   \\ V /| | (_|  __/        \n" +
                    "            |____/ \\___|_|    \\_/ |_|\\___\\___|     \n" + "\n";

    public static final String MENU_AUTH =
            "1) Login     \n" +
            "2) Register  \n\n" +
            "0) Exit      \n";

    public static final String MENU_USER_AREA =
            "1) Add Task                \n" +
            "2) Delete Pending Task     \n" +
            "3) Consult Finnish Tasks   \n\n" +
            "0) Exit  \n";


    // LOGIN
    public static final String MSG_LOGIN_USER_ALREADY_EXIST = "user Already Exist";
    public static final String MSG_LOGIN_WRONG_PASSWORD = "Wrong Password";
    public static final String MSG_LOGIN_USER_DOES_NOT_EXIST = "Username not found.\nVerify if username is correct or Registry first";


    // ERROR MSG
    public static final String MSG_SLEEP_THREAD = "[ERROR] Sleeping Thread";
    public static final String MSG_GET_MAC = "[ERROR] Getting MAC Address";
}
