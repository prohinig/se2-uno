package games.winchester.unodeluxe.models;

import java.io.Serializable;

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
    private final Hand hand = new Hand();

    /**
     * Shows if player has cheated
     */
    private boolean hasCheated = false;

    /**
     * States if player is accuseable for cheating
     */
    private boolean isAccusable = false;

    /**
     * Create a new player
     * @param playerName The players name
     */
    public Player(String playerName) {
        name = playerName;
    }

    /**
     * Get the players current hand
     * @return The players current Hand
     */
    public Hand getHand (){
        return hand;
    }

    /**
     * Get the players name
     * @return The players name
     */
    public String getName(){
        return name;
    }

    /**
     * Get information if player has cheated
     * @return true if player has cheated, otherwise false
     */
    public boolean hasCheated() {
        return hasCheated;
    }

    /**
     * If player cheated, set variable to true;
     * @param cheated If the player cheated
     */
    public void setCheated(boolean cheated) {
        hasCheated = cheated;
    }

    /**
     * Get if player is accuseable
     * @return if player is accuseable
     */
    public boolean isAccuseable() {
        return isAccusable;
    }

    /**
     * Set if player is accuseable
     * @param accusable If the player is accusable
     */
    public void setAccuseable(boolean accusable) {
        isAccusable = accusable;
    }
}
