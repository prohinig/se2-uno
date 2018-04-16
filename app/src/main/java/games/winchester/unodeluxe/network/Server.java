package games.winchester.unodeluxe.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketException;

public class Server {
    private Thread thread;
    private ServerSocket socket;

    public Server(int port) throws IOException {
        this.socket = new ServerSocket(port);

        this.thread = new Thread(new Runnable() {
            @Override
            public void run() {
                serve();
            }
        });
        this.thread.setDaemon(false);
        this.thread.setName(getClass().getName());
        this.thread.start();
    }

    private void serve(){
        while(!Thread.interrupted()){
            try {
                Session session = SessionImpl.from(socket.accept());
                SessionManager.getInstance().register(session);

            }catch(SocketException e){
                // Socket closed on this side
                // TODO: Do something
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void close() throws IOException {
        this.socket.close();
    }
}
