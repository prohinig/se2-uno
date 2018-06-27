package games.winchester.unodeluxe.enums;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CardSymbolTest {

    @Test
    public void testGetAction(){
        assertEquals(Action.DRAWTWO, CardSymbol.PLUSTWO.getAction());
        assertEquals(Action.REVERSE, CardSymbol.REVERSE.getAction());
        assertEquals(Action.SKIP, CardSymbol.SKIP.getAction());
        assertEquals(Action.WISH, CardSymbol.WISH.getAction());
        assertEquals(Action.DRAWFOUR, CardSymbol.PLUSFOUR.getAction());
        assertEquals(Action.SHAKE, CardSymbol.SHAKE.getAction());
        assertEquals(Action.NONE, CardSymbol.NINE.getAction());
    }
    
    @Test
    public void testIsAction(){
        assertEquals(true, CardSymbol.PLUSTWO.isActionSymbol());
        assertEquals(true, CardSymbol.REVERSE.isActionSymbol());
        assertEquals(true, CardSymbol.SKIP.isActionSymbol());
        assertEquals(true, CardSymbol.WISH.isActionSymbol());
        assertEquals(true, CardSymbol.PLUSFOUR.isActionSymbol());
        assertEquals(false, CardSymbol.NINE.isActionSymbol());
    }
}
