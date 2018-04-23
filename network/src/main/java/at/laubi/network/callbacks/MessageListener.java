package at.laubi.network.callbacks;

import at.laubi.network.messages.Message;
import at.laubi.network.session.Session;

@FunctionalInterface
public interface MessageListener {
    void onMessageReceived(Message msg, Session session);
}
