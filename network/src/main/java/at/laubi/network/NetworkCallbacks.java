package at.laubi.network;

import at.laubi.network.messages.Message;
import at.laubi.network.session.Session;

public interface NetworkCallbacks {
    default void onExceptionFallback(Exception e, Session s) {}
    default void onReceivedMessage(Message message, Session s) {}
}
