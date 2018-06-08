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
    private Card cardDisappeared;
    private Player accusedCheating;
    private CardColor activeColor;
    private boolean reverse;

    public Turn(){
        this.cardsDrawn = 0;
        this.activePlayer = 0;
        this.cardPlayed = null;
        this.cardDisappeared = null;
        this.accusedCheating = null;
        this.activeColor = null;
        this.reverse = false;
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

    public Card getCardDisappeared() {
        return cardDisappeared;
    }

    public Player getAccusedCheating() {
        return accusedCheating;
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
}
