package games.winchester.unodeluxe.messages;

import at.laubi.network.messages.Message;

public class Name implements Message {
    //Setup object is sent from master to all connected devices when game starts
    public String name;

    public Name(String name) {
        this.name = name;
    }

}
