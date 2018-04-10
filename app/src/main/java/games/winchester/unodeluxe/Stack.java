package games.winchester.unodeluxe;

import java.util.ArrayList;

/**
 * Created by christianprohinig on 10.04.18.
 */

public class Stack {

    private ArrayList<Card> cards;
    private Card topCard;

    public Stack() {
        this.cards = new ArrayList<Card>();
    }

    public void layCard(Card card){
        this.cards.add(card);
        this.topCard = card;
    }

    public Card getTopCard(){
        return this.topCard;
    }

    // to be called when shuffling stack back into deck
    // removes all but the top card from stack
    public ArrayList<Card> getCards() {

        // we do not return the topcard as it has to remain on stack
        this.cards.remove(this.topCard);

        ArrayList<Card> stackCards = new ArrayList<>();
        stackCards.addAll(this.cards);

        this.cards.clear();
        this.layCard(topCard);

        return stackCards;
    }
}
