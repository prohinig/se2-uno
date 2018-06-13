package games.winchester.unodeluxe.messages;

import at.laubi.network.messages.Message;
import games.winchester.unodeluxe.models.Card;

public class Cheat implements Message {
    private String cheater;
    private Card dissapearedCard;

    public Cheat(String cheater, Card dissapearedCard) {
        this.cheater = cheater;
        this.dissapearedCard = dissapearedCard;
    }

    public String getCheater() {
        return cheater;
    }

    public Card getDissapearedCard() {
        return dissapearedCard;
    }
}
