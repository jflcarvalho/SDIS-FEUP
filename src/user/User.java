package user;

import database.DataBase;
import utils.Utils;

import java.io.Serializable;
import java.util.Hashtable;

import static utils.Constants.*;

public class User implements Serializable {
    private final String username;
    private final String password;

    /**
     * user constructor create user object and add to database
     *
     * @param username of the user to create a new user
     * @param password password unhashed to create a user
     * @throws LoginException in case of user already exists
     */
    public User(String username, String password) throws LoginException {
        this.username = username;
        this.password = Utils.hashString(password);
        registry(this);
    }

    /**
     * Username getter
     * @return String with the username of the user
     */
    public String getUsername() {
        return username;
    }

    /**
     * Password getter
     * @return String with the password hash of the user
     */
    public String getPassword() {
        return password;
    }


    //TODO: make javadoc
    public static void login(String username, String password) throws LoginException {
        if(!getLoginHashTable().containsKey(username))
            throw new LoginException(MSG_LOGIN_USER_DOES_NOT_EXIST);
        if(!getLoginHashTable().get(username).equals(password))
            throw new LoginException(MSG_LOGIN_WRONG_PASSWORD);
    }

    //TODO: make javadoc
    private static void registry(User user) throws LoginException{
        if(getLoginHashTable().containsKey(user.username))
            throw new LoginException(MSG_LOGIN_USER_ALREADY_EXIST);
        getLoginHashTable().put(user.username, user.password);
    }

    //TODO: make javadoc
    public static class LoginException extends Throwable{
        //TODO: make javadoc
        private String error;

        //TODO: make javadoc
        private LoginException(String error){
            this.error = error;
        }

        //TODO: make javadoc
        public String getError() {
            return error;
        }
    }

    private static Hashtable<String, String> getLoginHashTable() {
        return DataBase.getLoginHash();
    }
}
