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

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class Client {

    InetAddress address;
    int port;
    PrintWriter out;
    BufferedReader in;
    SSLSocket socket;

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
            
            SSLContext sslContext = SSLContext.getInstance("SSL");
            
            SecureRandom secRand = new SecureRandom();
            secRand.nextInt();
            sslContext.init(keyMF.getKeyManagers(), trustMF.getTrustManagers(), secRand);

            this.socket = (SSLSocket) sslContext.getSocketFactory().createSocket(this.address, this.port);

            this.out = new PrintWriter(this.socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

        
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

    public void start(){
        try{
            
            String request = "GET LOGIN";
            this.out.println(request);
            
            
            /*
            while((answer = this.in.readLine()) != null)
            System.out.println(answer);
            */
            
            String answer = this.in.readLine();
            System.out.println(answer);
            while(this.in.ready()){
                System.out.println(this.in.readLine());
            }
            
            this.in.close();
            this.out.close();
            this.socket.close();
        
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}