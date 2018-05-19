package games.winchester.unodeluxe.messages;

import at.laubi.network.messages.Message;

public class Name implements Message {
    private String name;

    public Name(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
