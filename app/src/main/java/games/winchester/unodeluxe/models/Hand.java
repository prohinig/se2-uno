package games.winchester.unodeluxe.models;

import java.util.ArrayList;
import java.util.List;

public class Hand extends CardCollection {
    public Hand(){
        this.cards = new ArrayList<>();
    }

    public List<Card> getCards() {
        return this.cards;
    }

    public void addCards(List<Card> cards){
        this.cards.addAll(cards);
    }

    public void removeCard(Card card){
        this.cards.remove(card);
    }
}
