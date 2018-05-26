import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.net.SocketException;
import java.io.*;

public class SSLServer{

    Integer port;
    SSLServerSocket s;

    public static void main(String[] args) {
        //TODO check number of args
        
        try{
            SSLServer sslServer = new SSLServer(args);
            sslServer.receiveRequest();

        } catch(IOException e){
            e.printStackTrace();
        }
            
        
    }


    public SSLServer(String[] args) throws IOException {
        String[] cipherSuites = new String[args.length - 1];
        this.port = Integer.parseInt(args[0]);

        for(int i = 1; i < args.length; i++)
            cipherSuites[i - 1] = args[i];
 
        SSLServerSocketFactory ssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();  
            
        try {  
            this.s = (SSLServerSocket) ssf.createServerSocket(this.port);  
        }  
        catch(IOException e) {  
            System.out.println("Server - Failed to create SSLServerSocket");  
            e.getMessage();  
            return;  
        } 
       
        this.s.setNeedClientAuth(true);
        if(cipherSuites.length > 0)
            this.s.setEnabledCipherSuites(cipherSuites);
        else
            this.s.setEnabledCipherSuites(ssf.getDefaultCipherSuites()); 

    }

    public void receiveRequest() throws IOException{
        while(true){
            SSLSocket request = (SSLSocket) this.s.accept();
            PrintWriter out = new PrintWriter(request.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(request.getInputStream()));

            String msg = in.readLine();
            System.out.println("MSG: " + msg);
            String a = "Received: " + msg;
            out.println(a); 

        }
    }


}

