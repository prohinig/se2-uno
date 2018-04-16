package at.laubi.network;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import at.laubi.network.messages.Message;
import at.laubi.network.session.ClientSession;
import at.laubi.network.session.HostSession;
import at.laubi.network.session.Session;

public class Network {

    public interface FallbackCallbacks {
        void onException(Exception e, Session s);
        void onMessageReceived(Message message);
    }

    public interface SessionCreationListener<T extends Session>{
        void onSuccess(T session);
        void onException(Exception e);
    }

    public static class Options{
        public int port = 10001;
        public FallbackCallbacks callbacks = null;
        public int timeout = 0;
        public boolean reuseAddress = true;
    }

    private Options options;
    private final ExecutorService executor = Executors.newFixedThreadPool(1);

    public Network(Options options){
        this.options = options;
    }

    public void createHost(final SessionCreationListener<HostSession> listener) {
        if(listener == null) throw new IllegalArgumentException("listener must not be null");

        addTask(new Runnable() {
            @Override
            public void run() {
                try {
                    HostSession session = HostSession.open(Network.this);
                    listener.onSuccess(session);
                }catch (Exception e){
                    listener.onException(e);
                }
            }
        });
    }

    public void createClient(final String host, final SessionCreationListener<ClientSession> listener){
        if(host == null) throw new IllegalArgumentException("Host must not be null");
        if(listener == null) throw new IllegalArgumentException("Listener must not be null");

        addTask(new Runnable() {
            @Override
            public void run() {
                try{
                    ClientSession session = ClientSession.open(host, Network.this);
                    listener.onSuccess(session);
                }catch (Exception e) {
                    listener.onException(e);
                }
            }
        });
    }

    public void addTask(Runnable runnable){
        this.executor.submit(runnable);
    }

    public Options getOptions() {
        return options;
    }

    public void callFallbackException(Exception e, Session s){
        if(options.callbacks != null) options.callbacks.onException(e, s);

    }

    public void broadcastMessageReceived(Message m, Session s){
        if(this.options.callbacks != null) options.callbacks.onMessageReceived(m);
    }
}
