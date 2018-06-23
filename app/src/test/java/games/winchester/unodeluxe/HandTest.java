package games.winchester.unodeluxe;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import games.winchester.unodeluxe.enums.CardColor;
import games.winchester.unodeluxe.enums.CardSymbol;
import games.winchester.unodeluxe.models.Card;
import games.winchester.unodeluxe.models.Hand;

import static org.junit.Assert.assertEquals;

public class HandTest {

    @Test
    public void removeFromHand(){
        Card card = new Card(CardColor.GREEN, CardSymbol.SKIP);

        Hand hand = new Hand();
        hand.addCards(Collections.singletonList(card));
        hand.removeCard(card);

        assertEquals(0, hand.getCards().size());
    }
}
