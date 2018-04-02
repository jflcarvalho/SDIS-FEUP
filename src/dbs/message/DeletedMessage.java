package dbs.message;

import dbs.utils.Constants;

public class DeletedMessage extends RemovedMessage{
    DeletedMessage(double version, String sender_ID, String file_ID, int chunk_NO) {
        super(version, sender_ID, file_ID, chunk_NO);
        messageType = Constants.MessageType.DELETED;
    }
}
