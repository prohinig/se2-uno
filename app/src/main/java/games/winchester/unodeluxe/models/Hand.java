package games.winchester.unodeluxe.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Hand implements Serializable {
    private final List<Card> cards = new ArrayList<>();

    public List<Card> getCards() {
        return cards;
    }

    public void addCards(List<Card> additionalCards){
        cards.addAll(additionalCards);
    }

    public void removeCard(Card card){
        cards.remove(card);
    }

    public int cardsLeft(){
        return cards.size();
    }
}
