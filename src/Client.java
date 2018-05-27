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
        
        } catch(UnknownHostException e){
            e.printStackTrace();

        } catch(KeyStoreException e){
            e.printStackTrace();
        
        } catch(NoSuchAlgorithmException e){
            e.printStackTrace();
        
        } catch(CertificateException e){
            e.printStackTrace();
        
        } catch(UnrecoverableKeyException e){
            e.printStackTrace();
        
        } catch(KeyManagementException e){
            e.printStackTrace();
            
        } catch(IOException e){
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
                    this.out.println("LOGIN " + user.getUsername() + " " + user.getPassword());
                    System.out.println("LOGIN " + user.getUsername() + " " + user.getPassword());
                    waitAnsLogin();
                    break;

                default:
                    stdIn.close();
                    return -1;
            
            }

            stdIn.close();
                
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
                
                restartSocket();
                this.out.println("USER_MENU");
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
}