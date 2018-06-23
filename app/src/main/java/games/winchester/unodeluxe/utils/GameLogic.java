package games.winchester.unodeluxe.utils;

import games.winchester.unodeluxe.enums.CardColor;
import games.winchester.unodeluxe.enums.CardSymbol;
import games.winchester.unodeluxe.models.Card;
import games.winchester.unodeluxe.models.Hand;

public class GameLogic {
    /**
     * Private constructor for utility class
     */
    private GameLogic() {}

    //checks if player has a playable card
    public static boolean hasPlayableCard(Hand hand, CardColor activeColor, Card topCard) {
        for (int i = 0; i < hand.getCards().size(); i++) {
            if (hand.getCards().get(i).getColor() == activeColor ||
                    hand.getCards().get(i).getSymbol() == topCard.getSymbol() ||
                    hand.getCards().get(i).getColor() == CardColor.BLACK) {
                return true;
            }
        }

        return false;
    }


    //checks if player is allowed to play clicked card
    public static boolean isPlayableCard(Card chosenCard, Hand hand, Card topCard, CardColor activeColor) {
        //1. check if player has cards of active color in hand (is needed for +4 card)
        boolean hasCurrentColor = false;
        for (int i = 0; i < hand.getCards().size(); i++) {
            if (hand.getCards().get(i).getColor() == activeColor) {
                hasCurrentColor = true;
                break;
            }
        }

        //2. check if chosen card is allowed to be played
        if (chosenCard.getColor() == activeColor) {
            return true;
        } else if (chosenCard.getSymbol() == topCard.getSymbol()) {
            return true;
        } else if (chosenCard.getColor() == CardColor.BLACK) {
            return chosenCard.getSymbol() == CardSymbol.SHAKE || chosenCard.getSymbol() == CardSymbol.WISH || chosenCard.getSymbol() == CardSymbol.PLUSFOUR && !hasCurrentColor;
        }

        return false;
    }
}
