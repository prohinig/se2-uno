package games.winchester.unodeluxe.messages;

import at.laubi.network.messages.Message;
import games.winchester.unodeluxe.enums.CardColor;
import games.winchester.unodeluxe.models.Card;
import games.winchester.unodeluxe.models.Player;

public class Turn implements Message {
    public int cardsDrawn;
    public int activePlayer;

    public Card cardPlayed;
    public Card cardDisappeared;
    public Player accusedCheating;
    public CardColor activeColor;
    public boolean reverse;

    public Turn(){
        this.cardsDrawn = 0;
        this.activePlayer = 0;
        this.cardPlayed = null;
        this.cardDisappeared = null;
        this.accusedCheating = null;
        this.activeColor = null;
        this.reverse = false;
    }

}
