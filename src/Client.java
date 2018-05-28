import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import utils.*;
import user.User;
import ui.UI_Login;

public class Client {

    InetAddress address;
    int port;
    PrintWriter out;
    BufferedReader in;
    SSLSocket socket;
    SSLContext sslContext;
    User user;
    Boolean loginSuccess = false;

    public static void main(String[] args){
        if(args.length != 2){
            System.err.println("Wrong number of arguments!\nTry: Java Client <server_address> <server_port>");
            return;
        }

        Client client = new Client(args);
        client.start();

    }

    public Client(String[] args){
        
        try{
            this.address = InetAddress.getByName(args[0]);
            this.port = Integer.parseInt(args[1]);

            KeyStore serverKS = KeyStore.getInstance("JKS");
            serverKS.load(new FileInputStream("../server.public"), "public".toCharArray());
            KeyStore clientKS = KeyStore.getInstance("JKS");
            clientKS.load(new FileInputStream("../client.private"), "clientpw".toCharArray());
            
            TrustManagerFactory trustMF = TrustManagerFactory.getInstance("SunX509");
            trustMF.init(serverKS);
            KeyManagerFactory keyMF = KeyManagerFactory.getInstance("SunX509");
            keyMF.init(clientKS, "clientpw".toCharArray());
            
            this.sslContext = SSLContext.getInstance("SSL");
            
            SecureRandom secRand = new SecureRandom();
            secRand.nextInt();
            this.sslContext.init(keyMF.getKeyManagers(), trustMF.getTrustManagers(), secRand);

            initSocket();            
        
        } catch(KeyStoreException | NoSuchAlgorithmException | CertificateException | UnrecoverableKeyException | KeyManagementException | IOException e){
            e.printStackTrace();
        } 

    }

