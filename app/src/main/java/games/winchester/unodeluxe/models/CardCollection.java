package games.winchester.unodeluxe.models;

import java.util.ArrayList;

import games.winchester.unodeluxe.models.Card;

public abstract class CardCollection {

    protected ArrayList<Card> cards;

    public int getSize() {
        return this.cards.size();
    }

}
