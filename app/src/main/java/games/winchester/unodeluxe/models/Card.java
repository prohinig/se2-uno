package games.winchester.unodeluxe.models;

import java.io.Serializable;

import games.winchester.unodeluxe.enums.CardColor;
import games.winchester.unodeluxe.enums.CardSymbol;

/**
 * Created by christianprohinig on 10.04.18.
 */

public class Card implements Serializable{
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
}
