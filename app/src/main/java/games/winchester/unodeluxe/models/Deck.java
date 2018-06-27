package games.winchester.unodeluxe.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by christianprohinig on 10.04.18.
 */

public class Deck implements Serializable {
    private final List<Card> cards;

    Deck(List<Card> deckCards) {
        cards = new ArrayList<>(deckCards);
        shuffle();
    }

    public void addCards(List<Card> cs) {
        cards.addAll(cs);
    }

    // shuffle the cards
    public void shuffle() {
        Collections.shuffle(cards);
    }

    // deal function used for dealing but also to draw cards
    // returns ArrayList containing x cards from top of deck
    public List<Card> deal(int amount) {
        List<Card> dealtCards = new ArrayList<>();

        int amountDealt = 0;

        while (amountDealt < amount) {
            //List.remove returns the removed elemet
            // we take cards from top of the deck until we have the wished amount
            dealtCards.add(cards.remove(0));
            amountDealt++;
        }

        return dealtCards;
    }

    public int cardsLeft() {
        return cards.size();
    }

}
