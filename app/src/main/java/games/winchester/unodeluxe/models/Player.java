package games.winchester.unodeluxe.models;

import java.util.ArrayList;

import games.winchester.unodeluxe.models.Card;
import games.winchester.unodeluxe.models.Hand;

public class Player {

    private String name;
    private Hand hand;

    public Player(String name){
        this.name = name;
        this.hand = new Hand();
    }

    public Hand getHand (){
        return this.hand;
    }

    public void addCards(ArrayList<Card> cards){
        this.hand.addCards(cards);
    }
}
