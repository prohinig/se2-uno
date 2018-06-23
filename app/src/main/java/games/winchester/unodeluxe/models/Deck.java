package games.winchester.unodeluxe.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import games.winchester.unodeluxe.enums.CardColor;
import games.winchester.unodeluxe.enums.CardSymbol;

/**
 * Created by christianprohinig on 10.04.18.
 */

public class Deck extends CardCollection {
    public Deck() { }


    public Deck(List<Card> cards) {
        this.cards = cards;
    }

    // add a card to the deck
    // private because should only be called from constructor
    private void addCard(CardColor color, CardSymbol symbol, int amount) {
        int added = 0;
        while (added < amount) {
            // make sure to have different instances of card even though its same symbol/color
            Card card = new Card(color, symbol);
            this.cards.add(card);
            added++;
        }
    }

    public void addCards(List<Card> cs) {
        this.cards.addAll(cs);
    }

    // shuffle the cards
    public void shuffle() {
        Collections.shuffle(this.cards);
    }

    // deal function used for dealing but also to draw cards
    // returns ArrayList containing x cards from top of deck
    public List<Card> deal(int amount) {
        List<Card> dealtCards = new ArrayList<>();

        int amountDealt = 0;

        while (amountDealt < amount) {
            //List.remove returns the removed elemet
            // we take cards from top of the deck until we have the wished amount
            dealtCards.add(this.cards.remove(0));
            amountDealt++;
        }

        return dealtCards;
    }

}
