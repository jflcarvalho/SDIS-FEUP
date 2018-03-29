package dbs.network;

import dbs.message.Message;

import java.io.IOException;

import static dbs.utils.Constants.MessageType.PUTCHUNK;

public class MCB_Channel extends M_Channel {
    /**
     * Class that connects and listens to a multicast
     *
     * @param address multicast address
     * @param port    multicast port
     * @throws IOException
     */
    public MCB_Channel(String address, int port){
        super(address, port);
    }

    @Override
    protected void handleRequest(String string_message){
        Message message = Message.parse(string_message);
        if(message.getMessageType() == PUTCHUNK){

        }
    }
}
