package games.winchester.unodeluxe.models;

import java.io.Serializable;
import java.util.List;

/**
 * Represents a single player
 */
public class Player implements Serializable{

    /**
     * The players name
     */
    private String name;
    /**
     * Current hand of the player
     */
    private Hand hand;
    /**
     * Shows if player has cheated
     */
    private boolean cheated;
    /**
     * States if player is accuseable for cheating
     */
    private boolean accuseable;
    /**
     * number of penalty cards this player has to draw
     */
    private int penaltyCards;

    /**
     * Create a new player
     * @param name The players name
     */
    public Player(String name){
        this.name = name;
        this.hand = new Hand();
        this.cheated = false;
        this.accuseable = false;
        this.penaltyCards = 0;
    }

    /**
     * Get the players current hand
     * @return The players current Hand
     */
    public Hand getHand (){
        return this.hand;
    }

    /**
     * Add cards to the players hand
     * @param cards Cards to add to the hand
     */
    public void addCards(List<Card> cards){
        this.hand.addCards(cards);
    }

    /**
     * Get the players name
     * @return The players name
     */
    public String getName(){
        return this.name;
    }

    /**
     * Get information if player has cheated
     * @return true if player has cheated, otherwise false
     */
    public boolean hasCheated() {
        return cheated;
    }

    /**
     * If player cheated, set variable to true;
     * @param hasCheated
     */
    public void setCheated(boolean hasCheated) {
        this.cheated = hasCheated;
    }

    /**
     * Get if player is accuseable
     * @return if player is accuseable
     */
    public boolean isAccuseable() {
        return accuseable;
    }

    /**
     * Set if player is accuseable
     * @param isAccusable
     */
    public void setAccuseable(boolean isAccusable) {
        this.accuseable = isAccusable;
    }

    /**
     * returns how many penalty cards this player has to draw
     * @return number of penalty cards a player has to draw
     */
    public int getPenaltyCards() {
        return penaltyCards;
    }

    /**
     * adds number of penalty cards to this player
     * @param penaltyCards
     */
    public void addPenaltyCards(int penaltyCards) {
        this.penaltyCards += penaltyCards;
    }

    /**
     * decrements number of penalty cards to draw by one
     */
    public void decrementPenaltyCards() {
        if(this.penaltyCards > 0) {
            this.penaltyCards--;
        }
    }
}
