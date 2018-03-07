package com.DBS;

import com.DBS.Message.Message;

public class Main {

    public static void main(String[] args) {
        System.out.println(Message.encode(Message.parse("PUTCHUNK 1 0 mlksjhgwdjbjdcksfjksdnxmnsjkcbhdgvsbm 0 1 \r\n\r\n,ljduwyqghw1ib2qjm   snkdlsa nsanckl s,a csalc sac san@ieohdubwksckqn")));
    }
}
