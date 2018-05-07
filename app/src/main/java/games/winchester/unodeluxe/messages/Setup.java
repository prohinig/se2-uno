package games.winchester.unodeluxe.messages;

import java.util.ArrayList;

import at.laubi.network.messages.Message;
import games.winchester.unodeluxe.enums.CardColor;
import games.winchester.unodeluxe.models.Deck;
import games.winchester.unodeluxe.models.Game;
import games.winchester.unodeluxe.models.Player;
import games.winchester.unodeluxe.models.Stack;

public class Setup implements Message {
    //Setup object is sent from master to all connected device when game starts
    public Deck deck;
    public Stack stack;
    public CardColor activeColor;
    public ArrayList<Player> players;
    public int activePlayer;

    public Setup(Game game) {
        deck = game.getDeck();
        stack = game.getStack();
        activeColor = game.getActiveColor();
        players = game.getPlayers();
        activePlayer = game.getActivePlayer();
    }

}
