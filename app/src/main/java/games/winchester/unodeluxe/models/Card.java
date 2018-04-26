package games.winchester.unodeluxe.models;

import android.support.annotation.NonNull;

import java.util.Comparator;

import games.winchester.unodeluxe.enums.CardColor;
import games.winchester.unodeluxe.enums.CardSymbol;

/**
 * Created by christianprohinig on 10.04.18.
 */

public class Card {
    private CardColor color;
    private CardSymbol symbol;
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
        String graphic = "";
        switch (color) {
            case RED:
                graphic += "r";
                break;
            case YELLOW:
                graphic += "y";
                break;
            case GREEN:
                graphic += "g";
                break;
            case BLUE:
                graphic += "b";
                break;
        }

        switch (symbol) {
            case ZERO:
                graphic += "0";
                break;
            case ONE:
                graphic += "1";
                break;
            case TWO:
                graphic += "2";
                break;
            case THREE:
                graphic += "3";
                break;
            case FOUR:
                graphic += "4";
                break;
            case FIVE:
                graphic += "5";
                break;
            case SIX:
                graphic += "6";
                break;
            case SEVEN:
                graphic += "7";
                break;
            case EIGHT:
                graphic += "8";
                break;
            case NINE:
                graphic += "9";
                break;
            case PLUSTWO:
                graphic += "d2";
                break;
            case SKIP:
                graphic += "s";
                break;
            case REVERSE:
                graphic += "r";
                break;
            case PLUSFOUR:
                graphic += "draw4";
                break;
            case WISH:
                graphic += "wild";
        }

        if (1 > graphic.length()) {
            graphic += "back";
        }

        this.graphic = graphic;
    }
}
