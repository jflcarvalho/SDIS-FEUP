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

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;

import peers.Node;
import user.User;
import peers.Protocol.*;
import network.*;

import static utils.Utils.exceptionPrint;

public class Server{

    SSLServerSocket serverSocket;
    int port;
    Node node;

    public static void main(String[] args){
        if(args.length != 2){
            System.err.println("Wrong number of arguments!\nTry: Java Server <server_port> <ip_address:port>");
            return;
        }

        Server server = new Server(args);
        server.read();
    
    }

    public Server(String[] args){
        this.port = Integer.parseInt(args[0]);
        setNodeChord(args[1]);

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

    public void setNodeChord(String address){
        this.node = new Node(address);
    }

    public void read(){
        try{

            while(true){
                SSLSocket request = (SSLSocket) this.serverSocket.accept();
                PrintWriter out = new PrintWriter(request.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(request.getInputStream()));
                
                String msg = in.readLine();
                
                System.out.println("MSG: " + msg); //TODO erase this
                String[] splitMsg = msg.split("\\s");

                switch(splitMsg[0]){
                    case "MENU_TITLE":
                        out.println(Constants.MSG_OK + Constants.MENU_TITLE);
                    case "MENU_AUTH":
                        out.println(Constants.MSG_MULTIPLE_CHOICES + "AUTH 0 2\n" + Constants.MENU_AUTH); 
                        break;
                    case "LOGIN":
                        User user = new User(splitMsg[1], splitMsg[2]);

                        APIMessage apiMsg = (APIMessage) MessageFactory.getMessage(Message.MessageType.LOGIN, new Serializable[]{user});
                        apiMsg = (APIMessage) Network.sendRequest(this.node, apiMsg, true);
                        System.out.println(apiMsg.getReplyValue());

                        //TODO reply to client if correct or not
                    //TODO other msgs

                }
                
                
                out.close();
                in.close();
            }
        } catch(IOException e) {
            exceptionPrint(e, "[ERROR] reading...");
        }
    }
}