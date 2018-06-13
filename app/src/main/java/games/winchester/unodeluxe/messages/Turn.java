package games.winchester.unodeluxe.messages;

import at.laubi.network.messages.Message;
import games.winchester.unodeluxe.enums.CardColor;
import games.winchester.unodeluxe.models.Card;
import games.winchester.unodeluxe.models.Player;

/**
 * Message which is sent every turn
 */
public class Turn implements Message {
    private int cardsDrawn;
    private int activePlayer;

    private Card cardPlayed;
    private CardColor activeColor;
    private boolean reverse;
    private int cardsToDraw;

    public Turn(){
        this.cardsDrawn = 0;
        this.activePlayer = 0;
        this.cardPlayed = null;

        this.activeColor = null;
        this.reverse = false;
        this.cardsToDraw = 0;
    }

    public int getCardsDrawn() {
        return cardsDrawn;
    }

    public int getActivePlayer() {
        return activePlayer;
    }

    public Card getCardPlayed() {
        return cardPlayed;
    }

    public CardColor getActiveColor() {
        return activeColor;
    }

    public boolean isReverse() {
        return reverse;
    }

    public void setCardsDrawn(int cardsDrawn) {
        this.cardsDrawn = cardsDrawn;
    }

    public void setActivePlayer(int activePlayer) {
        this.activePlayer = activePlayer;
    }

    public void setCardPlayed(Card cardPlayed) {
        this.cardPlayed = cardPlayed;
    }

    public void setActiveColor(CardColor activeColor) {
        this.activeColor = activeColor;
    }

    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }

    public void setCardsToDraw(int count) {
        this.cardsToDraw = count;
    }

    public int getCardsToDraw() {
        return this.cardsToDraw;
    }
}
