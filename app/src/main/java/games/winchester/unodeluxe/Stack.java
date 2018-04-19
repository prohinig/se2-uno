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

    // returns true if successfully playd
    public boolean playCard(Card card) {
        if (this.canPlayCard(card)){
            this.cards.add(card);
            this.topCard = card;
            return true;
        }else {
            return false;
        }
    }

    public Card getTopCard(){
        return this.topCard;
    }

    public int getSize(){
        return this.cards.size();
    }

    // to be called when shuffling stack back into deck
    // removes all but the top card from stack
    public ArrayList<Card> getCards() {

        // we do not return the topcard as it has to remain on stack
        this.cards.remove(this.topCard);

        ArrayList<Card> stackCards = new ArrayList<>();
        stackCards.addAll(this.cards);

        this.cards.clear();
        this.cards.add(topCard);

        return stackCards;
    }

    // maybe put this functionality somewhere else
    // but it can always be moved easily afterwards
    public boolean canPlayCard(Card card) {
        // TODO implement rules by comparing top card with card to be layed
        return true;
    }
}