    public void initSocket(){
        try{

            this.socket = (SSLSocket) this.sslContext.getSocketFactory().createSocket(this.address, this.port);
            
            this.out = new PrintWriter(this.socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public void closeSocket(){
        try{

            this.socket.close();
            this.out.close();
            this.in.close();
        
        } catch(IOException e){
            e.printStackTrace();
        } 
    }

    public void restartSocket(){
        closeSocket();
        initSocket();

    }

    public int login(String[] msg){
        int min = Integer.parseInt(msg[1]);
        int max = Integer.parseInt(msg[2]);
        
        try{

            while(this.in.ready()){
                System.out.println(this.in.readLine());
            }
            
            Integer choice = Utils.inputIntBetween(min, max);
            Scanner stdIn = new Scanner(System.in);
            String username, password;
        
            switch(choice){
                case 1: 
                    System.out.println("\n\nLOGIN\n");
                    System.out.print("Username: ");
                    username = stdIn.nextLine();
                    
                    System.out.print("Password: ");
                    password = stdIn.nextLine();
                    
                    this.user = new User(username, Utils.getHex(Utils.hashString(password)));
                    restartSocket();
                    this.out.println("LOGIN " + user.getUsername() + " " + user.getPassword());
                    System.out.println("LOGIN " + user.getUsername() + " " + user.getPassword());
                    waitAnsLogin();
                    break;
                case 2:    
                    System.out.println("\n\nREGISTER\n");
                    System.out.print("Username: ");
                    username = stdIn.nextLine();
                    
                    System.out.print("Password: ");
                    password = stdIn.nextLine();
                    
                    this.user = new User(username, Utils.getHex(Utils.hashString(password)));
                    restartSocket();
                    this.out.println("REGISTER " + user.getUsername() + " " + user.getPassword());
                    System.out.println("REGISTER " + user.getUsername() + " " + user.getPassword());
                    waitAnsLogin();
                    break;

                default:
                    stdIn.close();
                    return -1;
            
            }

            
                
        } catch(IOException e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    public void waitAnsLogin(){

        try{

            String answer = this.in.readLine();
            System.out.println(answer);
            
            if(answer.equals("200")){
                while(this.in.ready()){
                    System.out.println(this.in.readLine());
                }
                
                this.loginSuccess = true;
                menu();

            } else if(answer.equals("400")) {
                
                this.loginSuccess = false;
                System.out.println("[ERROR] Login Incorrect");
            }
            
        } catch(IOException e){
            e.printStackTrace();
        } catch(NullPointerException e){
            System.err.println("[ERROR] Unexpected Error from Server");
            System.exit(-1);
        }
    }
    
    public void start(){
        try{
            
            String request = "MENU_TITLE";
            this.out.println(request);

            String answer = this.in.readLine();
            System.out.println(answer);
            
            if(answer.equals("200")){
                while(this.in.ready())
                    System.out.println(this.in.readLine());
                
            } else
                System.err.println("[ERROR] Unexpected message from server");
            

            while(!loginSuccess){

                restartSocket();
                request = "MENU_AUTH";
                this.out.println(request);
                
                answer = this.in.readLine();
                System.out.println(answer);
                
                if(answer.equals("300")){
                    String ans = this.in.readLine();
                    String[] divAnswer = ans.split("\\s");
                    
                    if(divAnswer[0].equals("AUTH")){
                        if(login(divAnswer) == -1)
                            return;
                        
                    }
                    
                } else
                    System.err.println("[ERROR] Unexpected message from server");
            }
            
            closeSocket();
        
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public void addTask(){
        try{

            System.out.println("\n\nADD TASK\n");
            Scanner stdIn = new Scanner(System.in);
            System.out.print("File path: ");
            String path = stdIn.nextLine();
            
            restartSocket();
            this.out.println("TASK " + this.user.getUsername() + " " + this.user.getPassword() + " " + path);
            
            String answer = this.in.readLine();
            System.out.println(answer);
            
            if(answer.equals("200")){
                while(this.in.ready()){
                    System.out.println("Successfull added task with ID " + this.in.readLine());
                }
            } else {
                System.err.println("[ERROR] Couldn't add task. Please try again");
            }
            
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public void consult(){
        try{

            System.out.println("\n\nCONSULT TASKS\n");
            
            restartSocket();
            this.out.println("CONSULT " + this.user.getUsername() + " " + this.user.getPassword());
            
            String answer = this.in.readLine();
            System.out.println(answer);
            
            if(answer.equals("200")){
                String print;
                while(this.in.ready()){
                    answer = this.in.readLine();
                    String[] splitAns = answer.split("\\s");
                    
                    //TODO check this block of code cuz I'm assuming the response comes in <NUM ID> where NUM is 0 or 1 if finished or not
                    if(splitAns[0].equals("0")){
                        System.out.println("Finished: " + splitAns[1]);
                    } else if(splitAns[0].equals("1")){
                        System.out.println("Working on it: " + splitAns[1]);
                    }
                }
            } else {
                System.err.println("[ERROR] Couldn't consult the tasks");
            }
        
        } catch(IOException e){
            e.printStackTrace();
        } 
    }

    public void delete(){
        try{

            System.out.println("\n\nDELETE TASKS\n");
            
            restartSocket();
            this.out.println("LIST_TASKS " + this.user.getUsername() + " " + this.user.getPassword());
            
            String answer = this.in.readLine();
            System.out.println(answer);
            
            if(answer.equals("200")){
                ArrayList<String> tasks = new ArrayList<String>();
                int num = 1;
                while(this.in.ready()){
                    answer = this.in.readLine();
                    System.out.println(num + ") " + answer);
                    tasks.add(answer);
                    num++;
                }
                System.out.println("\n0) Cancel");
                
                Integer choice = Utils.inputIntBetween(0, num - 1);
                if(choice != 0){
                    restartSocket();
                    this.out.println("DELETE " + this.user.getUsername() + " " + this.user.getPassword() + tasks.get(num - 2));
                    
                    String ans = this.in.readLine();
                    System.out.println(ans);
                    
                    if(ans.equals("200")){
                        while(this.in.ready()){
                            System.out.println("Successfull deleted task with ID " + this.in.readLine());
                        }
                    } else {
                        System.err.println("[ERROR] Couldn't delete task. Please try again");
                    }
                } else {
                    return;
                }
                
            } else {
                System.err.println("[ERROR] Couldn't get the tasks");
            }
        
        } catch(IOException e){
            e.printStackTrace();
        }

    }
        
    public void menu(){

        try{

            while(true){
                
                restartSocket();
                this.out.println("MENU_USER");
                
                String answer = this.in.readLine();
                System.out.println(answer);
                
                if(answer.equals("300")){
                    String ans = this.in.readLine();
                    String[] divAnswer = ans.split("\\s");
                    
                    if(divAnswer[0].equals("MENU_USER")){
                        int min = Integer.parseInt(divAnswer[1]);
                        int max = Integer.parseInt(divAnswer[2]);
                        
                        while(this.in.ready()){
                            System.out.println(this.in.readLine());
                        }
                        
                        Integer choice = Utils.inputIntBetween(min, max);
                        switch(choice){
                            case 1:
                                addTask();
                                break;
                            case 2:
                                delete();
                                break;
                            case 3:
                                consult();
                                break;
                            default:
                                return;
                        }
                        
                    }
                    
                } else
                System.err.println("[ERROR] Unexpected message from server");
                
            }
            
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}