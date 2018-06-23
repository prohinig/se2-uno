package games.winchester.unodeluxe.messages;

import at.laubi.network.messages.Message;
import games.winchester.unodeluxe.enums.CardColor;
import games.winchester.unodeluxe.enums.Direction;
import games.winchester.unodeluxe.models.Card;

/**
 * Message which is sent every turn
 */
public class Turn implements Message {
    private int cardsDrawn;
    private int activePlayer;

    private Card cardPlayed;
    private CardColor activeColor;
    private Direction direction = Direction.NORMAL;
    private int cardsToDraw;
    private String playerName;
    private boolean shakeRequired;

    public Turn() {
        this.cardsDrawn = 0;
        this.activePlayer = 0;
        this.cardPlayed = null;
        this.playerName = null;
        this.shakeRequired = false;

        this.activeColor = null;
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

    public Direction getDirection() {
        return direction;
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

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public void setCardsToDraw(int count) {
        this.cardsToDraw = count;
    }

    public int getCardsToDraw() {
        return this.cardsToDraw;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public boolean isShakeRequired() {
        return shakeRequired;
    }

    public void setShakeRequired(boolean shakeRequired) {
        this.shakeRequired = shakeRequired;
    }
}
