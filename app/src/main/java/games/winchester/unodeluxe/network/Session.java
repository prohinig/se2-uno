package games.winchester.unodeluxe.network;

import games.winchester.unodeluxe.messages.Message;
import games.winchester.unodeluxe.network.tasks.MessageSendTask;

public interface Session {
    void send(Message message);
    void send(Message message, MessageSendTask.MessageSendListener listener);
    void close();
    boolean isClosed();
}
