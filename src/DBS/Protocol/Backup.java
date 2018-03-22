package DBS.Protocol;

import DBS.Chunk;
import DBS.FileIO.M_File;
import DBS.Peer.Peer;

import java.io.File;

import static DBS.FileIO.FileManager.createFile;
import static DBS.FileIO.FileManager.writeFile;

public class Backup implements Protocol {

    private String file_path;
    private int replication_degree;
    private M_File mFile;
    private Peer peer;

    public Backup(String file_path, int replication_degree) {
        this.file_path = file_path;
        this.replication_degree = replication_degree;
        peer = new Peer();
    }

    public void run() {
        System.out.println("\n----------------- BACKUP --------------------\n");
        System.out.println("Backing up " + file_path);
        System.out.println("Replication degree: " + replication_degree + "\n");
        mFile = new M_File(file_path);
        System.out.println(mFile.toString());
        for(int i = 0; i < mFile.getChunks().length; i++){
            storeChunk(mFile.getChunks()[i]);
        }
        System.out.println("\n------------------ END ---------------------\n");
    }

    public void sendChunk(Chunk chunk) {

    }

    public void storeChunk(Chunk chunk) {
        createFile(chunk, peer);
        writeFile(chunk, peer);
        String file_path = "backup/" + peer.getPeerID() + "/" + chunk.getFileID() + "/" + chunk.getChunkID();
        long file_Size = (new File(file_path)).length();
        peer.addChunk(chunk, file_Size);
    }

    public Chunk readChunk(){
        return null;
    }
}
