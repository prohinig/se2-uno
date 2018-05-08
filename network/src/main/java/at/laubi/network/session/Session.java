package at.laubi.network.session;

import at.laubi.network.callbacks.ExceptionListener;
import at.laubi.network.messages.Message;

public interface Session {
    void send(Message message);
    void send(Message message, ExceptionListener listener);
    void close();
    boolean isClosed();
}
