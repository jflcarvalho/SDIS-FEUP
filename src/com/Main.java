package com;

import com.DBS.Message.Message;
import com.DBS.FileIO.M_File;

import static com.Utils.Constants.testFilePath;

public class Main {

    public static void main(String[] args) {
        System.out.println(Message.encode(Message.parse("PUTCHUNK 1 0 mlksjhgwdjbjdcksfjksdnxmnsjkcbhdgvsbm 0 1 \r\n\r\n,ljduwyqghw1ib2qjm   snkdlsa nsanckl s,a csalc sac san@ieohdubwksckqn")));

        M_File file = new M_File(testFilePath);
        if(file.getFile() == null)
            return;
        System.out.println(file.toString());
    }
}
