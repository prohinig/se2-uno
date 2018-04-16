package at.laubi.network;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import at.laubi.network.messages.Message;
import at.laubi.network.session.ClientSession;
import at.laubi.network.session.HostSession;
import at.laubi.network.session.Session;

public class Network {
    private final NetworkOptions options;
    private final ExecutorService executor = Executors.newFixedThreadPool(1);

    public Network(NetworkOptions options){
        this.options = options;
    }

    public void createHost(final CreationListener<HostSession> listener) {
        NetworkFactory.createHost(this, listener);
    }

    public void createClient(final String host, final CreationListener<ClientSession> listener){
        NetworkFactory.createClient(this, host, listener);
    }

    public void addTask(Runnable runnable){
        this.executor.submit(runnable);
    }

    public NetworkOptions getOptions() {
        return options;
    }

    public void callFallbackException(Exception e, Session s){
        if(options.callbacks != null) options.callbacks.onException(e, s);

    }

    public void broadcastMessageReceived(Message m, Session s){
        if(this.options.callbacks != null) options.callbacks.onMessageReceived(m);
    }
}
