package games.winchester.unodeluxe;

/**
 * Created by christianprohinig on 10.04.18.
 */

public class Card {
    private CardColor color;
    private CardSymbol symbol;

    public Card(CardColor color, CardSymbol symbol) {
        this.color = color;
        this.symbol = symbol;
    }

    public CardColor getColor() {
        return this.color;
    }

    public CardSymbol getSymbol() {
        return this.symbol;
    }
}
