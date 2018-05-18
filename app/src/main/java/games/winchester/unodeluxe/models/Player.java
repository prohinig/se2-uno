package games.winchester.unodeluxe.models;

import java.io.Serializable;
import java.util.List;

/**
 * Represents a single player
 */
public class Player implements Serializable{

    /**
     * The playes name
     */
    private String name;
    /**
     * Current hand of the player
     */
    private Hand hand;

    /**
     * Create a new player
     * @param name The players name
     */
    public Player(String name){
        this.name = name;
        this.hand = new Hand();
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

}
