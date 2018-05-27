package user;

import java.io.Serializable;

public class User implements Serializable {
    private final String username;
    private final String password;
    private final int lookup;

    /**
     * user constructor create user object and add to database
     *
     * @param username of the user to create a new user
     * @param password Password hashed to create a user
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.lookup = username.hashCode();
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

    public int getLookup() {
        return lookup;
    }

    //TODO: make javadoc
    public static class LoginException extends Throwable {
        //TODO: make javadoc
        private String error;

        //TODO: make javadoc
        public LoginException(String error){
            this.error = error;
        }

        //TODO: make javadoc
        public String getError() {
            return error;
        }
    }
}
