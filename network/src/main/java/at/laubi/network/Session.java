package at.laubi.network;

import at.laubi.network.messages.Message;

public interface Session {
    void send(Message message);
    void send(Message message, MessageSendListener listener);
    void close();
    boolean isClosed();
}
