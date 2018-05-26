import java.net.InetAddress;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.SSLSocket;
import java.net.SocketException;
import java.io.*;

public class SSLClient {

    InetAddress address;
    int port;
    SSLSocket socket;

    public static void main(String[] args) throws IOException{
        //TODO number of args

        SSLClient sslClient = new SSLClient(args);
        sslClient.sendRequest();
    }

    public SSLClient(String[] args) throws IOException{
        this.address = InetAddress.getByName(args[0]);
        this.port = Integer.parseInt(args[1]);

        String[] cipherSuites = new String[args.length - 2];
        for(int i = 2; i < args.length; i++)
            cipherSuites[i - 2] = args[i];
        
        this.socket = (SSLSocket) SSLSocketFactory.getDefault().createSocket(this.address, this.port);

        if(cipherSuites.length > 0)
            this.socket.setEnabledCipherSuites(cipherSuites);
        else 
            this.socket.setEnabledCipherSuites(((SSLServerSocketFactory)SSLServerSocketFactory.getDefault()).getDefaultCipherSuites());
        
    }

    public void sendRequest() throws IOException{
        PrintWriter out = new PrintWriter(this.socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

        String request = stdIn.readLine();
        out.println(request);

        String answer = in.readLine();
        System.out.println("Answer: " + answer);

        in.close();
        stdIn.close();
        out.close();
    }
}