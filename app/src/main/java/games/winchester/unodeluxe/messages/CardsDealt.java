package games.winchester.unodeluxe.messages;

import java.util.List;

import at.laubi.network.messages.Message;
import games.winchester.unodeluxe.models.Card;
import games.winchester.unodeluxe.models.CardCollection;

public class CardsDealt extends CardCollection implements Message {

    public List<Card> cards;
    public int playerIndex;

    public CardsDealt(List<Card> cards, int playerIndex) {
        this.cards = cards;
        this.playerIndex = playerIndex;
    }
}
