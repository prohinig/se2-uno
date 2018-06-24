package games.winchester.unodeluxe;

import org.junit.Test;

import games.winchester.unodeluxe.enums.CardColor;
import games.winchester.unodeluxe.enums.CardSymbol;
import games.winchester.unodeluxe.models.Card;
import games.winchester.unodeluxe.utils.CardGraphicResolver;

import static org.junit.Assert.assertEquals;

public class CardGraphicResolverTest {

    @Test
    public void testCardNameResolver() {
        assertEquals("r0", CardGraphicResolver.getResourceNameForCard(new Card(CardColor.RED, CardSymbol.ZERO)));
        assertEquals("b1", CardGraphicResolver.getResourceNameForCard(new Card(CardColor.BLUE, CardSymbol.ONE)));
        assertEquals("y2", CardGraphicResolver.getResourceNameForCard(new Card(CardColor.YELLOW, CardSymbol.TWO)));
        assertEquals("g3", CardGraphicResolver.getResourceNameForCard(new Card(CardColor.GREEN, CardSymbol.THREE)));
        assertEquals("r4", CardGraphicResolver.getResourceNameForCard(new Card(CardColor.RED, CardSymbol.FOUR)));
        assertEquals("r5", CardGraphicResolver.getResourceNameForCard(new Card(CardColor.RED, CardSymbol.FIVE)));
        assertEquals("r6", CardGraphicResolver.getResourceNameForCard(new Card(CardColor.RED, CardSymbol.SIX)));
        assertEquals("r7", CardGraphicResolver.getResourceNameForCard(new Card(CardColor.RED, CardSymbol.SEVEN)));
        assertEquals("r8", CardGraphicResolver.getResourceNameForCard(new Card(CardColor.RED, CardSymbol.EIGHT)));
        assertEquals("r9", CardGraphicResolver.getResourceNameForCard(new Card(CardColor.RED, CardSymbol.NINE)));
        assertEquals("wild", CardGraphicResolver.getResourceNameForCard(new Card(CardColor.BLACK, CardSymbol.WISH)));
        assertEquals("draw4", CardGraphicResolver.getResourceNameForCard(new Card(CardColor.BLACK, CardSymbol.PLUSFOUR)));
        assertEquals("rd2", CardGraphicResolver.getResourceNameForCard(new Card(CardColor.RED, CardSymbol.PLUSTWO)));
        assertEquals("rs", CardGraphicResolver.getResourceNameForCard(new Card(CardColor.RED, CardSymbol.SKIP)));
        assertEquals("rr", CardGraphicResolver.getResourceNameForCard(new Card(CardColor.RED, CardSymbol.REVERSE)));
        assertEquals("0", CardGraphicResolver.getResourceNameForCard(new Card(CardColor.BLACK, CardSymbol.ZERO)));
        assertEquals("shake", CardGraphicResolver.getResourceNameForCard(new Card(CardColor.BLACK, CardSymbol.SHAKE)));
    }
}
