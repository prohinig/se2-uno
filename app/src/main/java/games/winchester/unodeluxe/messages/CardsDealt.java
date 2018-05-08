package games.winchester.unodeluxe.messages;

import java.util.ArrayList;

import at.laubi.network.messages.Message;
import games.winchester.unodeluxe.models.Card;
import games.winchester.unodeluxe.models.CardCollection;

public class CardsDealt extends CardCollection implements Message {

    public ArrayList<Card> cards;
    public int playerIndex;

    public CardsDealt(ArrayList<Card> cards, int playerIndex) {
        this.cards = cards;
        this.playerIndex = playerIndex;
    }
}
