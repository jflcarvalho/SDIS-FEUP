import utils.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.HashSet;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;

import peers.Node;
import peers.Task;
import user.User;
import peers.Protocol.*;
import network.*;

import static utils.Utils.exceptionPrint;

public class Server{

    SSLServerSocket serverSocket;
    int port;
    Node databaseNode, workerNode;

    public static void main(String[] args){
        if(args.length != 3){
            System.err.println("Wrong number of arguments!\nTry: java Server <server_port> <db_ip_address:db_port> <worker_ip_address:worker_port>");
            return;
        }

        Server server = new Server(args);
        server.read();
    
    }

    public Server(String[] args){
        this.port = Integer.parseInt(args[0]);
        setNodeDatabase(args[1]);
        setNodeWorker(args[2]);

        try{

            KeyStore clientKS = KeyStore.getInstance("JKS");
            clientKS.load(new FileInputStream("../client.public"), "public".toCharArray());
            KeyStore serverKS = KeyStore.getInstance("JKS");
            serverKS.load(new FileInputStream("../server.private"), "serverpw".toCharArray());
            
            TrustManagerFactory trustMF = TrustManagerFactory.getInstance("SunX509");
            trustMF.init(clientKS);
            KeyManagerFactory keyMF = KeyManagerFactory.getInstance("SunX509");
            keyMF.init(serverKS, "serverpw".toCharArray());
            
            SSLContext sslContext = SSLContext.getInstance("SSL");
            
            SecureRandom secRand = new SecureRandom();
            secRand.nextInt();
            sslContext.init(keyMF.getKeyManagers(), trustMF.getTrustManagers(), secRand);

            this.serverSocket = (SSLServerSocket) sslContext.getServerSocketFactory().createServerSocket(this.port);

            serverSocket.setNeedClientAuth(true);
        
        } catch(KeyStoreException | NoSuchAlgorithmException | CertificateException | UnrecoverableKeyException | KeyManagementException | IOException e){
            exceptionPrint(e, "[ERROR] internal server error");

        }
    }

    public void setNodeDatabase(String address){
        this.databaseNode = new Node(address);
    }

    public void setNodeWorker(String address){
        this.workerNode = new Node(address);
    }

