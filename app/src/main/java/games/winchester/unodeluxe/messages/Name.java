package games.winchester.unodeluxe.messages;

import at.laubi.network.messages.Message;

public class Name implements Message {
    private String playerName;

    public Name(String name) {
        this.playerName = name;
    }

    public String getName() {
        return playerName;
    }
}
