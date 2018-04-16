package at.laubi.network;

import at.laubi.network.session.ClientSession;
import at.laubi.network.session.HostSession;

class NetworkFactory {

    public static void createHost(final Network network, final CreationListener<HostSession> listener) {
        if(listener == null) throw new IllegalArgumentException("listener must not be null");

        network.addTask(new Runnable() {
            @Override
            public void run() {
                try {
                    HostSession session = HostSession.open(network);
                    listener.onSuccess(session);
                }catch (Exception e){
                    listener.onException(e);
                }
            }
        });
    }

    public static void createClient(final Network network, final String host, final CreationListener<ClientSession> listener){
        if(host == null) throw new IllegalArgumentException("Host must not be null");
        if(listener == null) throw new IllegalArgumentException("Listener must not be null");

        network.addTask(new Runnable() {
            @Override
            public void run() {
                try{
                    ClientSession session = ClientSession.open(host, network);
                    listener.onSuccess(session);
                }catch (Exception e) {
                    listener.onException(e);
                }
            }
        });
    }
}
