package games.winchester.unodeluxe.utils;

import games.winchester.unodeluxe.enums.Action;
import games.winchester.unodeluxe.enums.CardColor;
import games.winchester.unodeluxe.enums.CardSymbol;
import games.winchester.unodeluxe.models.Card;
import games.winchester.unodeluxe.models.Hand;

public class GameLogic {

    static boolean actionHandled = true;

    public static boolean canPlayCard(Card chosenCard, Hand hand, Card topCard, CardColor currentColor) {
        //check if player has cards of active color in hand (is needed for +4 card)
        boolean hasCurrentColor = false;
        for (int i = 0; i < hand.getCards().size(); i++) {
            if(hand.getCards().get(i).getColor() == currentColor) {
                hasCurrentColor = true;
                break;
            }
        }

        //check if chosen card is allowed to be played
        if(chosenCard.getColor() == currentColor) {
            checkCardType(chosenCard);
            return true;
        } else if(chosenCard.getSymbol() == topCard.getSymbol()) {
            checkCardType(chosenCard);
            return true;
        } else if(chosenCard.getColor() == CardColor.BLACK) {
            if(chosenCard.getSymbol() == CardSymbol.WISH) {
                return true;
            } else if(chosenCard.getSymbol() == CardSymbol.PLUSFOUR && !hasCurrentColor) {
                actionHandled = false;
                return true;
            }
        }

        return false;
    }

    //checks if an action card has been played
    public static void checkCardType(Card c) {
        if(c.getSymbol() == CardSymbol.SKIP ||
                c.getSymbol() == CardSymbol.PLUSTWO ||
                c.getSymbol() == CardSymbol.REVERSE) {
            actionHandled = false;
        }
    }

    public static Action actionRequired(Card topCard) {
        if(!actionHandled) {
            actionHandled = true;
            switch (topCard.getSymbol()) {
                case PLUSTWO:
                    return Action.DRAWTWO;
                case REVERSE:
                    return Action.REVERSE;
                case SKIP:
                    return Action.SKIP;
                case WISH:
                    return Action.NONE;
                case PLUSFOUR:
                    return Action.DRAWFOUR;
                default:
                    return Action.NONE;
            }
        }
        return Action.NONE;
    }

}
