package at.laubi.network.session;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import at.laubi.network.Network;
import at.laubi.network.NetworkOptions;
import at.laubi.network.callbacks.ExceptionListener;
import at.laubi.network.messages.ConnectionEndMessage;
import at.laubi.network.messages.Message;

public class HostSession implements Session {
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final List<ClientSession> connectedClients = new LinkedList<>();

    private final ServerSocket serverSocket;
    private final Network network;

    private Thread acceptThread = new Thread(this::acceptLoop);

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
    public void send(Message message, ExceptionListener listener) {
        for(ClientSession cur : this.connectedClients) {
            cur.send(message, listener);
        }
    }

    @Override
    public void close() {
        send(new ConnectionEndMessage());

        executorService.submit(() -> {
            try {
                serverSocket.close();
            }catch(Exception e){
                network.callException(null, e, HostSession.this);
            }
        });
    }

    @Override
    public boolean isClosed() {
        return this.serverSocket.isClosed();
    }

    public static HostSession open(Network network) throws IOException {
        NetworkOptions options = network.getOptions();
        ServerSocket socket = new ServerSocket(options.port);
        socket.setSoTimeout(options.timeout);
        socket.setReuseAddress(options.reuseAddress);

        return new HostSession(socket, network);
    }

    private void acceptLoop(){
        while(!Thread.interrupted()) {
            try {
                Socket client = serverSocket.accept();

                ClientSession session = ClientSession.from(client, network);

                this.connectedClients.add(session);
                network.emitNewSessionConnected(session);

            }catch(SocketException exception) {
                // Socket closed
                acceptThread.interrupt();
            }catch(Exception e){
                network.callException(null, e, this);
            }
        }
    }

    public void removeClient(ClientSession session){
        this.connectedClients.remove(session);
    }
}
