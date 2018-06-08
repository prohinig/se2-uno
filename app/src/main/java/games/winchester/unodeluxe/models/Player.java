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
     * States if player is blameable for cheating
     */
    private boolean blameable;

    /**
     * Create a new player
     * @param name The players name
     */
    public Player(String name){
        this.name = name;
        this.hand = new Hand();
        this.cheated = false;
        this.blameable = false;
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
     * Get if player is blameable
     * @return if player is blameable
     */
    public boolean isBlameable() {
        return blameable;
    }

    /**
     * Set if player is blameable
     * @param isBlameable
     */
    public void setBlameable(boolean isBlameable) {
        this.blameable = isBlameable;
    }
}
