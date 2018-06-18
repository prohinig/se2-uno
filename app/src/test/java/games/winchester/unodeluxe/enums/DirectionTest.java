package games.winchester.unodeluxe.enums;

import org.junit.Test;

import static games.winchester.unodeluxe.enums.Direction.*;
import static org.junit.Assert.assertEquals;

public class DirectionTest {

    @Test
    public void testDirectionReverse(){
        assertEquals(REVERSE, NORMAL.reverseDirection());
        assertEquals(NORMAL, REVERSE.reverseDirection());
    }

    @Test
    public void testNextPlayerPos(){
        assertEquals(1, NORMAL.getNextPlayerPos(0, 2));
        assertEquals(1, REVERSE.getNextPlayerPos(0, 2));
        assertEquals(0, NORMAL.getNextPlayerPos(2, 3));
        assertEquals(1, REVERSE.getNextPlayerPos(2, 3));
        assertEquals(1, NORMAL.getNextPlayerPos(0, 2));
    }
}
