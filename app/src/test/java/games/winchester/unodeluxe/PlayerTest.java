package games.winchester.unodeluxe;

import org.junit.Test;

import games.winchester.unodeluxe.models.Player;

import static org.junit.Assert.assertEquals;

public class PlayerTest {

    @Test
    public void testPlayer(){
        Player player = new Player("Name");

        assertEquals("Name", player.getName());
        assertEquals(0, player.getHand().getCards().size());
        assertEquals(false, player.hasCheated());
        assertEquals(false, player.isAccuseable());

        player.setAccuseable(true);
        player.setCheated(true);

        assertEquals(true, player.hasCheated());
        assertEquals(true, player.isAccuseable());
    }

}
