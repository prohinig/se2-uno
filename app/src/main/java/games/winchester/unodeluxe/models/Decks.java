package games.winchester.unodeluxe.models;

import java.util.ArrayList;
import java.util.List;

import games.winchester.unodeluxe.enums.CardColor;
import games.winchester.unodeluxe.enums.CardSymbol;

import static games.winchester.unodeluxe.enums.CardColor.*;
import static games.winchester.unodeluxe.enums.CardSymbol.*;

public class Decks {
    private static final CardColor[] COLORS = { RED, GREEN, BLUE, YELLOW };
    private static final List<Card> STANDARD_DECK;
    private static final List<Card> CUSTOM_DECK;

    private Decks() {}

    static {
        List<Card> cards = new ArrayList<>(112);

        cards.addAll(generate(BLACK, PLUSFOUR, 4));
        cards.addAll(generate(BLACK, WISH, 4));
        cards.addAll(generateStandardColorDeck());

        STANDARD_DECK = new ArrayList<>(cards);

        cards.addAll(generate(BLACK, SHAKE, 4));
        CUSTOM_DECK = cards;
    }


    public static Deck getStandardDeck(){
        return new Deck(STANDARD_DECK);
    }

    public static Deck getCustomDeck() {
        return new Deck(CUSTOM_DECK);
    }

    private static List<Card> generate(CardColor color, CardSymbol symbol, int times){
        List<Card> result = new ArrayList<>(times);

        for(int i = 0; i < times; i++){
            result.add(new Card(color, symbol));
        }

        return result;
    }

    private static List<Card> generateStandardColorDeck() {
        List<Card> cards = new ArrayList<>(100);

        for(CardColor color: COLORS) {
            cards.addAll(generateColorCards(color));
        }

        return cards;
    }

    private static List<Card> generateColorCards(CardColor color){
        List<Card> results = new ArrayList<>(25);

        for(CardSymbol symbol : CardSymbol.values()){

            switch(symbol){
                case ZERO:
                    results.add(new Card(color, symbol));
                    break;
                case ONE:
                case TWO:
                case THREE:
                case FOUR:
                case FIVE:
                case SIX:
                case SEVEN:
                case EIGHT:
                case NINE:
                case PLUSTWO:
                case SKIP:
                case REVERSE:
                    results.addAll(generate(color, symbol, 2));
                    break;
                default:
                    break;
            }
        }

        return results;
    }

}
