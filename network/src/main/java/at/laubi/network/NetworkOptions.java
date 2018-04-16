package at.laubi.network;

import at.laubi.network.messages.Message;
import at.laubi.network.session.Session;

public class NetworkOptions {
    public int port = 10001;
    public FallbackCallbacks callbacks = null;
    public int timeout = 0;
    public boolean reuseAddress = true;

    public interface FallbackCallbacks {
        default void onException(Exception e, Session s) {}
        default void onMessageReceived(Message message) {}
    }
}
