package DBS;

import DBS.Message.Message;
import DBS.Protocol.Backup;

import java.net.SocketException;

import static DBS.Utils.Constants.testFilePath;

public class Main {

    public static void main(String[] args) throws SocketException {
        System.out.println(Message.encode(Message.parse("PUTCHUNK 1 0 mlksjhgwdjbjdcksfjksdnxmnsjkcbhdgvsbm 0 1 \r\n\r\n,ljduwyqghw1ib2qjm   snkdlsa nsanckl s,a csalc sac san@ieohdubwksckqn")));

        Backup backup = new Backup(testFilePath, 1);
        backup.run();
    }
}
