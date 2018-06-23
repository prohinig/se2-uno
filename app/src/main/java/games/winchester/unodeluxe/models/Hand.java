package games.winchester.unodeluxe.models;

import java.util.ArrayList;
import java.util.List;

public class Hand extends CardCollection {
    public Hand(){
        cards = new ArrayList<>();
    }

    public List<Card> getCards() {
        return cards;
    }

    public void addCards(List<Card> additionalCards){
        cards.addAll(additionalCards);
    }

    public void removeCard(Card card){
        cards.remove(card);
    }
}
