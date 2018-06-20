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
    private String graphic;

    public Card(CardColor color, CardSymbol symbol) {
        this.color = color;
        this.symbol = symbol;
        initGraphic(color, symbol);
    }

    public CardColor getColor() {
        return this.color;
    }

    public CardSymbol getSymbol() {
        return this.symbol;
    }

    public String getGraphic() {
        return this.graphic;
    }


    private void initGraphic(CardColor color, CardSymbol symbol) {
        String graphicName = "";
        switch (color) {
            case RED:
                graphicName += "r";
                break;
            case YELLOW:
                graphicName += "y";
                break;
            case GREEN:
                graphicName += "g";
                break;
            case BLUE:
                graphicName += "b";
                break;
            default:
                break;
        }

        switch (symbol) {
            case ZERO:
                graphicName += "0";
                break;
            case ONE:
                graphicName += "1";
                break;
            case TWO:
                graphicName += "2";
                break;
            case THREE:
                graphicName += "3";
                break;
            case FOUR:
                graphicName += "4";
                break;
            case FIVE:
                graphicName += "5";
                break;
            case SIX:
                graphicName += "6";
                break;
            case SEVEN:
                graphicName += "7";
                break;
            case EIGHT:
                graphicName += "8";
                break;
            case NINE:
                graphicName += "9";
                break;
            case PLUSTWO:
                graphicName += "d2";
                break;
            case SKIP:
                graphicName += "s";
                break;
            case REVERSE:
                graphicName += "r";
                break;
            case PLUSFOUR:
                graphicName += "draw4";
                break;
            case WISH:
                graphicName += "wild";
        }

        if (graphicName.length() < 1) {
            graphicName += "back";
        }

        this.graphic = graphicName;
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
                symbol == card.symbol &&
                Objects.equals(graphic, card.graphic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, symbol, graphic);
    }

    @Override
    public String toString() {
        return "Card{" +
                "color=" + color +
                ", symbol=" + symbol +
                ", graphic='" + graphic + '\'' +
                '}';
    }
}
