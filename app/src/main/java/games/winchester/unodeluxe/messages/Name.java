package games.winchester.unodeluxe.messages;

import java.util.ArrayList;

import at.laubi.network.messages.Message;
import games.winchester.unodeluxe.enums.CardColor;
import games.winchester.unodeluxe.models.Deck;
import games.winchester.unodeluxe.models.Game;
import games.winchester.unodeluxe.models.Player;
import games.winchester.unodeluxe.models.Stack;

public class Name implements Message {
    //Setup object is sent from master to all connected devices when game starts
    public String name;

    public Name(String name) {
        this.name = name;
    }

}
