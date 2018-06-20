package games.winchester.unodeluxe.messages;

import at.laubi.network.messages.Message;
import games.winchester.unodeluxe.models.Deck;

public class Shuffle implements Message {
    private Deck deck;

    public Shuffle(Deck deck) {
        this.deck = deck;
    }

    public Deck getDeck() {
        return this.deck;
    }
}