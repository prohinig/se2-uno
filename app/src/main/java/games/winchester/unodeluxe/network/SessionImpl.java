package games.winchester.unodeluxe.network;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

import games.winchester.unodeluxe.messages.ConnectionEndMessage;
import games.winchester.unodeluxe.messages.Message;

import static games.winchester.unodeluxe.network.Session.SocketState.Closed;
import static games.winchester.unodeluxe.network.Session.SocketState.Open;

public class SessionImpl implements Session {

    private final static SessionManager manager = SessionManager.getInstance();

    private final Socket socket;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;
    private final Thread thread;

    private static long nextSessionId = 0L;
    private static final Object sessionIdLock = new Object();
    private final long sessionId;


    private SocketState state;

    private SessionImpl(Socket socket) throws IOException {
        this.socket = socket;
        this.socket.setSoTimeout(0);
        this.socket.setReuseAddress(true);

        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());

        synchronized (sessionIdLock){
            this.sessionId = nextSessionId;
            nextSessionId++;
        }

        this.state = Open;

        this.thread = new Thread(new Runnable() {
            @Override
            public void run() {
                readStream();
            }
        });
        this.thread.setName(getClass().getName() + " - " + this.sessionId);
        this.thread.start();

    }

    @Override
    public void write(Message message) throws IOException {
        if(this.state == Open) {
            synchronized (out) {
                out.writeObject(message);
            }
        }
    }

    @Override
    public void flush() throws IOException {
        synchronized (out) {
            this.out.flush();
        }
    }

    @Override
    public long getSessionId() {
        return this.sessionId;
    }

    @Override
    public void close() throws IOException {
        this.write(new ConnectionEndMessage());
        this.flush();
    }

    @Override
    public void terminate() throws IOException {
        this.state = Closed;
        socket.close();
    }

    @Override
    public SocketState getState() {
        return this.state;
    }

    private void readStream(){
        try{
            while(!Thread.interrupted() && this.state == Open) {
                try {
                    Message o = (Message) in.readObject();
                    SessionManager.getInstance().emitNewMessage(this, o);

                    if (o instanceof ConnectionEndMessage) {
                        this.handleGracefullyStop();
                    }
                } catch(EOFException e){
                    //Socket on the other side closed
                    this.state = Closed;
                    manager.emitClosed(this);
                    socket.close();
                } catch(SocketException e) {
                    // Socket closed on this side
                    synchronized (socket){
                        if(this.state != Closed){
                            this.state = Closed;
                        }
                        manager.emitClosed(this);
                    }
                } catch (Exception e) {
                    handleReadException(e); //Unknown exception
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void handleGracefullyStop() throws IOException{
        this.state = Closed;
        this.close();
        SessionManager.getInstance().emitClosed(this);

        synchronized (out){
            socket.close();
        }
    }

    private void handleReadException(Exception e){
        e.printStackTrace();
    }

    public static Session open(String host, int port) throws IOException {
        Socket socket = new Socket(host, port);

        configureSocket(socket);

        return new SessionImpl(socket);
    }

    @SuppressWarnings("WeakerAccess")
    public static Session from(Socket socket) throws IOException {
        configureSocket(socket);

        return new SessionImpl(socket);
    }

    private static void configureSocket(Socket socket) throws SocketException {
        socket.setReuseAddress(true);
        socket.setSoTimeout(0);
    }
}
