package at.laubi.network;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import at.laubi.network.exceptions.NetworkException;
import at.laubi.network.messages.Message;
import at.laubi.network.session.ClientSession;
import at.laubi.network.session.HostSession;
import at.laubi.network.session.Session;

public class Network {
    private final ExecutorService executor = Executors.newFixedThreadPool(1);

    private final NetworkOptions options;
    private final NetworkCallbacks callbacks;
    private Session currentSession;

    public Network(NetworkOptions options, NetworkCallbacks callbacks){
        if(callbacks == null)
            throw new IllegalArgumentException("callbacks must be set");

        this.options = options == null ? new NetworkOptions() : options;
        this.callbacks = callbacks;
    }

    public void addTask(Runnable runnable){
        this.executor.submit(runnable);
    }

    public NetworkOptions getOptions() {
        return options;
    }

    public void callFallbackException(Exception e, Session s){
        callbacks.onExceptionFallback(e, s);
    }

    public void broadcastMessageReceived(Message m, Session s){
        callbacks.onReceivedMessage(m, s);
    }

    public void createHost(final CreationListener<HostSession> listener){
        if(listener == null)
            throw new IllegalArgumentException("listener must not be null");

        if(currentSession != null) {
            listener.onException(new NetworkException("This network is already initialized with a currentSession"));
        }

        this.addTask(() -> {
            try {
                HostSession session = HostSession.open(Network.this);
                Network.this.currentSession = session;
                listener.onSuccess(session);
            }catch (Exception e){
                listener.onException(e);
            }
        });
    }

    public void createClient(final String host, final CreationListener<ClientSession> listener) {
        if(host == null) throw new IllegalArgumentException("Host must not be null");
        if(listener == null) throw new IllegalArgumentException("Listener must not be null");

        if(currentSession != null){
            listener.onException(new NetworkException("This network is already initialized with a currentSession"));
        }

        this.addTask(() -> {
            try{
                ClientSession session = ClientSession.open(host, Network.this);
                Network.this.currentSession = session;
                listener.onSuccess(session);
            }catch (Exception e) {
                listener.onException(e);
            }
        });
    }

    public Session getCurrentSession(){
        return this.currentSession;
    }
}
