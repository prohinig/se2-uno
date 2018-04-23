package at.laubi.network.session;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import at.laubi.network.Network;
import at.laubi.network.NetworkOptions;
import at.laubi.network.callbacks.ExceptionListener;
import at.laubi.network.messages.ConnectionEndMessage;
import at.laubi.network.messages.Message;

import static at.laubi.network.session.ConnectionState.Closed;
import static at.laubi.network.session.ConnectionState.Open;

public class ClientSession implements Session {
    private final ExecutorService sendService = Executors.newSingleThreadExecutor();
    private final Socket socket;
    private final Network network;

    private final ObjectOutputStream out;
    private final ObjectInputStream in;

    private final Thread retrieveThread = new Thread(this::retrieveLoop);

    private ConnectionState state = Open;

    private ClientSession(Socket socket, Network network) throws IOException {
        this.network = network;
        this.socket = socket;

        this.out = new ObjectOutputStream(this.socket.getOutputStream());
        this.in = new ObjectInputStream(this.socket.getInputStream());

        this.retrieveThread.start();
    }

    @Override
    public void send(Message message){
        this.send(message, null);
    }

    @Override
    public void send(Message message, ExceptionListener listener) {
        if(state == Open) {
            this.sendService.submit(() -> {
                try {
                    out.writeObject(message);
                } catch (Exception e) {
                    network.callException(listener, e, this);
                }
            });
        }else{
            network.callException(listener, new Exception("Client is closed"), this);
        }
    }

    @Override
    public void close() {
        this.send(new ConnectionEndMessage());
        state = Closed;
        this.retrieveThread.interrupt();

        new Thread(() -> {

            try{
                out.close();

                List<Runnable> pendingTasks = sendService.shutdownNow();
                for (Runnable task : pendingTasks) {
                    task.run();
                }

                socket.close();

            }catch(Exception e) {
                network.callException(null, e, this);
            }
        }).start();
    }

    @Override
    public boolean isClosed() {
        return socket.isClosed();
    }

    private void retrieveLoop(){
        while(!Thread.interrupted()) {
            this.receiveMessage();
        }
    }

    private void receiveMessage(){
        try {
            Message msg = (Message) in.readObject();

            if(msg instanceof ConnectionEndMessage) {
                handleConnectionShutdown(true);
            }else{
                network.broadcastMessageReceived(msg, this);
            }
        }catch (SocketException|EOFException e) {
            handleConnectionShutdown(false);

        }catch(Exception e) {
            this.network.callException(null, e, this);
        }
    }

    /**
     * Handle connection shutdown on client side, if the server shuts down.
     *
     * @param gracefully If true, an answer is send to the server socket
     */
    private void handleConnectionShutdown(boolean gracefully) {
        //First, append a ConnectionEndMessage to send queue, while still open
        if(gracefully) {
            this.send(new ConnectionEndMessage());
        }

        // Mark socket as closed, so no new data can be appended
        state = Closed;

        // Interrupt thread, so no new data is read
        this.retrieveThread.interrupt();

        // Execute all pending send tasks (The current task still executes)
        if(gracefully) {
            List<Runnable> pendingTasks = sendService.shutdownNow();
            for (Runnable pendingTask : pendingTasks) {
                pendingTask.run();
            }
        }

        // Close socket
        try {
            this.socket.close();
        }catch(IOException e){
            network.callException(null, e, this);
        }

        // Emit event, that connection has been closed
        this.network.emitConnectionClosed(this);
    }

    public static ClientSession open(String host, Network network) throws IOException {
        NetworkOptions options = network.getOptions();
        Socket socket = new Socket(host, options.port);
        socket.setSoTimeout(options.timeout);
        socket.setReuseAddress(options.reuseAddress);

        return new ClientSession(socket, network);
    }

    public static ClientSession from(Socket socket, Network network) throws IOException{
        NetworkOptions options = network.getOptions();
        socket.setSoTimeout(options.timeout);
        socket.setReuseAddress(options.reuseAddress);

        return new ClientSession(socket, network);
    }
}
