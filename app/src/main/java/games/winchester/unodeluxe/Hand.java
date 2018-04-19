package games.winchester.unodeluxe;

import java.util.ArrayList;
import java.util.Observable;

public class Hand extends CardCollection {

    private ArrayList<Card> cards;

    public Hand(){
        this.cards = new ArrayList<Card>();
    }

    public ArrayList<Card> getCards() {
        return this.cards;
    }

    public void addCards(ArrayList<Card> cards){
        this.cards.addAll(cards);
    }

    public void removeCard(Card card){
        this.cards.remove(card);
    }

}
