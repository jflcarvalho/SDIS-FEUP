package DBS;

import DBS.Protocol.Backup;

import java.net.SocketException;

import static DBS.Utils.Constants.testFilePath;

public class Main {

    public static void main(String[] args) throws SocketException {
        Backup backup = new Backup(testFilePath, 1);
        backup.run();
    }
}
