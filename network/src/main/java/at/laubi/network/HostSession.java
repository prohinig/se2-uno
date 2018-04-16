package at.laubi.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.List;

import at.laubi.network.messages.ConnectionEndMessage;
import at.laubi.network.messages.Message;

public class HostSession implements Session {
    private final List<ClientSession> connectedClients = new LinkedList<>();

    private final ServerSocket serverSocket;
    private final Network network;

    private Thread acceptThread = new Thread(new Runnable() {
        @Override
        public void run() {
            acceptLoop();
        }
    });


    private HostSession(ServerSocket socket, Network network) {
        this.serverSocket = socket;
        this.network = network;

        this.acceptThread.start();
    }

    @Override
    public void send(Message message) {
        this.send(message, null);
    }

    @Override
    public void send(Message message, MessageSendListener listener) {
        for(ClientSession cur : this.connectedClients) {
            cur.send(message, listener);
        }
    }

    @Override
    public void close() {
        send(new ConnectionEndMessage());

        network.addTask(new Runnable() {
            @Override
            public void run() {
            try {
                serverSocket.close();
            }catch(Exception e){
                network.callFallbackException(e, HostSession.this);
            }
            }
        });
    }

    @Override
    public boolean isClosed() {
        return this.serverSocket.isClosed();
    }

    static HostSession open(Network network) throws IOException {
        Network.Options options = network.getOptions();
        ServerSocket socket = new ServerSocket(options.port);
        socket.setSoTimeout(options.timeout);
        socket.setReuseAddress(options.reuseAddress);

        return new HostSession(socket, network);
    }

    private void acceptLoop(){
        while(!Thread.interrupted()) {
            try {
                Socket client = serverSocket.accept();

                this.connectedClients.add(ClientSession.from(client, network));

            }catch(SocketException exception) {
                // Socket closed
                acceptThread.interrupt();
            }catch(Exception e){
                network.callFallbackException(e, this);
            }
        }
    }
}
