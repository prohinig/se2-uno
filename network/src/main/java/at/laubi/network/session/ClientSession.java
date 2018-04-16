package at.laubi.network.session;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

import at.laubi.network.MessageSendListener;
import at.laubi.network.Network;
import at.laubi.network.NetworkOptions;
import at.laubi.network.messages.ConnectionEndMessage;
import at.laubi.network.messages.Message;

public class ClientSession implements Session {

    private final Socket socket;
    private final Network network;

    private final ObjectOutputStream out;
    private final ObjectInputStream in;

    private final Thread retrieveThread = new Thread(new Runnable() {
        @Override
        public void run() { retrieveLoop(); }
    });

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
    public void send(final Message message, final MessageSendListener listener) {
        this.network.addTask(new Runnable() {
            @Override
            public void run() {
                try{
                    out.writeObject(message);
                }catch(Exception e){
                    if(listener != null) listener.onException(e);
                    else network.callFallbackException(e, ClientSession.this);
                }
            }
        });
    }

    @Override
    public void close() {
        this.network.addTask(new Runnable() {
            @Override
            public void run() {
                try {
                    socket.close();
                }catch (Exception e) {
                    network.callFallbackException(e, ClientSession.this);
                }
            }
        });
    }

    @Override
    public boolean isClosed() {
        return socket.isClosed();
    }

    private void retrieveLoop(){
        while(!Thread.interrupted()) {
            try {
                Message msg = (Message) in.readObject();

                if(msg instanceof ConnectionEndMessage) {
                    retrieveThread.interrupt();
                }else{
                    network.broadcastMessageReceived(msg, this);
                }

            }catch (SocketException|EOFException e) {
                this.close();

            }catch(Exception e) {
                this.network.callFallbackException(e, this);
            }
        }
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
