package games.winchester.unodeluxe.models;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Objects;

import games.winchester.unodeluxe.enums.Action;
import games.winchester.unodeluxe.enums.CardColor;
import games.winchester.unodeluxe.enums.CardSymbol;

/**
 * Created by christianprohinig on 10.04.18.
 */

public class Card implements Serializable, Comparable<Card> {
    private final CardColor color;
    private final CardSymbol symbol;

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

    public Action getRequiredAction() {
        return symbol.getAction();
    }

    public boolean isActionCard() {
        return symbol.isActionSymbol();
    }

    @Override
    public int compareTo(@NonNull Card o) {
        if (this == o) return 0;

        int resultColor = color.compareTo(o.color);
        if (resultColor != 0) return resultColor;

        return symbol.compareTo(o.symbol);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return color == card.color &&
                symbol == card.symbol;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, symbol);
    }

    @Override
    public String toString() {
        return "Card{" +
                "color=" + color +
                ", symbol=" + symbol +
                '}';
    }
}
