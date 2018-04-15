package games.winchester.unodeluxe.network;

import games.winchester.unodeluxe.messages.Message;

import java.io.IOException;

public interface Session {

    enum SocketState {
        Closed, Open
    }

    void flush() throws IOException;
    void write(Message message) throws IOException;

    long getSessionId();

    void close() throws IOException;
    void terminate() throws IOException;
    SocketState getState();
}
