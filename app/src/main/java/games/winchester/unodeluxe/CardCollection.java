package games.winchester.unodeluxe;

import java.util.ArrayList;

public abstract class CardCollection {

    protected ArrayList<Card> cards;

    public int getSize() {
        return this.cards.size();
    }

}
