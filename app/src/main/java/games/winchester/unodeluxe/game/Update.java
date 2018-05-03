package games.winchester.unodeluxe.game;

import java.io.Serializable;

import games.winchester.unodeluxe.enums.CardColor;
import games.winchester.unodeluxe.models.Card;
import games.winchester.unodeluxe.models.Player;

public class Update implements Serializable {
    private int cardsDrawn;
    private Card cardPlayed;
    private Card cardDisappeared;
    private Player accusedCheating;
    private CardColor colorWished;

    public Update(){
        this.cardsDrawn = 0;
        this.cardPlayed = null;
        this.cardDisappeared = null;
        this.accusedCheating = null;
        this.colorWished = null;
    }

    public int getCardsDrawn() {
        return cardsDrawn;
    }

    public void setCardsDrawn(int cardsDrawn) {
        this.cardsDrawn = cardsDrawn;
    }

    public Card getCardPlayed() {
        return cardPlayed;
    }

    public void setCardPlayed(Card cardPlayed) {
        this.cardPlayed = cardPlayed;
    }

    public Card getCardDisappeared() {
        return cardDisappeared;
    }

    public void setCardDisappeared(Card cardDisappeared) {
        this.cardDisappeared = cardDisappeared;
    }

    public Player getAccusedCheating() {
        return accusedCheating;
    }

    public void setAccusedCheating(Player accusedCheating) {
        this.accusedCheating = accusedCheating;
    }

    public CardColor getColorWished() {
        return colorWished;
    }

    public void setColorWished(CardColor colorWished) {
        this.colorWished = colorWished;
    }
}
