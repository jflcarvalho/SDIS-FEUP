package peers;

import user.User;

import java.io.Serializable;
import java.util.Date;

public class Task implements Serializable {
    private int task_ID;
    private String command;
    private int exitValue;
    private User _userOwner;

    /**
     * @param command String with main class and his command line arguments
     * @param own User how ownes the task
     */
    public Task(String command, User own) {
        this.command = command;
        this._userOwner = own;
        task_ID = (command + (new Date()).getTime()).hashCode();
    }

    public String getCommand() {
        return command;
    }

    public int getExitValue() {
        return exitValue;
    }

    public void setExitValue(int exitValue) {
        this.exitValue = exitValue;
    }

    public User getOwner() {
        return _userOwner;
    }

    public int getTask_ID() {
        return task_ID;
    }
}
