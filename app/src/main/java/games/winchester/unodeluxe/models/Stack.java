package games.winchester.unodeluxe.models;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by christianprohinig on 10.04.18.
 */

public class Stack implements Serializable {

    private List<Card> cards;
    private Card topCard;

    Stack() {
        cards = new LinkedList<>();
    }

    /**
     * Add card to top
     * @param card The new top card
     */
    public void playCard(Card card) {
        cards.add(card);
        topCard = card;
    }

    /**
     * Get the current top card
     * @return The current top card
     */
    public Card getTopCard() {
        return topCard;
    }

    /**
     * to be called when shuffling stack back into deck
     * removes all but the top card from stack
     *
     * @return All cards without the top card
     */
    public List<Card> getCards() {
        cards.remove(topCard);

        List<Card> stackCards = new LinkedList<>(cards);

        cards.clear();
        cards.add(topCard);

        return stackCards;
    }
}
