package chat.network.utils;

import chat.network.rpcprotocol.ClientRpcReflectionWorker;
import chat.service.IService;

import java.net.Socket;

public class ChatRpcConcurrentServer extends AbsConcurrentServer {
    private IService chatServer;
    public ChatRpcConcurrentServer(int port, IService chatServer) {
        super(port);
        this.chatServer = chatServer;
        System.out.println("Chat- ChatRpcConcurrentServer");
    }

    @Override
    protected Thread createWorker(Socket client) {
        // ChatClientRpcWorker worker=new ChatClientRpcWorker(chatServer, client);
        ClientRpcReflectionWorker worker=new ClientRpcReflectionWorker(chatServer, client);

        Thread tw=new Thread(worker);
        return tw;
    }

    @Override
    public void stop(){
        System.out.println("Stopping services ...");
    }
}

