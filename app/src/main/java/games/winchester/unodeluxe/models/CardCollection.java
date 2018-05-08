package games.winchester.unodeluxe.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import games.winchester.unodeluxe.models.Card;

public abstract class CardCollection implements Serializable {

    protected List<Card> cards;

    public int getSize() {
        return this.cards.size();
    }

}