    public void read(){ //TODO make this in threads
        try{

            while(true){
                SSLSocket request = (SSLSocket) this.serverSocket.accept();
                PrintWriter out = new PrintWriter(request.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(request.getInputStream()));
                
                String msg = in.readLine();
                
                System.out.println("MSG: " + msg); //TODO erase this
                String[] splitMsg = msg.split("\\s");

                String ans;

                switch(splitMsg[0]){
                    case "MENU_TITLE":
                        out.println(Constants.MSG_OK + Constants.MENU_TITLE);
                        break;

                    case "MENU_AUTH":
                        out.println(Constants.MSG_MULTIPLE_CHOICES + "AUTH 0 2\n" + Constants.MENU_AUTH); 
                        break;

                    case "MENU_USER":
                        out.println(Constants.MSG_MULTIPLE_CHOICES + "MENU_USER 0 3\n" + Constants.MENU_USER_AREA);
                        break;

                    case "LOGIN":
                        User user = new User(splitMsg[1], splitMsg[2]);

                        APIMessage apiMsg = (APIMessage) MessageFactory.getMessage(Message.MessageType.LOGIN, new Serializable[]{user});
                        apiMsg = (APIMessage) Network.sendRequest(this.databaseNode, apiMsg, true);
                        Boolean responseLogin = apiMsg.getReplyValue();
                        System.out.println(responseLogin); //TODO erase this
                        if(responseLogin)
                            out.println(Constants.MSG_OK);
                        else
                            out.println(Constants.MSG_CLIENT_ERROR);
                        break;

                    case "REGISTER":
                        User regUser = new User(splitMsg[1], splitMsg[2]);
                        APIMessage regMsg = (APIMessage) MessageFactory.getMessage(Message.MessageType.REGISTER, new Serializable[]{regUser});
                        regMsg = (APIMessage) Network.sendRequest(this.databaseNode, regMsg, true);
                        Boolean responseRegister = regMsg.getReplyValue();
                        System.out.println(responseRegister); //TODO erase this
                        if(responseRegister)
                            out.println(Constants.MSG_OK);
                        else
                            out.println(Constants.MSG_CLIENT_ERROR);
                        break;
                        
                    case "TASK":
                        User taskUser = new User(splitMsg[1], splitMsg[2]);
                        Task task = new Task("Process", taskUser); //TODO change to be the one received by the client
                        WorkerMessage taskMsg = (WorkerMessage) MessageFactory.getMessage(Message.MessageType.SUBMIT, new Serializable[]{task});
                        taskMsg = (WorkerMessage) Network.sendRequest(this.workerNode, taskMsg, false);
                        out.println(Constants.MSG_OK);
                        break;

                    case "LIST_TASKS":
                        User listUser = new User(splitMsg[1], splitMsg[2]);
                        WorkerMessage listMsg = (WorkerMessage) MessageFactory.getMessage(Message.MessageType.GET_TASKS, new Serializable[]{listUser});
                        listMsg = (WorkerMessage) Network.sendRequest(this.workerNode, listMsg, false);
                        HashSet<Task> tasks = (HashSet<Task>) listMsg.getArg();
                        
                        ans = "";
                        for(Task t: tasks){
                            ans += " " + t.getTask_ID();
                        }
                        
                        ans += "\n";
                        System.out.println(ans);
                        out.println(Constants.MSG_OK + ans);
                        
                        //TODO get all tasks and return it
                        break;

                    case "DELETE":
                        User deleteUser = new User(splitMsg[1], splitMsg[2]);
                        WorkerMessage deleteListMsg = (WorkerMessage) MessageFactory.getMessage(Message.MessageType.GET_TASKS, new Serializable[]{deleteUser});
                        deleteListMsg = (WorkerMessage) Network.sendRequest(this.workerNode, deleteListMsg, false);

                        Task deleteTask = null;
                        HashSet<Task> tasksSet = (HashSet<Task>) deleteListMsg.getArg();

                        for(Task t: tasksSet){
                            if(t.getTask_ID() == Integer.parseInt(splitMsg[3]))
                                deleteTask = t;
                        }
                        
                        WorkerMessage deleteMsg = (WorkerMessage) MessageFactory.getMessage(Message.MessageType.DELETE_TASK, new Serializable[]{deleteTask});
                        deleteMsg = (WorkerMessage) Network.sendRequest(this.workerNode, deleteMsg, false);

                        out.println(Constants.MSG_OK);
                        //TODO Delete task
                        break;
                    
                    case "CONSULT":
                        User consultUser = new User(splitMsg[1], splitMsg[2]);
                        WorkerMessage consultMsg = (WorkerMessage) MessageFactory.getMessage(Message.MessageType.GET_TASKS, new Serializable[]{consultUser});
                        consultMsg = (WorkerMessage) Network.sendRequest(this.workerNode, consultMsg, true);
                        HashSet<Task> consultTasks = (HashSet<Task>) consultMsg.getArg();
                        System.out.println("AAAA");

                        String ansConsult= "";
                        for(Task t: consultTasks){
                            ansConsult += ((t.getExitValue() == null ) ? " 1 " : " 0 ") + t.getTask_ID() + "\n";
                        }
                        System.out.println(ansConsult);

                        out.println(Constants.MSG_OK + ansConsult);
                        //TODO Return tasks and if finished or not
                        break;
                    
                    default:
                        //TODO Do we do something or simply ignore it?
                        break;
                    }
                    
                out.close();
                in.close();
            }
        } catch(IOException e) {
            exceptionPrint(e, "[ERROR] reading...");
        }
    }
}