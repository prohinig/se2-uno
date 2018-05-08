package games.winchester.unodeluxe.messages;

import at.laubi.network.messages.Message;

public class SimpleStringMessage implements Message {
    private String message;

    public SimpleStringMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "SimpleStringMessage{" +
                "message='" + message + '\'' +
                '}';
    }
}
