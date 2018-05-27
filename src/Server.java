import utils.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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

import static utils.Utils.exceptionPrint;

public class Server{

    SSLServerSocket serverSocket;
    int port;

    public static void main(String[] args){
        if(args.length != 1){
            System.err.println("Wrong number of arguments!\nTry: Java Server <server_port>");
            return;
        }

        Server server = new Server(args);
        server.read();
    
    }

    public Server(String[] args){
        this.port = Integer.parseInt(args[0]);
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

    public void read(){
        try{

            while(true){
                SSLSocket request = (SSLSocket) this.serverSocket.accept();
                PrintWriter out = new PrintWriter(request.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(request.getInputStream()));
                
                String msg = in.readLine();
                System.out.println("MSG: " + msg);
                
                if(msg.equals("GET LOGIN")){
                    out.println(Constants.MENU_TITLE); 
                }
                
                
                //out.close();
                //in.close();
            }
        } catch(IOException e) {
            exceptionPrint(e, "[ERROR] reading...");
        }
    }
}