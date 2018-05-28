package peers;

import network.Network;
import peers.Protocol.APIMessage;
import peers.Protocol.Message;
import peers.Protocol.MessageFactory;
import user.User;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;

import static peers.Protocol.Message.MessageType.*;
import static utils.Utils.exceptionPrint;

public class Worker extends ChordNode implements WorkerPeer{
    private Runtime runtime;
    private Node _database;

    public Worker(int node_Port, Node database) {
        super(node_Port);
        _database = database;
        runtime = Runtime.getRuntime();
    }

    private void printLines(String name, InputStream ins) throws Exception {
        String line;
        BufferedReader in = new BufferedReader(
                new InputStreamReader(ins));
        while ((line = in.readLine()) != null) {
            System.out.println(name + " " + line);
        }
    }

    public void submit(Task task) {
        Message msg = MessageFactory.getMessage(SAVE_TASK, new Serializable[]{task});
        Network.sendRequest(_database, msg, false);

        Node lookup_Node = new Node(task.getTask_ID(), null);
        if(Integer.compareUnsigned(getPredecessor().difference(lookup_Node), getPredecessor().difference(getNode())) < 0 ){
            execute(task);
        } else {
            Node n = findSuccessor(lookup_Node);
            msg = MessageFactory.getMessage(EXECUTE, new Serializable[]{task});
            Network.sendRequest(n, msg, false);
        }
    }

    @Override
    public boolean execute(Task task){
        Runnable runnable = () -> {
            try {
                String str;
                Process process = runtime.exec("java -cp Processes " + task.getCommand());
                printLines(task.getCommand() + " stdout:", process.getInputStream());
                printLines(task.getCommand() + " stderr:", process.getErrorStream());
                process.waitFor();
                task.setExitValue(process.exitValue());
                System.out.println(task.getCommand() + " exitValue() " + process.exitValue());
                Message msg = MessageFactory.getMessage(SAVE_TASK, new Serializable[]{task});
                Network.sendRequest(_database, msg, false);
                deletePendingTask(task);
            } catch (Exception e) {
                exceptionPrint(e, "[ERROR] executing task");
            }
        };
        runnable_Queue.put(task.getTask_ID(), runnable);
        threadPool.execute(runnable);
        return true;
    }

    public void deleteTask(Task task) {
        Node lookup_Node = new Node(task.getTask_ID(), null);
        if(Integer.compareUnsigned(getPredecessor().difference(lookup_Node), getPredecessor().difference(getNode())) < 0 ){
            deletePendingTask(task);
        } else {
            Node n = findSuccessor(lookup_Node);
            Message msg = MessageFactory.getMessage(DELETE_TASK, new Serializable[]{task});
            Network.sendRequest(n, msg, false);
        }
    }

    @Override
    public void deletePendingTask(Task task) {
        Runnable runnable = runnable_Queue.remove(task.getTask_ID());
        if(runnable != null)
            threadPool.remove(runnable);
    }

}
