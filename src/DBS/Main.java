package com.DBS;

import com.DBS.FileIO.M_File;
import com.DBS.Message.Message;

import java.net.SocketException;

import static com.DBS.Utils.Constants.testFilePath;

public class Main {

    public static void main(String[] args) throws SocketException {
        System.out.println(Message.encode(Message.parse("PUTCHUNK 1 0 mlksjhgwdjbjdcksfjksdnxmnsjkcbhdgvsbm 0 1 \r\n\r\n,ljduwyqghw1ib2qjm   snkdlsa nsanckl s,a csalc sac san@ieohdubwksckqn")));

        M_File file = new M_File(testFilePath);
        if(file.getFile() == null)
            return;
        System.out.println(file.toString());
    }
}
