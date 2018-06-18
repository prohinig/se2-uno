package games.winchester.unodeluxe;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import games.winchester.unodeluxe.enums.CardColor;
import games.winchester.unodeluxe.enums.CardSymbol;
import games.winchester.unodeluxe.models.Card;
import games.winchester.unodeluxe.models.Hand;
import games.winchester.unodeluxe.utils.GameLogic;

import static org.junit.Assert.*;

public class GameLogicUnitTest {
    private Hand hand;
    private Card topCard;
    private CardColor activeColor;


    @Before
    public void setup() {
        hand = new Hand();
        topCard = new Card(CardColor.BLUE, CardSymbol.SEVEN);
        activeColor = topCard.getColor();
    }

    @Test
    public void hasPlayableCardTrueColorTest() {
        List<Card> temp = new ArrayList<>();
        temp.add(new Card(CardColor.RED, CardSymbol.PLUSTWO));
        temp.add(new Card(CardColor.GREEN, CardSymbol.THREE));
        temp.add(new Card(CardColor.BLUE, CardSymbol.EIGHT));
        hand.addCards(temp);

        assertTrue(GameLogic.hasPlayableCard(hand, activeColor, topCard));
    }

    @Test
    public void hasPlayableCardTrueSymbolTest() {
        List<Card> temp = new ArrayList<>();
        temp.add(new Card(CardColor.RED, CardSymbol.PLUSTWO));
        temp.add(new Card(CardColor.GREEN, CardSymbol.THREE));
        temp.add(new Card(CardColor.GREEN, CardSymbol.SEVEN));
        hand.addCards(temp);

        assertTrue(GameLogic.hasPlayableCard(hand, activeColor, topCard));
    }

    @Test
    public void hasPlayableCardFalseTest() {
        List<Card> temp = new ArrayList<>();
        temp.add(new Card(CardColor.RED, CardSymbol.PLUSTWO));
        temp.add(new Card(CardColor.GREEN, CardSymbol.THREE));
        temp.add(new Card(CardColor.YELLOW, CardSymbol.EIGHT));
        hand.addCards(temp);

        assertFalse(GameLogic.hasPlayableCard(hand, activeColor, topCard));
    }

    @Test
    public void hasPlayableCardBlackCardTest() {
        List<Card> temp = new ArrayList<>();
        temp.add(new Card(CardColor.BLACK, CardSymbol.WISH));
        hand.addCards(temp);

        assertTrue(GameLogic.hasPlayableCard(hand, activeColor, topCard));
    }

    @Test
    public void isPlayableCardTrueColorTest() {
        List<Card> temp = new ArrayList<>();
        temp.add(new Card(CardColor.RED, CardSymbol.PLUSTWO));
        temp.add(new Card(CardColor.GREEN, CardSymbol.THREE));
        temp.add(new Card(CardColor.BLUE, CardSymbol.EIGHT));
        hand.addCards(temp);

        assertTrue(GameLogic.isPlayableCard(hand.getCards().get(2), hand, topCard, activeColor));
    }

    @Test
    public void isPlayableCardTrueSymbolTest() {
        List<Card> temp = new ArrayList<>();
        temp.add(new Card(CardColor.RED, CardSymbol.PLUSTWO));
        temp.add(new Card(CardColor.GREEN, CardSymbol.THREE));
        temp.add(new Card(CardColor.RED, CardSymbol.SEVEN));
        hand.addCards(temp);

        assertTrue(GameLogic.isPlayableCard(hand.getCards().get(2), hand, topCard, activeColor));
    }

    @Test
    public void isPlayableCardFalseTest() {
        List<Card> temp = new ArrayList<>();
        temp.add(new Card(CardColor.RED, CardSymbol.ZERO));
        temp.add(new Card(CardColor.GREEN, CardSymbol.THREE));
        temp.add(new Card(CardColor.BLUE, CardSymbol.EIGHT));
        hand.addCards(temp);

        assertFalse(GameLogic.isPlayableCard(hand.getCards().get(0), hand, topCard, activeColor));
    }

    @Test
    public void isPlayableCardPlusFourTrueTest() {
        List<Card> temp = new ArrayList<>();
        temp.add(new Card(CardColor.RED, CardSymbol.PLUSTWO));
        temp.add(new Card(CardColor.GREEN, CardSymbol.SEVEN));
        temp.add(new Card(CardColor.BLACK, CardSymbol.PLUSFOUR));
        hand.addCards(temp);

        assertTrue(GameLogic.isPlayableCard(hand.getCards().get(2), hand, topCard, activeColor));
    }

    @Test
    public void isPlayableCardPlusFourFalseTest() {
        List<Card> temp = new ArrayList<>();
        temp.add(new Card(CardColor.BLUE, CardSymbol.EIGHT));
        temp.add(new Card(CardColor.GREEN, CardSymbol.THREE));
        temp.add(new Card(CardColor.BLACK, CardSymbol.PLUSFOUR));
        hand.addCards(temp);

        assertFalse(GameLogic.isPlayableCard(hand.getCards().get(2), hand, topCard, activeColor));
    }
}
