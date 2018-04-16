package games.winchester.unodeluxe;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by christianprohinig on 10.04.18.
 */

public class Deck {
    private ArrayList<Card> cards;

    public Deck() {
        this.initialize();
    }

    private void initialize() {
        this.cards = new ArrayList<Card>();
        // add all cards
        for (CardColor color : CardColor.values()) {
            switch (color) {
                case BLACK:
                    for (CardSymbol symbol : CardSymbol.values()) {
                        switch (symbol) {
                            case PLUSFOUR:
                            case WISH:
                                // there are 4 of each black ones
                                this.addCard(color, symbol, 4);
                            default:
                                break;
                        }
                    }
                    break;
                default:
                    for (CardSymbol symbol : CardSymbol.values()) {
                        switch (symbol) {
                            case PLUSFOUR:
                            case WISH:
                                break;
                            default:
                                // there are 2 of each coloured ones
                                this.addCard(color, symbol, 2);
                                break;
                        }
                    }
                    break;
            }
        }

        // shuffle the cards because they are sorted
        this.shuffle();
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

    public void addCards(ArrayList<Card> cs) {
        this.cards.addAll(cs);
    }

    // shuffle the cards
    public void shuffle() {
        Collections.shuffle(this.cards);
    }

    // deal function used for dealing but also to draw cards
    // returns ArrayList containing x cards from top of deck
    public ArrayList<Card> deal(int amount) {
        ArrayList<Card> dealtCards = new ArrayList<Card>();

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
