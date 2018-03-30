package dbs.utils;

public class Constants {
    public enum MessageType {PUTCHUNK, STORED}
    public static final String CRLF = "\r\n";
    public static final String CRLF_D = "\r\n\r\n";
    public static final String SPACE = " ";
    public static final String testFilePath = "testFiles/testPdf.pdf";

    public static final int CHUNK_SIZE = 64000;
    public static final int PACKET_LENGTH = 65536;

    public static final int NUMBER_OF_TRIES = 5;
    public static final int SLEEP_TIME = 1000;

    public static final int MC = 0;
    public static final int MCB = 1;
    public static final int MCR = 2;
}
