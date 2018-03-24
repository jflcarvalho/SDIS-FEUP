package DBS.Utils;

public class Constants {
    public enum MessageType {PUTCHUNK, STORED}
    public static final String CRLF = "\r\n";
    public static final String CRLF_D = "\r\n\r\n";
    public static final String SPACE = " ";
    public static final String testFilePath = "testFiles/testTXT.txt";

    public static final int CHUNKSIZE = 64000;
    public static final int PACKETLENGHT = 65024;
}
